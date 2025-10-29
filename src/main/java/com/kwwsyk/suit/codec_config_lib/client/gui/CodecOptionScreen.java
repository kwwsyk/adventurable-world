package com.kwwsyk.suit.codec_config_lib.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.kwwsyk.suit.codec_config_lib.api.CodecConfigApi;
import com.kwwsyk.suit.codec_config_lib.api.Constraints;
import com.kwwsyk.suit.codec_config_lib.api.EditorProvider;
import com.kwwsyk.suit.codec_config_lib.api.EditorProviderRegistry;
import com.kwwsyk.suit.codec_config_lib.client.Constants;
import com.kwwsyk.suit.codec_config_lib.client.gui.widget.OptionEntry;
import com.kwwsyk.suit.codec_config_lib.core.codec.CodecIntrospector;
import com.kwwsyk.suit.codec_config_lib.core.codec.LifecycleUtil;
import com.kwwsyk.suit.codec_config_lib.core.json.JsonDiff;
import com.kwwsyk.suit.codec_config_lib.core.json.JsonSerde;
import com.kwwsyk.suit.codec_config_lib.core.schema.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic screen capable of rendering configuration editors derived from codec-driven schemas. The
 * screen is configured through the {@link Builder} fluent API which allows callers to provide
 * translations, tooltips, constraints, and custom editors. User-facing classes only need to supply
 * their config instance, codec, and optional callbacks while the library takes care of wiring the
 * UI.
 *
 * @param <T> logical type of the codec backing the screen.
 */
public class CodecOptionScreen<T> extends ParseCodecOptionScreen {

