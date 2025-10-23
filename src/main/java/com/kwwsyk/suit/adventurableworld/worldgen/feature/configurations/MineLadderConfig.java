package com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Try use {@link net.minecraft.util.valueproviders.IntProvider}
 * @param lengthInclude the way to count the length of a ladder, drop for down, climb for up and both for both.
 */
public record MineLadderConfig(
        boolean hangable, int minLength, int maxLength, boolean climbable, LengthInclude lengthInclude
) implements FeatureConfiguration {

    public static final Codec<MineLadderConfig> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.BOOL.fieldOf("hangable").forGetter(MineLadderConfig::hangable),
                    Codec.INT.fieldOf("min_length").forGetter(MineLadderConfig::minLength),
                    Codec.INT.fieldOf("max_length").forGetter(MineLadderConfig::maxLength),
                    Codec.BOOL.fieldOf("climbable").forGetter(MineLadderConfig::climbable),
                    LengthInclude.CODEC.fieldOf("length_include").forGetter(MineLadderConfig::lengthInclude)
            ).apply(builder, MineLadderConfig::new)
    );

    /**
     * Count the length of a ladder
     */
    public enum LengthInclude implements StringRepresentable {
        DROP("drop"),
        CLIMB("climb"),
        BOTH("both");

        public static final Codec<LengthInclude> CODEC = StringRepresentable.fromEnum(LengthInclude::values);

        private final String id;

        LengthInclude(String id){
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }
    }
}
