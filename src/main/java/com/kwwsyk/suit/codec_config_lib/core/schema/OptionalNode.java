package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.Objects;

/**
 * Wraps a child node that might be absent in the encoded object.
 */
public final class OptionalNode extends Node {

    private final Node child;
    private final boolean presentByDefault;

    public OptionalNode(ValuePath path,
                        JsonElement defaultValue,
                        Lifecycle lifecycle,
                        Node child,
                        boolean presentByDefault) {
        super(path, NodeKind.OPTIONAL, defaultValue, lifecycle);
        this.child = Objects.requireNonNull(child, "child");
        this.presentByDefault = presentByDefault;
    }

    /**
     * @return nested schema node that describes the optional payload.
     */
    public Node child() {
        return child;
    }

    /**
     * @return whether the optional value was present in the snapshot JSON.
     */
    public boolean presentByDefault() {
        return presentByDefault;
    }
}
