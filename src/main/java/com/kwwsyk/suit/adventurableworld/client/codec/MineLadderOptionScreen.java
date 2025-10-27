package com.kwwsyk.suit.adventurableworld.client.codec;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MineLadderConfig;
import com.kwwsyk.suit.codec_config_lib.api.CodecConfigApi;
import com.kwwsyk.suit.codec_config_lib.api.Constraints;
import com.kwwsyk.suit.codec_config_lib.api.EditorProvider;
import com.kwwsyk.suit.codec_config_lib.api.EditorProviderRegistry;
import com.kwwsyk.suit.codec_config_lib.client.BaseTypeOption;
import com.kwwsyk.suit.codec_config_lib.client.Constants;
import com.kwwsyk.suit.codec_config_lib.client.gui.ParseCodecOptionScreen;
import com.kwwsyk.suit.codec_config_lib.client.gui.widget.OptionEntry;
import com.kwwsyk.suit.codec_config_lib.client.gui.OptionScreen;
import com.kwwsyk.suit.codec_config_lib.core.codec.CodecIntrospector;
import com.kwwsyk.suit.codec_config_lib.core.codec.LifecycleUtil;
import com.kwwsyk.suit.codec_config_lib.core.json.JsonDiff;
import com.kwwsyk.suit.codec_config_lib.core.json.JsonSerde;
import com.kwwsyk.suit.codec_config_lib.core.schema.*;
import com.kwwsyk.suit.codec_config_lib.client.gui.test.ScreenDebug;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.*;
import net.minecraft.client.gui.narration.NarratableEntry;

/**
 * Screen that demonstrates how the codec configuration library can be used to build a
 * configurable editor for {@link MineLadderConfig}. The implementation keeps track of user edits,
 * applies them through {@link CodecConfigApi} and displays a preview of the patched JSON payload.
 */
public class MineLadderOptionScreen extends ParseCodecOptionScreen {
    ///todo The user-side class does a lot what should be done by codec_option_lib:
    /// @see #initialiseProviders(): better let OptionEntry and its subclasses to provide easy-to-use ConfigEntries
    ///todo many of Codec types are unsupported, add them as extends of ConfigEntry
    /// @see OptionEntry.NumberEntry extend exist NumberConfigEntry and its subclasses
    ///

    private static final Logger LOGGER = LogUtils.getLogger();

    private final EditorProviderRegistry registry = new EditorProviderRegistry();
    private final Map<ValuePath, Object> edits = new LinkedHashMap<>();
    private final Map<String, Constraints> constraints = new HashMap<>();
    private final boolean codecUsesDynamicMap;
    private OptionSchema<MineLadderConfig> schema;
    private MineLadderConfig previewConfig;
    private MutableComponent lifecycleStatus = CommonComponents.EMPTY.copy();
    private MutableComponent errorStatus = CommonComponents.EMPTY.copy();
    private List<FormattedCharSequence> jsonPreviewTooltip = List.of();

    public MineLadderOptionScreen(Screen lastScreen, MineLadderConfig config) {
        super(lastScreen, Component.translatable("adv_option.title"));
        this.codecUsesDynamicMap = CodecIntrospector.isDynamicMap(MineLadderConfig.CODEC);
        initialiseConstraints();
        initialiseProviders();
        LOGGER.debug("Registered {} codec editor providers", registry.providers().size());
        captureSchema(config);
        refreshPreview();
    }

    private void initialiseConstraints() {
        constraints.put("min_length", new Constraints(Optional.of(1), Optional.empty(), Optional.of(1),
                Optional.of("adv_option.mine_ladder.min_length.tooltip")));
        constraints.put("max_length", new Constraints(Optional.of(1), Optional.of(128), Optional.of(1),
                Optional.of("adv_option.mine_ladder.max_length.tooltip")));
    }

