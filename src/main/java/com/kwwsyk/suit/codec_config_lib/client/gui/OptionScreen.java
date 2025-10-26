package com.kwwsyk.suit.codec_config_lib.client.gui;

import com.kwwsyk.suit.codec_config_lib.client.gui.widget.OptionEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class OptionScreen extends Screen {

    final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Runnable exitCallback;

    protected OptionScreen(Component title, Runnable exitCallback) {
        super(title);
        this.exitCallback = exitCallback;
    }

    public void onClose(){
        super.onClose();
        exitCallback.run();
    }

    public class OptionList extends ContainerObjectSelectionList<OptionEntry>{

        private static final int DEFAULT_ENTRY_HEIGHT = 18;//I think it's consisted of two 1 pixel edges and 1 16 pixel h body.

        public OptionList() {
            super(
                    Minecraft.getInstance(),
                    OptionScreen.this.width,
                    OptionScreen.this.layout.getContentHeight(),
                    OptionScreen.this.layout.getHeaderHeight(),
                    DEFAULT_ENTRY_HEIGHT
            );
        }
    }
}
