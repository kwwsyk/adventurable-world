package com.kwwsyk.suit.adventurableworld.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BiomeModifiers {

    public static final ResourceKey<BiomeModifier> SIMPLE_LADDER_FEATURE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            DatapackMeta.withDedicatedNamespace("simple_ladder_feature")
    );

    public static void addBiomeModifier(RegistrySetBuilder Builder){

        Builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
            // Lookup any necessary registries.
            // Static registries only need to be looked up if you need to grab the tag data.
            HolderGetter<PlacedFeature> placedFeatures = bootstrap.lookup(Registries.PLACED_FEATURE);

            // Register the biome modifiers.
            bootstrap.register(SIMPLE_LADDER_FEATURE,
                    new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                            // The biome(s) to generate within
                            bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                            // The feature(s) to generate within the biomes
                            HolderSet.direct(
                                    placedFeatures.getOrThrow(PlacedFeatures.SIMPLE_LADDER_PLACE),
                                    placedFeatures.getOrThrow(PlacedFeatures.SHORT_LADDER_PLACE),
                                    placedFeatures.getOrThrow(PlacedFeatures.LONG_LADDER_PLACE),
                                    placedFeatures.getOrThrow(PlacedFeatures.HIGH_LADDER_PLACE)
                            ),
                            // The generation step
                            GenerationStep.Decoration.LOCAL_MODIFICATIONS
                    )
            );
        });
    }
}
