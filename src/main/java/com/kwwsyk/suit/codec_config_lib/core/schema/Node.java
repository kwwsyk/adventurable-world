package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.Objects;

/**
 * Base class for all schema nodes. Nodes are immutable snapshots of the encoded object structure,
 * carrying the original JSON value together with metadata such as the node kind and lifecycle.
 */
public abstract class Node {

    private final ValuePath path;
    private final NodeKind kind;
    private final JsonElement defaultValue;
    private final Lifecycle lifecycle;

    protected Node(ValuePath path, NodeKind kind, JsonElement defaultValue, Lifecycle lifecycle) {
        this.path = Objects.requireNonNull(path, "path");
        this.kind = Objects.requireNonNull(kind, "kind");
        this.defaultValue = Objects.requireNonNull(defaultValue, "defaultValue");
        this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle");
    }

    /**
     * @return location of this node in the original tree.
     */
    public ValuePath path() {
        return path;
    }

    /**
     * @return structural kind associated with this node.
     */
    public NodeKind kind() {
        return kind;
    }

    /**
     * @return JSON snapshot captured during schema creation.
     */
    public JsonElement defaultValue() {
        return defaultValue;
    }

    /**
     * @return lifecycle reported by the codec, defaulting to {@link Lifecycle#stable()}.
     */
    public Lifecycle lifecycle() {
        return lifecycle;
    }
}
