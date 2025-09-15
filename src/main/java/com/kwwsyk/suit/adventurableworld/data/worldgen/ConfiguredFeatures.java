package com.kwwsyk.suit.adventurableworld.data.worldgen;

import com.kwwsyk.suit.adventurableworld.worldgen.feature.FeaturesReg;
import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MiningLadderConfiguration;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> SHORT_LADDER_CONFIG = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            DatapackMeta.withDedicatedNamespace("short_ladder_config")
    );
    public static final ResourceKey<ConfiguredFeature<?, ?>> LONG_LADDER_CONFIG = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            DatapackMeta.withDedicatedNamespace("long_ladder_config")
    );
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIMPLE_LADDER_CONFIG = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            DatapackMeta.withDedicatedNamespace("simple_ladder_config")
    );


    /**Use a {@link RegistrySetBuilder} to register {@code configured feature}. Refer to
     * <a href="https://docs.neoforged.net/docs/concepts/registries#data-generation-for-datapack-registries">Neoforge doc</a>.
     * <p>
     * The contructor of {@link ConfiguredFeature} should apply a registried feature value, like {@link DeferredHolder#value()}
     *  (vanilla are in {@link net.minecraft.world.level.levelgen.feature.Feature}
     */
    public static void addConfiguredFeature(RegistrySetBuilder Builder){
        Builder.add(Registries.CONFIGURED_FEATURE, bootstrap -> {
            bootstrap.register(
                    // The resource key of our configured feature.
                    SIMPLE_LADDER_CONFIG,
                    // The actual configured feature.
                    new ConfiguredFeature<>(
                            FeaturesReg.SIMPLE_LADDER.value(),
                            new MiningLadderConfiguration(true,2,20))
            );
            bootstrap.register(
                    SHORT_LADDER_CONFIG,
                    new ConfiguredFeature<>(
                            FeaturesReg.SIMPLE_LADDER.value(),
                            new MiningLadderConfiguration(false,2,10)
                    )
            );
            bootstrap.register(
                    LONG_LADDER_CONFIG,
                    new ConfiguredFeature<>(
                            FeaturesReg.SIMPLE_LADDER.value(),
                            new MiningLadderConfiguration(true,15,64)
                    )
            );
        });
    }
}
