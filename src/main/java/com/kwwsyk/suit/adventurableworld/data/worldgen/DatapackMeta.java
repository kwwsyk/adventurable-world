package com.kwwsyk.suit.adventurableworld.data.worldgen;

import com.kwwsyk.suit.adventurableworld.ModInit;
import net.minecraft.resources.ResourceLocation;

public class DatapackMeta {

    public static final String NAMESPACE = ModInit.MODID;

    public static ResourceLocation withDedicatedNamespace(String id){
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, id);
    }
}
