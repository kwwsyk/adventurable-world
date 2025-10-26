package com.kwwsyk.suit.adventurableworld.worldgen.level.levelgen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;

/** Class to track and hook the carver cave generation steps to obtain a Cave Decoration feature set/chain generation plan.
 *  This carver replaces overworld cave caver when adventurable worldgen is enabled.
 *  This carver tries to generate vanilla-similar caves but let adventure mode players be able to access Underground Goals.
 */
public class AdventureCaveWorldCarver extends CaveWorldCarver {

    /// @see net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
    /// @see net.minecraft.world.level.levelgen.carver.WorldCarver
    /// Referable relevsnt classes that controls carver cave generation.

    public AdventureCaveWorldCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }


}
