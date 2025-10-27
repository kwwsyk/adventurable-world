package com.kwwsyk.suit.adventurableworld.data.worldgen;

import com.kwwsyk.suit.adventurableworld.worldgen.feature.configurations.MineLadderConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Aggregated configuration describing the custom structures injected by the mod.
 * <p>
 * The configuration mirrors the generated datapack entries under
 * {@code resources/adventurableworld/data/} allowing the values to be tweaked at
 * runtime via a codec driven options screen.
 */
public record WorldgenSettings(MineLadderConfig simpleLadder,
                               MineLadderConfig shortLadder,
                               MineLadderConfig longLadder,
                               LadderPlacementConfig simplePlacement,
                               LadderPlacementConfig shortPlacement,
                               LadderPlacementConfig longPlacement,
                               LadderPlacementConfig highPlacement) {

    public static final Codec<WorldgenSettings> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            MineLadderConfig.CODEC.fieldOf("simple_ladder").forGetter(WorldgenSettings::simpleLadder),
            MineLadderConfig.CODEC.fieldOf("short_ladder").forGetter(WorldgenSettings::shortLadder),
            MineLadderConfig.CODEC.fieldOf("long_ladder").forGetter(WorldgenSettings::longLadder),
            LadderPlacementConfig.CODEC.fieldOf("simple_placement").forGetter(WorldgenSettings::simplePlacement),
            LadderPlacementConfig.CODEC.fieldOf("short_placement").forGetter(WorldgenSettings::shortPlacement),
            LadderPlacementConfig.CODEC.fieldOf("long_placement").forGetter(WorldgenSettings::longPlacement),
            LadderPlacementConfig.CODEC.fieldOf("high_placement").forGetter(WorldgenSettings::highPlacement)
    ).apply(builder, WorldgenSettings::new));

    /**
     * @return default settings mirroring the hard coded datapack content.
     */
    public static WorldgenSettings defaults() {
        return new WorldgenSettings(
                new MineLadderConfig(true, 4, 32, false, MineLadderConfig.LengthInclude.DROP),
                new MineLadderConfig(false, 2, 10, true, MineLadderConfig.LengthInclude.BOTH),
                new MineLadderConfig(true, 15, 64, true, MineLadderConfig.LengthInclude.DROP),
                LadderPlacementConfig.everyLayer(255),
                new LadderPlacementConfig(false, 255, 2, 128),
                new LadderPlacementConfig(false, 127, 2, 128),
                new LadderPlacementConfig(false, 127, 130, 192)
        );
    }
}
