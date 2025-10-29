package com.kwwsyk.suit.adventurableworld.mixin.client;

import com.kwwsyk.suit.adventurableworld.client.AdvWOptionButtonAccess;
import com.kwwsyk.suit.adventurableworld.client.ClientEvents;
import com.kwwsyk.suit.adventurableworld.client.CreateWorldScreenExt;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.kwwsyk.suit.adventurableworld.client.ClientEvents.openOptions;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public class CreateWorldScreen$GameTabMixin {

    @Final
    @Shadow
    CreateWorldScreen this$0;

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;Lnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
                    ordinal = 1
            )
    )
    public <T extends LayoutElement> T aw$modifyGMButton(GridLayout.RowHelper instance, T child, LayoutSettings layoutSettings){
        var layout = ((GridLayoutTabAccessor)this).aw$getLayout();//this: GameTab ext GridLayoutTab
        var children = ((GridLayoutAccessor)layout).aw$getChildren();

        var ret = instance.addChild(
                CycleButton.<ClientEvents.ExtendedGameMode>builder(ClientEvents.ExtendedGameMode::title)
                        .withValues(ClientEvents.ExtendedGameMode.values())
                        .create(0, 0, 210, 20, Component.translatable("selectWorld.gameMode"),
                                (p_268266_, extGM) -> {
                                    ((CreateWorldScreenExt) this$0).adventure_suit$setGameMode(extGM);
                                    this$0.getUiState().setGameMode(switch (extGM){
                                        case CREATIVE -> WorldCreationUiState.SelectedGameMode.CREATIVE;
                                        case HARDCORE -> WorldCreationUiState.SelectedGameMode.HARDCORE;
                                        default -> WorldCreationUiState.SelectedGameMode.SURVIVAL;
                                    });
                                }),
                layoutSettings
        );

        Button optionsButton = Button.builder(Component.translatable("adv_option.worldgen.configure"),
                btn -> openOptions(this$0)).build();
        children.add(optionsButton);

        ((AdvWOptionButtonAccess)this$0).setOptionButton(optionsButton);

        this$0.getUiState().addListener(
                uiS -> {
                    ret.setValue(((CreateWorldScreenExt) this$0).adventure_suit$getGameMode());
                    ret.active = !uiS.isDebug();
                    ret.setTooltip(Tooltip.create(uiS.getGameMode().getInfo()));

                    optionsButton.setX(ret.getX() + 5 + 210);
                    optionsButton.setY(ret.getY());
                }
        );
        return (T) ret;
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldCreationUiState;addListener(Ljava/util/function/Consumer;)V",
                    ordinal = 1
            )
    )
    public void aw$modifyWorldCreationUiStateListener(WorldCreationUiState instance, java.util.function.Consumer<WorldCreationUiState> listener){

    }
}