    private static final Constraints NO_CONSTRAINTS = new Constraints(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    private final Codec<T> codec;
    private final T originalConfig;
    private final Consumer<T> applyCallback;
    private final boolean showJsonPreview;
    private final Function<Node, Component> labelResolver;
    private final Function<Node, List<FormattedCharSequence>> tooltipResolver;
    private final Map<ValuePath, Constraints> constraints;
    private final Map<ValuePath, Predicate<String>> stringValidators;
    private final EditorProviderRegistry registry = new EditorProviderRegistry();
    private final Map<ValuePath, Object> edits = new LinkedHashMap<>();
    private final boolean codecUsesDynamicMap;

    private OptionSchema<T> schema;
    private T previewConfig;
    private MutableComponent lifecycleStatus = CommonComponents.EMPTY.copy();
    private MutableComponent errorStatus = CommonComponents.EMPTY.copy();
    private List<FormattedCharSequence> jsonPreviewTooltip = List.of();

    protected CodecOptionScreen(Builder<T> builder) {
        super(builder.lastScreen, builder.title);
        this.codec = builder.codec;
        this.originalConfig = builder.originalConfig;
        this.applyCallback = builder.applyCallback;
        this.showJsonPreview = builder.showJsonPreview;
        this.labelResolver = builder.createLabelResolver();
        this.tooltipResolver = builder.createTooltipResolver();
        this.constraints = Map.copyOf(builder.constraints);
        this.stringValidators = Map.copyOf(builder.stringValidators);
        this.codecUsesDynamicMap = CodecIntrospector.isDynamicMap(builder.codec);
        registerDefaultProviders();
        builder.customProviders.forEach(factory -> registry.register(factory.create(this)));
        captureSchema();
        refreshPreview();
    }

    private void registerDefaultProviders() {
        registry.register(new BooleanEditorProvider());
        registry.register(new NumberEditorProvider());
        registry.register(new StringEditorProvider());
    }

    private void captureSchema() {
        DataResult<OptionSchema<T>> result = CodecConfigApi.buildSchema(originalConfig, codec);
        this.schema = result.result().orElse(null);
        this.lifecycleStatus = Component.translatable("adv_option.codec.lifecycle", LifecycleUtil.lifecycleOrStable(result).toString());
        result.error().ifPresent(error -> this.errorStatus = Component.translatable("adv_option.codec.error", error.message()));
        if (this.schema == null) {
            this.errorStatus = Component.translatable("adv_option.codec.schema_missing");
        }
    }

    private void refreshPreview() {
        if (schema == null) {
            previewConfig = null;
            jsonPreviewTooltip = List.of();
            return;
        }
        errorStatus = CommonComponents.EMPTY.copy();
        DataResult<T> result = CodecConfigApi.applyEdits(schema, edits, codec);
        previewConfig = result.result().orElse(null);
        result.error().ifPresent(error -> errorStatus = Component.translatable("adv_option.codec.error", error.message()));
        JsonElement patched = CodecConfigApi.applyEditsToJson(schema, edits);
        JsonElement diff = JsonDiff.identity(patched);
        String pretty = JsonSerde.toPrettyString(diff);
        jsonPreviewTooltip = Minecraft.getInstance().font.split(Component.literal(pretty), 260);
    }

    @Override
    protected void populateOptions(OptionList list) {
        List<OptionEntry> entries = new ArrayList<>();
        if (schema == null) {
            entries.add(new OptionEntry.SubTitleEntry(this, Component.translatable("adv_option.codec.schema_missing"), null));
        } else {
            renderNode(schema.root(), entries);
        }
        if (showJsonPreview) {
            entries.add(new OptionEntry.EmptySpaceEntry(this));
            entries.add(new JsonPreviewEntry());
        }
        list.setEntries(entries);
    }

    private void renderNode(Node node, List<OptionEntry> entries) {
        switch (node.kind()) {
            case GROUP -> renderGroup((GroupNode) node, entries);
            case LIST -> entries.add(unsupported(node, "List"));
            case MAP -> entries.add(unsupported(node, "Map"));
            case OPTIONAL -> entries.add(unsupported(node, "Optional"));
            case EITHER -> entries.add(unsupported(node, "Either"));
            case DISPATCH -> entries.add(unsupported(node, "Dispatch"));
            case HOLDER -> entries.add(unsupported(node, "Holder"));
            case PROVIDER -> entries.add(unsupported(node, "Provider"));
            case NULL -> entries.add(unsupported(node, "Null"));
            default -> registry.resolve(node)
                    .filter(OptionEntry.class::isInstance)
                    .map(OptionEntry.class::cast)
                    .ifPresent(entries::add);
        }
    }

    private OptionEntry unsupported(Node node, String type) {
        return new OptionEntry.SubTitleEntry(this, Component.translatable("adv_option.codec.unsupported", type, node.path()), tooltipFor(node));
    }

    private void renderGroup(GroupNode group, List<OptionEntry> entries) {
        entries.add(new OptionEntry.SubTitleEntry(this, labelFor(group), tooltipFor(group)));
        for (Node child : group.orderedChildren()) {
            renderNode(child, entries);
        }
    }

    private Component labelFor(Node node) {
        return labelResolver.apply(node);
    }

    private List<FormattedCharSequence> tooltipFor(Node node) {
        List<FormattedCharSequence> tooltip = tooltipResolver.apply(node);
        return tooltip == null ? List.of() : tooltip;
    }

    private Constraints constraintsFor(ValuePath path) {
        return constraints.getOrDefault(path, NO_CONSTRAINTS);
    }

    private Predicate<String> stringValidator(ValuePath path) {
        return stringValidators.getOrDefault(path, value -> true);
    }

    private void updateEdits(ValuePath path, Object value, Object defaultValue) {
        if (Objects.equals(value, defaultValue)) {
            edits.remove(path);
        } else {
            edits.put(path, value);
        }
        refreshPreview();
    }

    @Override
    protected void onDone() {
        super.onDone();
        if (previewConfig != null) {
            applyCallback.accept(previewConfig);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int baseY = this.getDoneButton().getY() + this.getDoneButton().getHeight() + 6;
        guiGraphics.drawString(font, lifecycleStatus, 20, baseY, 0xFFFFFF, false);
        guiGraphics.drawString(font, errorStatus, 20, baseY + font.lineHeight + 3, 0xFF8888, false);
    }

    public boolean usesDynamicMapCodec() {
        return codecUsesDynamicMap;
    }

    private final class JsonPreviewEntry extends OptionEntry {

        JsonPreviewEntry() {
            super(CodecOptionScreen.this, jsonPreviewTooltip);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public List<FormattedCharSequence> tooltip() {
            return jsonPreviewTooltip;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            guiGraphics.drawString(font, Component.translatable("adv_option.codec.preview"), left, top, 0xFFFFFF, false);
            if (!jsonPreviewTooltip.isEmpty()) {
                int offset = 12;
                for (FormattedCharSequence sequence : jsonPreviewTooltip) {
                    guiGraphics.drawString(font, sequence, left, top + offset, 0xAAAAAA, false);
                    offset += 10;
                }
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public int getItemHeight() {
            int lines = jsonPreviewTooltip.size();
            int contentHeight = 12 + lines * 10;
            return Math.max(24, contentHeight);
        }
    }

    private final class BooleanEditorProvider implements EditorProvider {

        @Override
        public boolean supports(Node node) {
            return node instanceof LeafNode leaf && leaf.kind() == NodeKind.BOOL;
        }

        @Override
        public Optional<Object> create(Node node) {
            if (!(node instanceof LeafNode leaf)) {
                return Optional.empty();
            }
            boolean defaultValue = leaf.defaultValue().getAsBoolean();
            OptionEntry.BooleanConfigEntry entry = new OptionEntry.BooleanConfigEntry(
                    CodecOptionScreen.this,
                    labelFor(node),
                    tooltipFor(node),
                    defaultValue,
                    (cycleButton, value) -> updateEdits(node.path(), value, defaultValue)
            );
            return Optional.of(entry);
        }
    }

    private final class StringEditorProvider implements EditorProvider {

        @Override
        public boolean supports(Node node) {
            return node instanceof LeafNode leaf && leaf.kind() == NodeKind.STRING;
        }

        @Override
        public Optional<Object> create(Node node) {
            if (!(node instanceof LeafNode leaf)) {
                return Optional.empty();
            }
            String defaultValue = leaf.defaultValue().getAsString();
            OptionEntry.StringEntry entry = new OptionEntry.StringEntry(
                    CodecOptionScreen.this,
                    labelFor(node),
                    tooltipFor(node),
                    defaultValue,
                    stringValidator(node.path()),
                    value -> updateEdits(node.path(), value, defaultValue)
            );
            return Optional.of(entry);
        }
    }

    private final class NumberEditorProvider implements EditorProvider {

        @Override
        public boolean supports(Node node) {
            return node instanceof LeafNode leaf && leaf.kind() == NodeKind.NUMBER;
        }

        @Override
        public Optional<Object> create(Node node) {
            if (!(node instanceof LeafNode leaf)) {
                return Optional.empty();
            }
            Constraints constraints = constraintsFor(node.path());
            String raw = leaf.defaultValue().getAsString();
            NumberKind kind = determineNumberKind(raw);
            return Optional.of(createEntry(kind, node, constraints));
        }

        private OptionEntry.ConfigEntry createEntry(NumberKind kind, Node node, Constraints constraints) {
            LeafNode leaf = (LeafNode) node;
            switch (kind) {
                case LONG -> {
                    long defaultValue = leaf.defaultValue().getAsLong();
                    return new OptionEntry.LongEntry(CodecOptionScreen.this, labelFor(node), tooltipFor(node), defaultValue, constraints, value -> updateEdits(node.path(), value, defaultValue));
                }
                case DOUBLE -> {
                    double defaultValue = leaf.defaultValue().getAsDouble();
                    return new OptionEntry.DoubleEntry(CodecOptionScreen.this, labelFor(node), tooltipFor(node), defaultValue, constraints, value -> updateEdits(node.path(), value, defaultValue));
                }
                default -> {
                    int defaultValue = leaf.defaultValue().getAsInt();
                    return new OptionEntry.IntEntry(CodecOptionScreen.this, labelFor(node), tooltipFor(node), defaultValue, constraints, value -> updateEdits(node.path(), value, defaultValue));
                }
            }
        }

        private NumberKind determineNumberKind(String raw) {
            if (raw.contains(".") || raw.contains("e") || raw.contains("E")) {
                return NumberKind.DOUBLE;
            }
            try {
                long value = Long.parseLong(raw);
                if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
                    return NumberKind.LONG;
                }
                return NumberKind.INT;
            } catch (NumberFormatException ignored) {
                return NumberKind.DOUBLE;
            }
        }
    }

    private enum NumberKind {
        INT,
        LONG,
        DOUBLE
    }

    /**
     * Fluent builder used to configure {@link CodecOptionScreen} instances.
     *
     * @param <T> logical codec type.
     */
    public static final class Builder<T> {

        private final Screen lastScreen;
        private final Component title;
        private final T originalConfig;
        private final Codec<T> codec;

        private Consumer<T> applyCallback = value -> {};
        private boolean showJsonPreview = true;
        private String labelPrefix;
        private String tooltipPrefix;
        private final Map<ValuePath, Component> labelOverrides = new HashMap<>();
        private final Map<ValuePath, List<FormattedCharSequence>> tooltipOverrides = new HashMap<>();
        private final Map<ValuePath, Constraints> constraints = new HashMap<>();
        private final Map<ValuePath, Predicate<String>> stringValidators = new HashMap<>();
        private final List<EditorProviderFactory<T>> customProviders = new ArrayList<>();

        private Builder(Screen lastScreen, Component title, T originalConfig, Codec<T> codec) {
            this.lastScreen = Objects.requireNonNull(lastScreen, "lastScreen");
            this.title = Objects.requireNonNull(title, "title");
            this.originalConfig = Objects.requireNonNull(originalConfig, "originalConfig");
            this.codec = Objects.requireNonNull(codec, "codec");
        }

        public static <T> Builder<T> create(Screen lastScreen, Component title, T originalConfig, Codec<T> codec) {
            return new Builder<>(lastScreen, title, originalConfig, codec);
        }

        public Builder<T> withLabelPrefix(String prefix) {
            this.labelPrefix = Objects.requireNonNull(prefix, "prefix");
            return this;
        }

        public Builder<T> withTooltipPrefix(String prefix) {
            this.tooltipPrefix = Objects.requireNonNull(prefix, "prefix");
            return this;
        }

        public Builder<T> withLabel(ValuePath path, Component label) {
            Objects.requireNonNull(path, "path");
            this.labelOverrides.put(path, Objects.requireNonNull(label, "label"));
            return this;
        }

        public Builder<T> withTooltip(ValuePath path, Component tooltip) {
            Objects.requireNonNull(path, "path");
            Objects.requireNonNull(tooltip, "tooltip");
            this.tooltipOverrides.put(path, List.of(tooltip.getVisualOrderText()));
            return this;
        }

        public Builder<T> withTooltip(ValuePath path, List<FormattedCharSequence> tooltip) {
            Objects.requireNonNull(path, "path");
            this.tooltipOverrides.put(path, ImmutableList.copyOf(tooltip));
            return this;
        }

        public Builder<T> withConstraints(ValuePath path, Constraints constraints) {
            Objects.requireNonNull(path, "path");
            this.constraints.put(path, Objects.requireNonNull(constraints, "constraints"));
            return this;
        }

        public Builder<T> withStringValidator(ValuePath path, Predicate<String> validator) {
            Objects.requireNonNull(path, "path");
            this.stringValidators.put(path, Objects.requireNonNull(validator, "validator"));
            return this;
        }

        public Builder<T> withEditor(EditorProviderFactory<T> factory) {
            this.customProviders.add(Objects.requireNonNull(factory, "factory"));
            return this;
        }

        public <E extends Enum<E>> Builder<T> withEnum(ValuePath path,
                                                       E[] values,
                                                       Function<E, Component> valueLabel,
                                                       Function<E, String> serializer) {
            Objects.requireNonNull(path, "path");
            Objects.requireNonNull(values, "values");
            Objects.requireNonNull(valueLabel, "valueLabel");
            Objects.requireNonNull(serializer, "serializer");
            this.customProviders.add(screen -> new EnumEditorProvider<>(screen, path, values, valueLabel, serializer));
            return this;
        }

        public <E extends Enum<E>> Builder<T> withEnum(ValuePath path,
                                                       E[] values,
                                                       Function<E, Component> valueLabel) {
            return withEnum(path, values, valueLabel, value -> value.name().toLowerCase(Locale.ROOT));
        }

        public Builder<T> onApply(Consumer<T> applyCallback) {
            this.applyCallback = Objects.requireNonNull(applyCallback, "applyCallback");
            return this;
        }

        public Builder<T> showJsonPreview(boolean showJsonPreview) {
            this.showJsonPreview = showJsonPreview;
            return this;
        }

        public CodecOptionScreen<T> build() {
            return new CodecOptionScreen<>(this);
        }

        private Function<Node, Component> createLabelResolver() {
            return node -> {
                Component override = labelOverrides.get(node.path());
                if (override != null) {
                    return override;
                }
                if (labelPrefix != null) {
                    String key = pathKey(node.path());
                    if (key.isEmpty()) {
                        return Component.translatable(labelPrefix);
                    }
                    return Component.translatable(labelPrefix + "." + key);
                }
                return Component.literal(defaultLabel(node.path()));
            };
        }

        private Function<Node, List<FormattedCharSequence>> createTooltipResolver() {
            return node -> {
                List<FormattedCharSequence> override = tooltipOverrides.get(node.path());
                if (override != null) {
                    return override;
                }
                if (tooltipPrefix != null) {
                    String key = pathKey(node.path());
                    if (!key.isEmpty()) {
                        return List.of(Component.translatable(tooltipPrefix + "." + key).getVisualOrderText());
                    }
                }
                JsonElement defaultValue = node.defaultValue();
                return List.of(Component.literal(defaultValue.toString()).getVisualOrderText());
            };
        }

        private static String pathKey(ValuePath path) {
            if (path.isRoot()) {
                return "root";
            }
            StringBuilder builder = new StringBuilder();
            for (ValuePath.Segment segment : path) {
                if (!builder.isEmpty()) {
                    builder.append('.');
                }
                builder.append(segment.isKey() ? segment.key() : segment.index());
            }
            return builder.toString();
        }

        private static String defaultLabel(ValuePath path) {
            if (path.isRoot()) {
                return "root";
            }
            List<ValuePath.Segment> segments = path.segments();
            ValuePath.Segment segment = segments.getLast();
            return segment.isKey() ? segment.key() : String.valueOf(segment.index());
        }
    }

    @FunctionalInterface
    public interface EditorProviderFactory<T> {
        EditorProvider create(CodecOptionScreen<T> screen);
    }

    private static final class EnumEditorProvider<E extends Enum<E>, T> implements EditorProvider {

        private final CodecOptionScreen<T> screen;
        private final ValuePath path;
        private final E[] values;
        private final Function<E, Component> valueLabel;
        private final Function<E, String> serializer;

        private EnumEditorProvider(CodecOptionScreen<T> screen,
                                   ValuePath path,
                                   E[] values,
                                   Function<E, Component> valueLabel,
                                   Function<E, String> serializer) {
            this.screen = screen;
            this.path = path;
            this.values = values;
            this.valueLabel = valueLabel;
            this.serializer = serializer;
        }

        @Override
        public boolean supports(Node node) {
            return node.path().equals(path);
        }

        @Override
        public Optional<Object> create(Node node) {
            if (!(node instanceof LeafNode leaf)) {
                return Optional.empty();
            }
            String defaultSerialized = leaf.defaultValue().getAsString();
            E defaultValue = findValue(defaultSerialized).orElse(values[0]);
            CycleButton<E> button = CycleButton.<E>builder(valueLabel::apply)
                    .withValues(values)
                    .withInitialValue(defaultValue)
                    .create(0, 0, Constants.DEFAULT_CONTROL_WIDTH, 20, CommonComponents.EMPTY, (cycleButton, value) -> screen.updateEdits(node.path(), serializer.apply(value), serializer.apply(defaultValue)));
            OptionEntry.CycleButtonConfigEntry<E> entry = new OptionEntry.CycleButtonConfigEntry<>(screen, screen.labelFor(node), screen.tooltipFor(node), button);
            return Optional.of(entry);
        }

        private Optional<E> findValue(String serialized) {
            for (E value : values) {
                if (serializer.apply(value).equals(serialized)) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }
}