    private void initialiseProviders() {
        registry.register(new EditorProvider() {
            @Override
            public boolean supports(Node node) {
                return node instanceof LeafNode leaf && (leaf.kind() == NodeKind.BOOL || leaf.kind() == NodeKind.NUMBER || leaf.kind() == NodeKind.ENUM);
            }

            @Override
            public Optional<Object> create(Node node) {
                if (!(node instanceof LeafNode leaf)) {
                    return Optional.empty();
                }
                String key = lastSegmentKey(node);
                List<FormattedCharSequence> tooltip = tooltipFor(node);
                if (leaf.kind() == NodeKind.BOOL) {
                    BaseTypeOption option = (screen, hint, codec) -> {
                        boolean defaultValue = leaf.defaultValue().getAsBoolean();
                        return new OptionEntry.BooleanConfigEntry(MineLadderOptionScreen.this, labelFor(key), hint, defaultValue, (cycleButton, value) -> {
                            if (value == defaultValue) {
                                edits.remove(node.path());
                            } else {
                                edits.put(node.path(), value);
                            }
                            refreshPreview();
                        });
                    };
                    return Optional.of(option.createEntry(MineLadderOptionScreen.this, tooltip, MapCodec.unit(leaf.defaultValue().getAsBoolean())));
                }
                if (leaf.kind() == NodeKind.NUMBER) {
                    BaseTypeOption option = (screen, hint, codec) -> {
                        int defaultValue = leaf.defaultValue().getAsInt();
                        Constraints constraint = constraints.getOrDefault(key, new Constraints(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
                        return new NumberEntry(MineLadderOptionScreen.this, labelFor(key), hint, defaultValue, node.path(), constraint);
                    };
                    return Optional.of(option.createEntry(MineLadderOptionScreen.this, tooltip, MapCodec.unit(leaf.defaultValue().getAsInt())));
                }
                if (leaf.kind() == NodeKind.ENUM && "length_include".equals(key)) {
                    BaseTypeOption option = (screen, hint, codec) -> {
                        MineLadderConfig.LengthInclude defaultValue = MineLadderConfig.LengthInclude.valueOf(leaf.defaultValue().getAsString().toUpperCase(Locale.ROOT));
                        CycleButton<MineLadderConfig.LengthInclude> button = CycleButton.<MineLadderConfig.LengthInclude>builder(value -> Component.literal(value.getSerializedName()))
                                .withValues(MineLadderConfig.LengthInclude.values())
                                .withInitialValue(defaultValue)
                                .create(0, 0, Constants.DEFAULT_CONTROL_WIDTH, 20, CommonComponents.EMPTY, (cycleButton, value) -> {
                                    if (value == defaultValue) {
                                        edits.remove(node.path());
                                    } else {
                                        edits.put(node.path(), value.getSerializedName());
                                    }
                                    refreshPreview();
                                });
                        return new OptionEntry.CycleButtonConfigEntry<>(MineLadderOptionScreen.this, labelFor(key), hint, button);
                    };
                    return Optional.of(option.createEntry(MineLadderOptionScreen.this, tooltip, MapCodec.unit(leaf.defaultValue().getAsString())));
                }
                return Optional.empty();
            }
        });
    }

    private void captureSchema(MineLadderConfig config) {
        DataResult<OptionSchema<MineLadderConfig>> result = CodecConfigApi.buildSchema(config, MineLadderConfig.CODEC);
        this.schema = result.result().orElse(null);
        this.lifecycleStatus = Component.translatable("adv_option.codec.lifecycle", LifecycleUtil.lifecycleOrStable(result));
        result.error().ifPresent(error -> {
            this.errorStatus = Component.translatable("adv_option.codec.error", error.message());
            LOGGER.error("Failed to build ladder schema: {}", error.message());
        });
        if (this.schema == null) {
            LOGGER.warn("Schema capture failed; editor will show empty state.");
        }
    }

    private void refreshPreview() {
        if (schema == null) {
            previewConfig = null;
            jsonPreviewTooltip = List.of();
            return;
        }
        errorStatus = CommonComponents.EMPTY.copy();
        DataResult<MineLadderConfig> result = CodecConfigApi.applyEdits(schema, edits, MineLadderConfig.CODEC);
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
        entries.add(new OptionEntry.EmptySpaceEntry(this));
        entries.add(new JsonPreviewEntry());
        list.setEntries(entries);
    }

    @Override
    protected void onDone() {
        super.onDone();
        if (previewConfig != null) {
            LOGGER.info("User confirmed ladder configuration: {}", previewConfig);
        }
    }

    private void renderNode(Node node, List<OptionEntry> entries) {
        switch (node.kind()) {
            case GROUP -> renderGroup((GroupNode) node, entries);
            case LIST -> {
                ListNode listNode = (ListNode) node;
                entries.add(new OptionEntry.SubTitleEntry(this, unsupportedLabel(node, "List"), listTooltip(listNode)));
            }
            case MAP -> {
                MapNode mapNode = (MapNode) node;
                entries.add(new OptionEntry.SubTitleEntry(this, unsupportedLabel(node, "Map"), mapTooltip(mapNode)));
            }
            case OPTIONAL -> {
                OptionalNode optionalNode = (OptionalNode) node;
                entries.add(new OptionEntry.SubTitleEntry(this, unsupportedLabel(node, "Optional"), optionalTooltip(optionalNode)));
            }
            case EITHER -> {
                EitherNode eitherNode = (EitherNode) node;
                entries.add(new OptionEntry.SubTitleEntry(this, unsupportedLabel(node, "Either"), eitherTooltip(eitherNode)));
            }
            case DISPATCH -> {
                DispatchNode dispatchNode = (DispatchNode) node;
                entries.add(new OptionEntry.SubTitleEntry(this, unsupportedLabel(node, "Dispatch"), dispatchTooltip(dispatchNode)));
            }
            case HOLDER -> {
                HolderNode holderNode = (HolderNode) node;
                entries.add(new OptionEntry.SubTitleEntry(this, unsupportedLabel(node, "Holder"), holderTooltip(holderNode)));
            }
            case PROVIDER -> {
                ProviderNode providerNode = (ProviderNode) node;
                entries.add(new OptionEntry.SubTitleEntry(this, unsupportedLabel(node, "Provider"), providerTooltip(providerNode)));
            }
            case NULL -> entries.add(new OptionEntry.SubTitleEntry(this, unsupportedLabel(node, "Null"), null));
            default -> registry.resolve(node)
                    .filter(OptionEntry.class::isInstance)
                    .map(OptionEntry.class::cast)
                    .ifPresent(entries::add);
        }
    }

    private void renderGroup(GroupNode group, List<OptionEntry> entries) {
        entries.add(new OptionEntry.SubTitleEntry(this, labelFor(lastSegmentKey(group)), groupTooltip(group)));
        for (Node child : group.orderedChildren()) {
            renderNode(child, entries);
        }
    }

    private Component unsupportedLabel(Node node, String type) {
        return Component.translatable("adv_option.codec.unsupported", type, node.path());
    }

    private List<FormattedCharSequence> tooltipFor(Node node) {
        return ImmutableList.of(Component.literal(node.defaultValue().toString()).getVisualOrderText());
    }

    private List<FormattedCharSequence> groupTooltip(GroupNode group) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(group.path().toString()).getVisualOrderText());
        tooltip.add(Component.literal(group.children().keySet().toString()).getVisualOrderText());
        return tooltip;
    }

