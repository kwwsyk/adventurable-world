package com.kwwsyk.suit.adventurableworld.data.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

/**
 * Describes how a ladder feature is placed within the world.
 */
public record LadderPlacementConfig(boolean everyLayer,
                                    int count,
                                    int minAboveBottom,
                                    int maxAboveBottom) {

    public static final Codec<LadderPlacementConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.BOOL.fieldOf("every_layer").forGetter(LadderPlacementConfig::everyLayer),
            Codec.INT.fieldOf("count").forGetter(LadderPlacementConfig::count),
            Codec.INT.fieldOf("min_above_bottom").forGetter(LadderPlacementConfig::minAboveBottom),
            Codec.INT.fieldOf("max_above_bottom").forGetter(LadderPlacementConfig::maxAboveBottom)
    ).apply(builder, LadderPlacementConfig::new));

    public static LadderPlacementConfig everyLayer(int count) {
        return new LadderPlacementConfig(true, count, 0, 0);
    }

    /**
     * @return placement modifiers matching the configuration.
     */
    public List<PlacementModifier> createModifiers() {
        if (everyLayer) {
            return List.of(
                    CountOnEveryLayerPlacement.of(count),
                    BiomeFilter.biome()
            );
        }
        return List.of(
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.aboveBottom(minAboveBottom),
                        VerticalAnchor.aboveBottom(maxAboveBottom)
                ),
                BiomeFilter.biome()
        );
    }
}
