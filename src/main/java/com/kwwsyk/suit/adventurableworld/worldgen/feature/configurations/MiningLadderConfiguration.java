package com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Try use {@link net.minecraft.util.valueproviders.IntProvider}
 */
public record MiningLadderConfiguration(
        boolean hangable, int minLength, int maxLength
) implements FeatureConfiguration {

    public static final Codec<MiningLadderConfiguration> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.BOOL.fieldOf("hangable").forGetter(MiningLadderConfiguration::hangable),
                    Codec.INT.fieldOf("min_length").forGetter(MiningLadderConfiguration::minLength),
                    Codec.INT.fieldOf("max_length").forGetter(MiningLadderConfiguration::maxLength)
            ).apply(builder,MiningLadderConfiguration::new)
    );


}