    private List<FormattedCharSequence> listTooltip(ListNode listNode) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("size=" + listNode.elements().size()).getVisualOrderText());
        return tooltip;
    }

    private List<FormattedCharSequence> mapTooltip(MapNode mapNode) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("keys=" + mapNode.entries().keySet()).getVisualOrderText());
        return tooltip;
    }

    private List<FormattedCharSequence> optionalTooltip(OptionalNode optionalNode) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("present=" + optionalNode.presentByDefault()).getVisualOrderText());
        tooltip.add(Component.literal("child=" + optionalNode.child().kind()).getVisualOrderText());
        return tooltip;
    }

    private List<FormattedCharSequence> eitherTooltip(EitherNode eitherNode) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("active=" + eitherNode.activeSide()).getVisualOrderText());
        return tooltip;
    }

    private List<FormattedCharSequence> dispatchTooltip(DispatchNode dispatchNode) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("key=" + dispatchNode.discriminatorKey()).getVisualOrderText());
        tooltip.add(Component.literal("type=" + dispatchNode.activeType()).getVisualOrderText());
        return tooltip;
    }

    private List<FormattedCharSequence> holderTooltip(HolderNode holderNode) {
        return List.of(Component.literal(holderNode.registryKey()).getVisualOrderText());
    }

    private List<FormattedCharSequence> providerTooltip(ProviderNode providerNode) {
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(providerNode.providerType()).getVisualOrderText());
        tooltip.add(Component.literal("args=" + providerNode.arguments().keySet()).getVisualOrderText());
        return tooltip;
    }

    private String lastSegmentKey(Node node) {
        List<ValuePath.Segment> segments = node.path().segments();
        if (segments.isEmpty()) {
            return "root";
        }
        ValuePath.Segment segment = segments.getLast();
        return segment.isKey() ? segment.key() : String.valueOf(segment.index());
    }

    private MutableComponent labelFor(String key) {
        return Component.translatable("adv_option.mine_ladder." + key);
    }

    public boolean usesDynamicMapCodec() {
        return codecUsesDynamicMap;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, lifecycleStatus, 20, height - 55, 0xFFFFFF, false);
        guiGraphics.drawString(font, errorStatus, 20, height - 42, 0xFF8888, false);
    }

    private final class JsonPreviewEntry extends OptionEntry {

        JsonPreviewEntry() {
            super(MineLadderOptionScreen.this, jsonPreviewTooltip);
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
            guiGraphics.drawString(MineLadderOptionScreen.this.font, Component.translatable("adv_option.codec.preview"), left, top, 0xFFFFFF, false);
            if (!jsonPreviewTooltip.isEmpty()) {
                int offset = 12;
                for (FormattedCharSequence sequence : jsonPreviewTooltip) {
                    guiGraphics.drawString(MineLadderOptionScreen.this.font, sequence, left, top + offset, 0xAAAAAA, false);
                    offset += 10;
                }
            }
        }

        @Override
        public List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children() {
            return List.of();
        }
    }

    private final class NumberEntry extends OptionEntry.ConfigEntry {

        private final EditBox field;
        private final ValuePath path;
        private final Constraints constraints;
        private final int defaultValue;

        NumberEntry(OptionScreen screen, Component label, List<FormattedCharSequence> tooltip, int defaultValue, ValuePath path, Constraints constraints) {
            super(screen, label, tooltip);
            this.path = path;
            this.constraints = constraints;
            this.defaultValue = defaultValue;
            this.field = new EditBox(Minecraft.getInstance().font, 0, 0, Constants.DEFAULT_CONTROL_WIDTH, 20, Component.literal(""));
            this.field.setValue(String.valueOf(defaultValue));
            this.field.setResponder(this::onValueChanged);
            children.add(field);
        }

        private void onValueChanged(String value) {
            if (value.isBlank()) {
                edits.remove(path);
                refreshPreview();
                return;
            }
            try {
                int parsed = Integer.parseInt(value);
                int min = constraints.min().map(Number::intValue).orElse(Integer.MIN_VALUE);
                int max = constraints.max().map(Number::intValue).orElse(Integer.MAX_VALUE);
                parsed = Mth.clamp(parsed, min, max);
                if (parsed == defaultValue) {
                    edits.remove(path);
                } else {
                    edits.put(path, parsed);
                }
                refreshPreview();
            } catch (NumberFormatException ignored) {
            }
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            renderLabel(guiGraphics, left, top + 6);
            field.setX(left + 120);
            field.setY(top);
            field.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
