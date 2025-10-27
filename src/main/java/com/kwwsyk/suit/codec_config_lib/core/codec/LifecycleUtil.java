package com.kwwsyk.suit.codec_config_lib.core.codec;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;

/**
 * Extracts lifecycle information from codec operations.
 */
public final class LifecycleUtil {

    private LifecycleUtil() {
    }

    /**
     * Best effort method that retrieves the lifecycle from a {@link DataResult}. When the lifecycle
     * is missing the method falls back to {@link Lifecycle#stable()}.
     *
     * @param result data result produced by a codec.
     * @return lifecycle associated with the result.
     */
    public static Lifecycle lifecycleOrStable(DataResult<?> result) {
        return result.lifecycle();
    }
}
