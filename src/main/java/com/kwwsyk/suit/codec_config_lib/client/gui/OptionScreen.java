package com.kwwsyk.suit.codec_config_lib.client.gui;

import com.google.common.collect.ImmutableList;
import com.kwwsyk.suit.codec_config_lib.client.gui.widget.OptionEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * {@link Screen} implementation that renders a vertically scrollable list of {@link OptionEntry} elements together
 * with a conventional "Done" footer. Concrete subclasses only need to populate the {@link OptionList} by overriding
 * {@link #populateOptions(OptionList)}; layout, rendering, and button wiring are handled automatically.
 */
public class OptionScreen extends Screen {

    /** Shared MC layout helper that handles header/footer spacing. */
    final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private final Runnable exitCallback;

    @Nullable
    private OptionList optionList;
    private Button doneButton;
    private Button cancelButton;

    /**
     * Creates a new screen.
     *
     * @param title        localized title component.
     * @param exitCallback runnable invoked after the screen closes.
     */
    protected OptionScreen(Component title, Runnable exitCallback) {
        super(title);
        this.exitCallback = exitCallback;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        if (this.optionList == null) {
            this.layout.addTitleHeader(this.title, this.getMinecraft().font);
            this.optionList = this.layout.addToContents(new OptionList());
            //this.optionList.setRenderBackground(false);//todo BUG: DONE and CANCEL button overlap (are in same pos)
            this.doneButton = Button.builder(CommonComponents.GUI_DONE, button -> this.onDone()).build();
            this.layout.addToFooter(this.doneButton);
            this.cancelButton = Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose()).build();
            this.layout.addToFooter(this.cancelButton);
        }
        this.layout.visitWidgets(this::addRenderableWidget);
        OptionList list = this.optionList();
        //list.clearEntries();
        this.populateOptions(list);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.optionList != null) {
            this.optionList.updateSize(this.width, this.layout);
        }
        if (this.doneButton != null && this.cancelButton != null) {
            int margin = 5;
            int spacing = 10;
            int available = Math.max(0, this.width - margin * 2);
            int buttonWidth = Math.min(150, Math.max(80, (available - spacing) / 2));
            int totalButtonsWidth = buttonWidth * 2 + spacing;
            if (totalButtonsWidth > available) {
                buttonWidth = Math.max(60, (available - spacing) / 2);
                totalButtonsWidth = buttonWidth * 2 + spacing;
            }
            int buttonY = this.doneButton.getY();
            int startX = Math.max(margin, (this.width - totalButtonsWidth) / 2);
            int leftX = startX;
            int rightX = leftX + buttonWidth + spacing;
            if (rightX + buttonWidth > this.width - margin) {
                rightX = this.width - margin - buttonWidth;
                leftX = Math.max(margin, rightX - spacing - buttonWidth);
            }
            this.doneButton.setWidth(buttonWidth);
            this.cancelButton.setWidth(buttonWidth);
            this.doneButton.setPosition(leftX, buttonY);
            this.cancelButton.setPosition(rightX, buttonY);
        }
    }

    /**
     * Called when the "Done" button is clicked. The default behaviour is to close the screen, subclasses may override
     * to perform validation or persistence before calling {@link #onClose()}.
     */
    protected void onDone() {
        this.onClose();
    }

    /**
     * @return footer "Done" button allowing subclasses to toggle active state or update the label.
     */
    protected final Button getDoneButton() {
        return this.doneButton;
    }

    /**
     * Returns the active {@link OptionList}. The list is guaranteed to be initialised once {@link #init()} completes.
     *
     * @return option list instance.
     */
    protected final OptionList optionList() {
        return Objects.requireNonNull(this.optionList, "Option list is not initialised yet");
    }

    /**
     * Populates the option list with entries. The default implementation clears any existing rows and leaves the list
     * empty; subclasses should override to add meaningful configuration controls.
     *
     * @param list mutable list reference created during {@link #init()}.
     */
    protected void populateOptions(OptionList list) {
        list.setEntries(List.of());
    }

    @Override
    public void onClose() {
        super.onClose();
        this.exitCallback.run();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.optionList != null) {
            this.optionList.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        /*if (this.optionList != null) {
            OptionEntry hovered = this.optionList.getHovered();
            if (hovered != null) {
                List<FormattedCharSequence> tooltip = hovered.tooltip();
                if (tooltip != null) {
                    guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
                }
            }
        }*/
    }

    /**
     * Scrollable container that keeps a vertically stacked sequence of {@link OptionEntry} instances.
     */
    public class OptionList extends ContainerObjectSelectionList<OptionEntry> {

        private static final int DEFAULT_ENTRY_HEIGHT = 18;//I think it's consisted of two 1 pixel edges and 1 16 pixel h body.

        OptionList() {
            super(
                    Minecraft.getInstance(),
                    OptionScreen.this.width,
                    OptionScreen.this.layout.getContentHeight(),
                    OptionScreen.this.layout.getHeaderHeight(),
                    DEFAULT_ENTRY_HEIGHT
            );
            //this.setRenderTopAndBottom(false);
        }

        /**
         * Convenience helper mirroring {@link net.minecraft.client.gui.components.AbstractSelectionList#replaceEntries} but using a more generic input collection.
         *
         * @param entries collection of new rows.
         */
        public void setEntries(Collection<? extends OptionEntry> entries) {
            this.replaceEntries(ImmutableList.copyOf(entries));
        }

        /**
         * Iterates through each entry in insertion order using the provided consumer.
         *
         * @param visitor callback invoked for each entry currently present in the list.
         */
        public void visitEntries(Consumer<OptionEntry> visitor) {
            for (OptionEntry entry : this.children()) {
                visitor.accept(entry);
            }
        }

        @Override
        public int getRowWidth() {
            return OptionScreen.this.width - 40;
        }
    }
}
