package com.kwwsyk.suit.codec_config_lib.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 *
 */
public class ParseCodecOptionScreen extends Screen {

    /// Referables:
    /// @see net.neoforged.neoforge.client.gui.ConfigurationScreen
    /// @see net.minecraft.client.gui.screens.options.OptionsSubScreen
    /// @see net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen
    /// @see net.minecraft.client.gui.screens.CreateBuffetWorldScreen

    public final Screen lastScreen;

    protected ParseCodecOptionScreen(Screen lastScreen, Component title) {
        super(title);
        this.lastScreen = lastScreen;
    }
}
