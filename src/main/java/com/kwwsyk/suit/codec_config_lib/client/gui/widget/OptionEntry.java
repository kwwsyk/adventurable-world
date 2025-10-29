package com.kwwsyk.suit.codec_config_lib.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.kwwsyk.suit.codec_config_lib.api.Constraints;
import com.kwwsyk.suit.codec_config_lib.client.Constants;
import com.kwwsyk.suit.codec_config_lib.client.gui.OptionScreen;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class OptionEntry extends ContainerObjectSelectionList.Entry<OptionEntry> {

    private static final int DEFAULT_HEIGHT = 24;//I think it consists of two 1-pixel edges and 1 16 pixel h body.
    private static final int DEFAULT_OFFSET = 17;//1 + 16

    public final Screen screen;
    @Nullable
    final List<FormattedCharSequence> tooltip;

    public OptionEntry(OptionScreen screen, @Nullable List<FormattedCharSequence> tooltip) {
        this.screen = screen;
        this.tooltip = tooltip;
    }

    public OptionEntry(OptionScreen screen){
        this.screen = screen;
        this.tooltip = null;
    }

    public void renderTooltip(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick){
        if(this.tooltip != null && hovering){
            guiGraphics.renderTooltip(this.screen.getMinecraft().font, this.tooltip, mouseX, mouseY);
        }
    }

    /**
     * @return optional tooltip displayed when the entry is hovered, or {@code null} when no tooltip is available.
     */
    @Nullable
    public List<FormattedCharSequence> tooltip() {
        return this.tooltip;
    }

    public static class EmptySpaceEntry extends OptionEntry {
        public EmptySpaceEntry(OptionScreen screen) {super(screen);}
        @Override
        public List<? extends NarratableEntry> narratables() {return List.of();}
        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            renderTooltip(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovering, partialTick);
        }
        @Override
        public List<? extends GuiEventListener> children() {return List.of();}
    }
    public static class SubTitleEntry extends OptionEntry {
        final Component title;
        public SubTitleEntry(OptionScreen screen, Component title, @Nullable List<FormattedCharSequence> comments){
            super(screen,comments);
            this.title = title;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                @Override
                public NarrationPriority narrationPriority() {
                    return NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput output) {
                    output.add(NarratedElementType.TITLE, SubTitleEntry.this.title);
                }
            });
        }
        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            guiGraphics.drawCenteredString(screen.getMinecraft().font, this.title, left + width / 2, top + 5, -1);
            renderTooltip(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovering, partialTick);
        }
        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }
    }

    public abstract static class ConfigEntry extends OptionEntry {

        public static final int WIDGET_HEIGHT = 20;

        Component label;
        protected final List<AbstractWidget> children = Lists.newArrayList();

        public ConfigEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> tooltip) {
            super(screen, tooltip);
            this.label = label;
        }

        /**
         * Sets the label rendered to the left of the entry's widget collection.
         *
         * @param label display component.
         */
        public void setLabel(Component label) {
            this.label = label;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return children;
        }
        @Override
        public List<? extends GuiEventListener> children() {
            return children;
        }

        protected void renderLabel(GuiGraphics guiGraphics, int x, int y){
            guiGraphics.drawString(screen.getMinecraft().font, this.label, x, y, -1, false);
        }
    }

    public static class CycleButtonConfigEntry<T> extends ConfigEntry {

        final CycleButton<T> button;

        public CycleButtonConfigEntry(OptionScreen screen,Component label ,@Nullable List<FormattedCharSequence> comment, CycleButton<T> button) {
            super(screen, label, comment);
            this.button = button;
            children.add(button);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            this.renderLabel(guiGraphics, left, top);
            renderTooltip(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovering, partialTick);
            this.button.setX(left + width - 17 - Constants.DEFAULT_CONTROL_WIDTH);
            this.button.setY(top);
            this.button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
    public static class BooleanConfigEntry extends CycleButtonConfigEntry<Boolean> {

        public static final Boolean2ObjectFunction<Component> button_label = v -> v ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;

        public BooleanConfigEntry(OptionScreen screen, Component label,
                                  @Nullable List<FormattedCharSequence> comment,
                                  boolean defaultValue,
                                  CycleButton.OnValueChange<Boolean> onValueChange
        ) {
            super(screen, label, comment,
                    CycleButton.onOffBuilder().displayOnlyValue().withInitialValue(defaultValue)
                            .create(0, 0, Constants.DEFAULT_CONTROL_WIDTH, 20, label, onValueChange)
            );
        }
    }
    public static class EnumConfigEntry<E extends Enum<E>> extends CycleButtonConfigEntry<Enum<E>> {

        public EnumConfigEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> comment,
                               Function<Enum<E>, Component> valueStringifier,
                               E[] values,
                               E defaultValue,
                               CycleButton.OnValueChange<Enum<E>> onValueChange
        ) {
            super(screen, label, comment,
                    CycleButton.builder(valueStringifier)
                            .withValues(values)
                            .withInitialValue(defaultValue)
                            .create(0,0,Constants.DEFAULT_CONTROL_WIDTH,20,label,onValueChange)
            );
        }
    }

    public abstract static class EditBoxConfigEntry<T> extends ConfigEntry{

        final EditBox editBox = new EditBox(screen.getMinecraft().font, Constants.DEFAULT_CONTROL_WIDTH, 20, label);
        String value;
        T parsedValue = null;

        public EditBoxConfigEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> tooltip) {
            super(screen, label, tooltip);
            value = editBox.getValue();
            this.children.add(editBox);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            this.renderLabel(guiGraphics, left, top);
            renderTooltip(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovering, partialTick);
            int editBoxX = left + width - 17 - Constants.DEFAULT_CONTROL_WIDTH;
            this.editBox.setX(editBoxX);
            this.editBox.setY(top);
            this.editBox.render(guiGraphics, mouseX, mouseY, partialTick);
            if(!Objects.equals(value, editBox.getValue())){
                if (!editBox.isFocused()) {
                    applyValue(editBox.getValue());//Apply value when the edit box loses focus.
                }else{
                    try {
                        T parsed = parseValue(editBox.getValue());
                        if(!testValue(parsed)){
                            guiGraphics.renderTooltip(screen.getMinecraft().font,
                                    getUnexpectedValueTip(editBox.getValue(),value),
                                    editBoxX,
                                    top + 20
                            );
                        }
                    }catch (Exception e){
                        guiGraphics.renderTooltip(screen.getMinecraft().font,
                                getInvalidValueTip(editBox.getValue(),value, e),
                                editBoxX,
                                top + 20
                        );
                    }
                }
            }
        }

        /**
         * Called when the valid value of the edit box has been updated.
         */
        public abstract void onValueChanged();

        public abstract T parseValue(String value) throws Exception;

        /**
         * Updates the current value and its parsed representation based on the input
         * from the edit box and triggers the value change event. If an error occurs
         * during parsing, a callback for invalid value handling is executed.
         * <p>
         * This method attempts to:
         * 1. Retrieve the current value from the edit box.
         * 2. Parse the retrieved value into the appropriate type.
         * 3. Invoke the {@code onValueChanged()} method to handle the updated value.
         * <p>
         * If an exception is thrown during parsing or value handling, the
         * {@code callbackOnInvalidValue()} method is invoked to handle the invalid
         * input scenario.
         */
        protected void applyValue(String newValue){
            try {
                T parsed = parseValue(newValue);
                if(!testValue(parsed)){
                    callbackOnUnexpectedValue(newValue,value);
                    return;
                }
                parsedValue = parsed;
                value = newValue;
                onValueChanged();
            } catch (Exception e) {
                callbackOnInvalidValue(newValue, value, e);
            }
        }

        public abstract boolean testValue(T value);

        public abstract void callbackOnUnexpectedValue(String newValue, String oldValue);

        public Component getUnexpectedValueTip(String newValue, String oldValue){
            return CommonComponents.EMPTY;
        }

        public abstract void callbackOnInvalidValue(String newValue, String oldValue,@Nullable Exception e);

        public Component getInvalidValueTip(String newValue, String oldValue,@Nullable Exception e){
            return CommonComponents.EMPTY;
        }

        public T getParsedValue(){
            return parsedValue;
        }

        public String getText(){
            return value;
        }

        public void setText(String text){
            editBox.setValue(text);
        }
    }
    public static class StringEntry extends EditBoxConfigEntry<String>{

        private final Consumer<String> onValueChanged;
        private final Predicate<String> validator;

        public StringEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> tooltip,
                           String defaultValue,
                           @Nullable Predicate<String> validator,
                           @Nullable Consumer<String> onValueChanged
        ) {
            super(screen, label, tooltip);
            this.validator = validator != null ? validator : v -> true;
            this.onValueChanged = onValueChanged != null ? onValueChanged : value -> {};
            this.editBox.setValue(defaultValue);
            this.value = defaultValue;
            this.parsedValue = defaultValue;
        }

        /**
         * Called when the valid value of the edit box has been updated.
         */
        @Override
        public void onValueChanged() {
            this.onValueChanged.accept(this.parsedValue);
        }

        @Override
        public String parseValue(String value) {
            return value;
        }

        @Override
        public boolean testValue(String value) {
            return this.validator.test(value);
        }

        @Override
        public void callbackOnUnexpectedValue(String newValue, String oldValue) {
            this.editBox.setValue(oldValue);
        }

        @Override
        public void callbackOnInvalidValue(String newValue, String oldValue, @org.jetbrains.annotations.Nullable Exception e) {
            this.editBox.setValue(oldValue);
        }
    }
    public abstract static class NumberEntry<N extends Number> extends EditBoxConfigEntry<N>{

        final Number defaultValue;
        final Constraints constraints;
        private final Consumer<N> onValueChanged;
        N clampedValue;

        public NumberEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> tooltip,
                           Number defaultValue, Constraints constraints,
                           @Nullable Consumer<N> onValueChanged) {
            super(screen, label, tooltip);
            this.defaultValue = defaultValue;
            this.constraints = constraints;
            this.onValueChanged = onValueChanged != null ? onValueChanged : value -> {};
        }

        @Override
        public Component getUnexpectedValueTip(String newValue, String oldValue) {
            return Component.literal("Number may be out of range.");
        }

        @Override
        public Component getInvalidValueTip(String newValue, String oldValue, @Nullable Exception e) {
            return Component.literal("Invalid number format.");
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovering, partialTick);
        }

        /**
         * Called when the valid value of the edit box has been updated.
         */
        @Override
        public void onValueChanged() {
            this.onValueChanged.accept(this.parsedValue);
        }

        @Override
        public void callbackOnUnexpectedValue(String newValue, String oldValue) {
            this.editBox.setValue(String.valueOf(this.clampedValue));
        }

        @Override
        public void callbackOnInvalidValue(String newValue, String oldValue,@Nullable Exception e) {
            this.editBox.setValue(oldValue);
        }
    }
    public static class IntEntry extends NumberEntry<Integer>{

        public IntEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> tooltip,
                        int defaultValue, Constraints constraints, @Nullable Consumer<Integer> onValueChanged
        ) {
            super(screen, label, tooltip, defaultValue, constraints, onValueChanged);
            this.clampedValue = constraints.clamp(defaultValue);
            this.parsedValue = defaultValue;
            this.value = String.valueOf(defaultValue);
            this.editBox.setValue(this.value);
        }

        @Override
        public Integer parseValue(String value) throws NumberFormatException {
            return Integer.parseInt(value);
        }

        @Override
        public boolean testValue(Integer value) {
            clampedValue = constraints.clamp(value);
            return clampedValue.equals(value);
        }
    }
    public static class FloatEntry extends NumberEntry<Float>{
        public FloatEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> tooltip,
                          float defaultValue, Constraints constraints, @Nullable Consumer<Float> onValueChanged
        ) {
            super(screen, label, tooltip, defaultValue, constraints, onValueChanged);
            this.clampedValue = constraints.clamp(defaultValue);
            this.parsedValue = defaultValue;
            this.value = String.valueOf(defaultValue);
            this.editBox.setValue(this.value);
        }
        @Override
        public Float parseValue(String value) throws NumberFormatException {
            return Float.parseFloat(value);
        }
        @Override
        public boolean testValue(Float value){
            clampedValue = constraints.clamp(value);
            return clampedValue.equals(value);
        }
    }
    public static class DoubleEntry extends NumberEntry<Double>{
        public DoubleEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> tooltip,
                           double defaultValue, Constraints constraints, @Nullable Consumer<Double> onValueChanged
        ) {
            super(screen, label, tooltip, defaultValue, constraints, onValueChanged);
            this.clampedValue = constraints.clamp(defaultValue);
            this.parsedValue = defaultValue;
            this.value = String.valueOf(defaultValue);
            this.editBox.setValue(this.value);
        }
        @Override
        public Double parseValue(String value) throws NumberFormatException {
            return Double.parseDouble(value);
        }
        @Override
        public boolean testValue(Double value){
            clampedValue = constraints.clamp(value);
            return clampedValue.equals(value);
        }
    }
    public static class LongEntry extends NumberEntry<Long>{
        public LongEntry(OptionScreen screen, Component label, @Nullable List<FormattedCharSequence> tooltip,
                         long defaultValue, Constraints constraints, @Nullable Consumer<Long> onValueChanged
        ) {
            super(screen, label, tooltip, defaultValue, constraints, onValueChanged);
            this.clampedValue = constraints.clamp(defaultValue);
            this.parsedValue = defaultValue;
            this.value = String.valueOf(defaultValue);
            this.editBox.setValue(this.value);
        }
        @Override
        public Long parseValue(String value) throws NumberFormatException {
            return Long.parseLong(value);
        }
        @Override
        public boolean testValue(Long value){
            clampedValue = constraints.clamp(value);
            return clampedValue.equals(value);
        }
    }
}
