package com.kwwsyk.suit.adventurableworld.worldgen.feature;

import com.kwwsyk.suit.adventurableworld.ModInit;
import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MiningLadderConfiguration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FeaturesReg {

    public static final String SIMPLE_LADDER_ID = "simple_cave_ladder";

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, ModInit.MODID);
    /**Set the type to {@link DeferredHolder}{@code <Feature<?>,SimpleMiningLadder>} to let non-type value present in use.
     */
    public static final DeferredHolder<Feature<?>,SimpleMiningLadder> SIMPLE_LADDER = FEATURES.register(SIMPLE_LADDER_ID, ()-> new SimpleMiningLadder(MiningLadderConfiguration.CODEC));

}
