package com.kwwsyk.suit.adventurableworld.client;

import com.kwwsyk.suit.adventurableworld.ModInit;
import com.kwwsyk.suit.adventurableworld.client.codec.WorldgenOptionScreen;
import com.kwwsyk.suit.adventurableworld.mixin.client.CreateWorldScreenAccessor;
import com.kwwsyk.suit.adventurableworld.mixin.client.CreateWorldScreenMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = ModInit.MODID, value = Dist.CLIENT)
public final class ClientEvents {//todo BUG: switch to World & More tab will not disable or hide EXT gameModeButton

    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onCreateWorldInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CreateWorldScreen screen)) {
            return;
        }

        WorldCreationUiState uiState = ((CreateWorldScreenAccessor) screen).adventure_suit$getUiState();

        AbstractWidget reference = (AbstractWidget) event.getListenersList().stream()
                .filter(widget -> widget instanceof CycleButton<?> cycle && cycle.getMessage().getString().contains("Game Mode"))
                .findFirst()
                .orElse(null);

        if (reference == null) {
            return;
        }

        event.removeListener(reference);

        int x = reference.getX();
        int y = reference.getY();
        int width = reference.getWidth();
        int height = reference.getHeight();

        ExtendedGameMode initial = ExtendedGameMode.from(uiState);

        CycleButton<ExtendedGameMode> gameModeButton = CycleButton.<ExtendedGameMode>builder(ExtendedGameMode::title)
                .withValues(ExtendedGameMode.values())
                .withInitialValue(initial)
                .withCustomNarration(CycleButton::createDefaultNarrationMessage)
                .displayOnlyValue()
                .create(x, y,
                        width,
                        height,
                        Component.translatable("selectWorld.gameMode"),
                        (button, value) -> applyGameMode(uiState, value, screen)
                );

        gameModeButton.setTooltip(Tooltip.create(Component.translatable("adv_option.worldgen.adventure.tooltip")));
        //applyGameMode(uiState, initial);
        event.addListener(gameModeButton);

        int buttonX = x + width + 5;
        Button optionsButton = Button.builder(Component.translatable("adv_option.worldgen.configure"), btn -> openOptions(screen))
                .bounds(buttonX, y, 120, height)
                .build();
        event.addListener(optionsButton);
    }

    private static void applyGameMode(WorldCreationUiState uiState, ExtendedGameMode mode, CreateWorldScreen screen) {
        uiState.setGameMode(switch (mode){
            case CREATIVE -> WorldCreationUiState.SelectedGameMode.CREATIVE;
            case HARDCORE -> WorldCreationUiState.SelectedGameMode.HARDCORE;
            default -> WorldCreationUiState.SelectedGameMode.SURVIVAL;
        });
        if (screen instanceof CreateWorldScreenExt ext) {
            ext.adventure_suit$setGameMode(mode);
        }
        uiState.setAllowCommands(mode.allowCheats());
    }

    private static void openOptions(Screen parent) {
        Minecraft.getInstance().setScreen(new AdvOptionScreen(parent));
    }

    public enum ExtendedGameMode {
        SURVIVAL(Component.translatable("selectWorld.gameMode.survival"), GameType.SURVIVAL, false, false),
        ADVENTURE(Component.translatable("adv_option.worldgen.adventure"), GameType.ADVENTURE, false, false),
        HARDCORE(Component.translatable("selectWorld.gameMode.hardcore"), GameType.SURVIVAL, true, false),
        CREATIVE(Component.translatable("selectWorld.gameMode.creative"), GameType.CREATIVE, false, true);


        private final Component title;
        private final GameType gameType;
        private final boolean hardcore;
        private final boolean allowCheats;

        ExtendedGameMode(Component title, GameType gameType, boolean hardcore, boolean allowCheats) {
            this.title = title;
            this.gameType = gameType;
            this.hardcore = hardcore;
            this.allowCheats = allowCheats;
        }

        public Component title() {
            return title;
        }

        public GameType gameType() {
            return gameType;
        }

        public boolean hardcore() {
            return hardcore;
        }

        public boolean allowCheats() {
            return allowCheats;
        }

        public static ExtendedGameMode from(WorldCreationUiState uiState) {
            if (uiState.isHardcore()) {
                return HARDCORE;
            }

            WorldCreationUiState.SelectedGameMode current = uiState.getGameMode();
            return switch (current) {
                case CREATIVE -> CREATIVE;
                default -> SURVIVAL;
            };
        }
    }
}
