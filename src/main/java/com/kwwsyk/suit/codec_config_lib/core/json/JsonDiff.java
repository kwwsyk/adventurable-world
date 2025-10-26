package com.kwwsyk.suit.codec_config_lib.core.json;

import com.google.gson.JsonElement;

/**
 * Placeholder for JSON diff computation utilities. The diff view is part of the long-term roadmap
 * and will be implemented once the editing workflow stabilizes.
 */
public final class JsonDiff {

    private JsonDiff() {
    }

    /**
     * Returns the provided element untouched. The method acts as a convenient hook for future diff
     * calculation without disturbing current callers.
     *
     * @param element json element to inspect.
     * @return the same element.
     */
    public static JsonElement identity(JsonElement element) {
        return element;
    }
}
