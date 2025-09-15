package com.kwwsyk.suit.adventurableworld.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AdvOptionScreen extends Screen{

    private static final Component title = Component.translatable("adv_option.title");

    /**
     */
    public AdvOptionScreen(Screen lastScreen) {
        super(title);
    }


}
