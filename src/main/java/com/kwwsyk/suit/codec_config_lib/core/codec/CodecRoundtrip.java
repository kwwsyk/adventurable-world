package com.kwwsyk.suit.codec_config_lib.core.codec;

import com.google.gson.JsonElement;
import com.kwwsyk.suit.codec_config_lib.core.patch.SchemaPatcher;
import com.kwwsyk.suit.codec_config_lib.core.schema.OptionSchema;
import com.kwwsyk.suit.codec_config_lib.core.schema.ValuePath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.util.Map;

/**
 * Convenience helpers that glue schema patching with codec parsing. The class centralizes the
 * encode/parse cycle so callers can focus on surfacing validation errors.
 */
public final class CodecRoundtrip {

    private CodecRoundtrip() {
    }

    /**
     * Applies the provided edits to the schema snapshot and parses the resulting JSON through the
     * given codec.
     *
     * @param schema schema describing the original object.
     * @param edits  map of user edits.
     * @param codec  codec used for parsing.
     * @param <T>    logical type of the codec.
     * @return parse result that propagates codec errors.
     */
    public static <T> DataResult<T> parseEdited(OptionSchema<T> schema,
                                                Map<ValuePath, Object> edits,
                                                Codec<T> codec) {
        JsonElement patched = SchemaPatcher.apply(schema, edits);
        return codec.parse(JsonOps.INSTANCE, patched);
    }
}
