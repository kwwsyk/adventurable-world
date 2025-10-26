package com.kwwsyk.suit.codec_config_lib.api;

import com.google.gson.JsonElement;
import com.kwwsyk.suit.codec_config_lib.core.codec.CodecRoundtrip;
import com.kwwsyk.suit.codec_config_lib.core.ops.SchemaCollectingOps;
import com.kwwsyk.suit.codec_config_lib.core.patch.SchemaPatcher;
import com.kwwsyk.suit.codec_config_lib.core.schema.OptionSchema;
import com.kwwsyk.suit.codec_config_lib.core.schema.ValuePath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Map;
import java.util.Objects;

/**
 * Entry point used by callers to build schemas, apply edits and persist results.
 */
public final class CodecConfigApi {

    private CodecConfigApi() {
    }

    /**
     * Builds an {@link OptionSchema} from an existing object instance and its codec.
     *
     * @param instance original object.
     * @param codec    codec describing the object.
     * @param <T>      logical type of the codec.
     * @return result wrapping the schema or the codec error message.
     */
    public static <T> DataResult<OptionSchema<T>> buildSchema(T instance, Codec<T> codec) {
        Objects.requireNonNull(codec, "codec");
        return SchemaCollectingOps.collect(instance, codec);
    }

    /**
     * Applies edits to the schema snapshot and re-parses the value using the codec.
     *
     * @param schema schema previously built with {@link #buildSchema(Object, Codec)}.
     * @param edits  user supplied patches.
     * @param codec  codec responsible for parsing the edited JSON.
     * @param <T>    logical type of the codec.
     * @return parse result containing either the decoded object or the codec error.
     */
    public static <T> DataResult<T> applyEdits(OptionSchema<T> schema,
                                               Map<ValuePath, Object> edits,
                                               Codec<T> codec) {
        Objects.requireNonNull(schema, "schema");
        Objects.requireNonNull(edits, "edits");
        Objects.requireNonNull(codec, "codec");
        return CodecRoundtrip.parseEdited(schema, edits, codec);
    }

    /**
     * Applies edits without parsing them again, returning the raw JSON tree that can later be
     * exported as part of a datapack.
     *
     * @param schema schema instance.
     * @param edits  user supplied patches.
     * @return patched JSON element.
     */
    public static JsonElement applyEditsToJson(OptionSchema<?> schema, Map<ValuePath, Object> edits) {
        return SchemaPatcher.apply(schema, edits);
    }
}
