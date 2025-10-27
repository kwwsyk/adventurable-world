package com.kwwsyk.suit.codec_config_lib.api;

import net.minecraft.util.Mth;

import java.util.Optional;

/**
 * Carries optional constraints that editors can use to render richer controls such as sliders or
 * integer steppers.
 */
public record Constraints(Optional<Number> min, Optional<Number> max, Optional<Number> step, Optional<String> i18nKey) {

    public Number minValue(){
        return min.orElse(null);
    }

    public Number maxValue(){
        return max.orElse(null);
    }

    public int clamp(int value){
        return Mth.clamp(value, min.map(Number::intValue).orElse(Integer.MIN_VALUE), max.map(Number::intValue).orElse(Integer.MAX_VALUE));
    }

    public double clamp(double value){
        return Mth.clamp(value, min.map(Number::doubleValue).orElse(Double.MIN_VALUE), max.map(Number::doubleValue).orElse(Double.MAX_VALUE));
    }

    public float clamp(float value){
        return Mth.clamp(value, min.map(Number::floatValue).orElse(Float.MIN_VALUE), max.map(Number::floatValue).orElse(Float.MAX_VALUE));
    }

    public long clamp(long value){
        return Mth.clamp(value, min.map(Number::longValue).orElse(Long.MIN_VALUE), max.map(Number::longValue).orElse(Long.MAX_VALUE));
    }
}
