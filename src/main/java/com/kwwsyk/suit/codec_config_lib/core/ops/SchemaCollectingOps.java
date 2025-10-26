package com.kwwsyk.suit.codec_config_lib.core.ops;

import com.google.gson.JsonElement;
import com.kwwsyk.suit.codec_config_lib.core.schema.Node;
import com.kwwsyk.suit.codec_config_lib.core.schema.OptionSchema;
import com.kwwsyk.suit.codec_config_lib.core.schema.SchemaBuilders;
import com.kwwsyk.suit.codec_config_lib.core.schema.ValuePath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Facade that turns a codec encoded object into an {@link OptionSchema}. The implementation
 * currently leverages {@link JsonOps} to serialize the instance and then rehydrates the schema from
 * the produced JSON. The class name mirrors the original design where a custom {@code DynamicOps}
 * captured the structure on the fly, keeping the door open for richer metadata in the future.
 */
public final class SchemaCollectingOps {

    private SchemaCollectingOps() {
    }

    /**
     * Serializes the provided instance through the supplied codec and constructs an
     * {@link OptionSchema} from the resulting JSON tree.
     *
     * @param instance object being edited.
     * @param codec    codec describing the object.
     * @param <T>      logical type of the codec.
     * @return data result carrying either the built schema or the codec error.
     */
    public static <T> DataResult<OptionSchema<T>> collect(T instance, Codec<T> codec) {
        DataResult<JsonElement> encoded = codec.encodeStart(JsonOps.INSTANCE, instance);
        return encoded.map(json -> {
            Map<ValuePath, Node> lookup = new LinkedHashMap<>();
            Node root = SchemaBuilders.buildTree(json, lookup);
            return OptionSchema.of(root, json, lookup);
        });
    }
}
