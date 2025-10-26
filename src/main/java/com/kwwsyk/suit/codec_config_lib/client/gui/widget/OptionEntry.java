package com.kwwsyk.suit.codec_config_lib.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.kwwsyk.suit.codec_config_lib.client.gui.OptionScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;

public abstract class OptionEntry extends ContainerObjectSelectionList.Entry<OptionEntry> {

    private static final int DEFAULT_HEIGHT = 18;//I think it's consisted of two 1 pixel edges and 1 16 pixel h body.
    private static final int DEFAULT_OFFSET = 17;//1 + 16

    private int displayNameOffset = DEFAULT_OFFSET;
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

    public static class EmptySpaceEntry extends OptionEntry {
        public EmptySpaceEntry(OptionScreen screen) {super(screen);}
        @Override
        public List<? extends NarratableEntry> narratables() {return List.of();}
        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {}
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
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
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
        }
        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }
    }

    public abstract static class ConfigEntry extends OptionEntry {

        Component label;
        protected final List<AbstractWidget> children = Lists.newArrayList();

        public ConfigEntry(OptionScreen screen, @org.jetbrains.annotations.Nullable List<FormattedCharSequence> tooltip) {
            super(screen, tooltip);
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

        public CycleButtonConfigEntry(OptionScreen screen,@Nullable List<FormattedCharSequence> comment, CycleButton<T> button) {
            super(screen, comment);
            this.button = button;
            children.add(button);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            this.renderLabel(guiGraphics, left, top);
            this.button.setX(left + 17);
            this.button.setY(top);
            this.button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
    public static class BooleanConfigEntry extends CycleButtonConfigEntry<Boolean> {


        public BooleanConfigEntry(OptionScreen screen,
                                  @org.jetbrains.annotations.Nullable List<FormattedCharSequence> comment,
                                  CycleButton<Boolean> button) {
            super(screen, comment, button);
        }
    }
}
