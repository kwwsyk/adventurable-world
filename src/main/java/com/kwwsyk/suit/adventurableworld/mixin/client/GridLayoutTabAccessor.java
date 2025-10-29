package com.kwwsyk.suit.adventurableworld.mixin.client;

import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GridLayoutTab.class)
public interface GridLayoutTabAccessor {

    @Accessor("layout")
    GridLayout aw$getLayout();
}
