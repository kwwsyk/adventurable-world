package com.kwwsyk.suit.codec_config_lib.api;

import java.util.Optional;

/**
 * Carries optional constraints that editors can use to render richer controls such as sliders or
 * integer steppers.
 */
public record Constraints(Optional<Number> min, Optional<Number> max, Optional<Number> step, Optional<String> i18nKey) { }
