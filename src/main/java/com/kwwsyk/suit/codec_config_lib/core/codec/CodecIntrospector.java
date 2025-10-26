package com.kwwsyk.suit.codec_config_lib.core.codec;

import com.mojang.serialization.Codec;

/**
 * Placeholder for codec shape detection utilities. The introspector will be filled with helpers that
 * understand optional codecs, dispatch codecs, providers and registry holders. Having the class in
 * place allows the UI layer to depend on the API surface while the implementation grows over time.
 */
public final class CodecIntrospector {

    private CodecIntrospector() {
    }

    /**
     * Returns {@code true} when the supplied codec likely behaves like a map with arbitrary keys.
     * Currently a stub and always returns {@code false}.
     *
     * @param codec codec under inspection.
     * @return whether the codec can emit dynamic map entries.
     */
    public static boolean isDynamicMap(Codec<?> codec) {
        return false;
    }
}
