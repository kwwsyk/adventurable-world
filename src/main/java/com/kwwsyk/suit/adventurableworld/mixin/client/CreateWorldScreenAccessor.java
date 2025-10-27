package com.kwwsyk.suit.adventurableworld.mixin.client;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreateWorldScreen.class)
public interface CreateWorldScreenAccessor {

    @Accessor("uiState")
    WorldCreationUiState adventure_suit$getUiState();

    @Accessor("gameModeButton")
    CycleButton<?> adventure_suit$getGameModeButton();
}
