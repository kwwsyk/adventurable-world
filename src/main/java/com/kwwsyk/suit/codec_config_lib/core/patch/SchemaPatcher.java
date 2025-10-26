package com.kwwsyk.suit.codec_config_lib.core.patch;

import com.google.gson.JsonElement;
import com.kwwsyk.suit.codec_config_lib.core.schema.OptionSchema;
import com.kwwsyk.suit.codec_config_lib.core.schema.ValuePath;

import java.util.Map;
import java.util.Objects;

/**
 * Applies user edits to the immutable snapshot stored in an {@link OptionSchema}. The patcher never
 * mutates the schema and instead returns a detached JSON tree that can be parsed through the codec
 * again.
 */
public final class SchemaPatcher {

    private SchemaPatcher() {
    }

    /**
     * Applies the provided edits to the schema snapshot.
     *
     * @param schema schema to patch.
     * @param edits  map of paths to replacement values. Values may be JSON elements or Java
     *               primitives understood by {@link JsonPointer#toJsonElement(Object)}.
     * @return mutated JSON tree detached from the schema.
     */
    public static JsonElement apply(OptionSchema<?> schema, Map<ValuePath, Object> edits) {
        Objects.requireNonNull(schema, "schema");
        Objects.requireNonNull(edits, "edits");
        JsonElement copy = JsonPointer.deepCopy(schema.snapshot());
        for (Map.Entry<ValuePath, Object> entry : edits.entrySet()) {
            JsonPointer.set(copy, entry.getKey(), entry.getValue());
        }
        return copy;
    }
}
