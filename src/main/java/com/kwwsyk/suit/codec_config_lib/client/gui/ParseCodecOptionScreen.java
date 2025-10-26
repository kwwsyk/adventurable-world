package com.kwwsyk.suit.codec_config_lib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Objects;

/**
 * Base class for screens that render codec-backed configuration options. The implementation
 * delegates layout concerns to {@link OptionScreen} while retaining a reference to the parent
 * screen so concrete subclasses can return to it when closing.
 */
public abstract class ParseCodecOptionScreen extends OptionScreen {

    /// Referables:
    /// @see net.neoforged.neoforge.client.gui.ConfigurationScreen
    /// @see net.minecraft.client.gui.screens.options.OptionsSubScreen
    /// @see net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen
    /// @see net.minecraft.client.gui.screens.CreateBuffetWorldScreen

    public final Screen lastScreen;

    protected ParseCodecOptionScreen(Screen lastScreen, Component title) {
        super(title, () -> Minecraft.getInstance().setScreen(lastScreen));
        this.lastScreen = Objects.requireNonNull(lastScreen, "lastScreen");
    }
}
