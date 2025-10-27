package com.kwwsyk.suit.adventurableworld.data.worldgen;


import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;


public class PlacedFeatures {

    public static final ResourceKey<PlacedFeature> SIMPLE_LADDER_PLACE = ResourceKey.create(
            Registries.PLACED_FEATURE,DatapackMeta.withDedicatedNamespace("simple_ladder_placement")
    );
    public static final ResourceKey<PlacedFeature> SHORT_LADDER_PLACE = ResourceKey.create(
            Registries.PLACED_FEATURE,DatapackMeta.withDedicatedNamespace("short_ladder_placement")
    );
    public static final ResourceKey<PlacedFeature> LONG_LADDER_PLACE = ResourceKey.create(
            Registries.PLACED_FEATURE,DatapackMeta.withDedicatedNamespace("long_ladder_placement")
    );
    //can be found in high place XD
    public static final ResourceKey<PlacedFeature> HIGH_LADDER_PLACE = ResourceKey.create(
            Registries.PLACED_FEATURE,DatapackMeta.withDedicatedNamespace("high_ladder_placement")
    );

    /**Use a {@link RegistrySetBuilder} to register {@code placed features} using {@code configured feature}. Refer to
     * <a href="https://docs.neoforged.net/docs/concepts/registries#data-generation-for-datapack-registries">Neoforge doc</a>.
     * <p>
     * bootstrap register needs a {@code ResourceKey<PlacedFeature>}, and a new {@link PlacedFeature} which takes a list of
     * {@link PlacementModifier} subclasses.
     */
    public static void addPlacedFeatures(RegistrySetBuilder Builder){
        Builder.add(
                Registries.PLACED_FEATURE, bootstrap -> {
                    HolderGetter<ConfiguredFeature<?, ?>> holderGetter = bootstrap.lookup(Registries.CONFIGURED_FEATURE);
                    /// Consider offset as the placement of ladders is from the top:
                    ///     minAbove = minAboveBottomPredicted + minLengthConfigured
                    ///     minAbove(absolute) = minAboveBottomPredicted + maxLengthConfigured
                    ///     maxAbove = maxAboveBottomPredicted
                    WorldgenSettings settings = WorldgenSettingsManager.get();

                    bootstrap.register(
                            SIMPLE_LADDER_PLACE,new PlacedFeature(
                                    holderGetter.getOrThrow(ConfiguredFeatures.SIMPLE_LADDER_CONFIG),
                                    settings.simplePlacement().createModifiers()
                        )
                    );
                    bootstrap.register(
                            SHORT_LADDER_PLACE,new PlacedFeature(
                                    holderGetter.getOrThrow(ConfiguredFeatures.SHORT_LADDER_CONFIG),
                                    settings.shortPlacement().createModifiers()
                        )
                    );
                    bootstrap.register(
                            LONG_LADDER_PLACE,new PlacedFeature(
                                    holderGetter.getOrThrow(ConfiguredFeatures.LONG_LADDER_CONFIG),
                                    settings.longPlacement().createModifiers()
                            )
                    );
                    bootstrap.register(
                            HIGH_LADDER_PLACE,new PlacedFeature(
                                    holderGetter.getOrThrow(ConfiguredFeatures.LONG_LADDER_CONFIG),
                                    settings.highPlacement().createModifiers()
                            )
                    );
                }
        );
    }
}
