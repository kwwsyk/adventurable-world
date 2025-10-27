package com.kwwsyk.suit.adventurableworld.mixin.client;

import com.kwwsyk.suit.adventurableworld.client.ClientEvents;
import com.kwwsyk.suit.adventurableworld.client.CreateWorldScreenExt;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin implements CreateWorldScreenExt {

    @Unique
    ClientEvents.ExtendedGameMode gameMode = null;
    @Shadow@Final
    WorldCreationUiState uiState;

    @Unique
    public ClientEvents.ExtendedGameMode adventure_suit$getGameMode() {
        return this.gameMode;
    }

    @Unique
    public void adventure_suit$setGameMode(ClientEvents.ExtendedGameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Inject(
            method = "createLevelSettings",
            at = @At("HEAD"),
            cancellable = true
    )
    public void adventure_suit$expandLevelSettings(boolean debug, CallbackInfoReturnable<LevelSettings> cir){
        if(debug) return;
        var n_ret = new LevelSettings(
                this.uiState.getName().trim(),
                gameMode != null ? gameMode.gameType() : this.uiState.getGameMode().gameType,
                this.uiState.isHardcore(),
                this.uiState.getDifficulty(),
                this.uiState.isAllowCommands(),
                this.uiState.getGameRules(),
                this.uiState.getSettings().dataConfiguration()
        );
        cir.setReturnValue(n_ret);
    }
}
