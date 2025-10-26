package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a JSON object where each child is keyed by name. The node keeps insertion order to
 * provide a deterministic layout for the UI.
 */
public final class GroupNode extends Node {

    private final Map<String, Node> children;

    public GroupNode(ValuePath path,
                     JsonElement defaultValue,
                     Lifecycle lifecycle,
                     Map<String, Node> children) {
        super(path, NodeKind.GROUP, defaultValue, lifecycle);
        Objects.requireNonNull(children, "children");
        this.children = Collections.unmodifiableMap(new LinkedHashMap<>(children));
    }

    /**
     * @return immutable map of child nodes keyed by object member name.
     */
    public Map<String, Node> children() {
        return children;
    }

    /**
     * @return ordered collection of children for convenient iteration.
     */
    public Collection<Node> orderedChildren() {
        return children.values();
    }

    /**
     * Returns the child node for a specific key.
     *
     * @param key object member name.
     * @return matching node or {@code null} when absent.
     */
    public Node child(String key) {
        return children.get(key);
    }
}
