package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

/**
 * Simple node that wraps a primitive JSON value. No additional metadata is stored beyond the
 * default JSON snapshot as codecs already normalize primitives according to their constraints.
 */
public final class LeafNode extends Node {

    public LeafNode(ValuePath path,
                    NodeKind kind,
                    JsonElement defaultValue,
                    Lifecycle lifecycle) {
        super(path, kind, defaultValue, lifecycle);
    }
}
