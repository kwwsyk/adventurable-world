package com.kwwsyk.suit.adventurableworld.worldgen.level.levelgen.carver;

import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;

public class AdventureCaveCarverConfiguration extends CaveCarverConfiguration {


    public AdventureCaveCarverConfiguration(CarverConfiguration config, FloatProvider horizontalRadiusMultiplier, FloatProvider verticalRadiusMultiplier, FloatProvider floorLevel) {
        super(config, horizontalRadiusMultiplier, verticalRadiusMultiplier, floorLevel);
    }
}
