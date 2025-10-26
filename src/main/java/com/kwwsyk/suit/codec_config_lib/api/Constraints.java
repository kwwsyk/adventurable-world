package com.kwwsyk.suit.codec_config_lib.api;

import java.util.Optional;

/**
 * Carries optional constraints that editors can use to render richer controls such as sliders or
 * integer steppers.
 */
public record Constraints(Optional<Number> min, Optional<Number> max, Optional<Number> step, Optional<String> i18nKey) {

    public Constraints {
        min = min == null ? Optional.empty() : min;
        max = max == null ? Optional.empty() : max;
        step = step == null ? Optional.empty() : step;
        i18nKey = i18nKey == null ? Optional.empty() : i18nKey;
    }
}
