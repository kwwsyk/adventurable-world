package com.kwwsyk.suit.codec_config_lib.core.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * Centralizes Gson configuration for serializing auxiliary data structures such as diffs or cached
 * edits.
 */
public final class JsonSerde {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonSerde() {
    }

    /**
     * Serializes the provided element into a human-readable string.
     *
     * @param element json element.
     * @return pretty printed string.
     */
    public static String toPrettyString(JsonElement element) {
        return GSON.toJson(element);
    }
}
