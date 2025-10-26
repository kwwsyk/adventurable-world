package com.kwwsyk.suit.adventurableworld.client;

import com.kwwsyk.suit.adventurableworld.client.codec.MineLadderOptionScreen;
import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MineLadderConfig;
import net.minecraft.client.gui.screens.Screen;

/**
 * Entry point screen that opens the codec driven editor using default ladder configuration values.
 */
public class AdvOptionScreen extends MineLadderOptionScreen {

    public AdvOptionScreen(Screen lastScreen) {
        super(lastScreen, MineLadderConfig.defaults());
    }
}
