package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable representation of a codec-driven editable tree. The schema contains the root node and a
 * JSON snapshot that can be patched without mutating the original instance.
 *
 * @param <T> logical type of the codec backing this schema.
 */
public final class OptionSchema<T> {

    private final Node root;
    private final JsonElement snapshot;
    private final Map<ValuePath, Node> lookup;

    private OptionSchema(Node root, JsonElement snapshot, Map<ValuePath, Node> lookup) {
        this.root = Objects.requireNonNull(root, "root");
        this.snapshot = Objects.requireNonNull(snapshot, "snapshot").deepCopy();
        this.lookup = Map.copyOf(lookup);
    }

    /**
     * Factory used by the builder utilities.
     */
    public static <T> OptionSchema<T> of(Node root, JsonElement snapshot, Map<ValuePath, Node> lookup) {
        return new OptionSchema<>(root, snapshot, lookup);
    }

    /**
     * @return schema root node.
     */
    public Node root() {
        return root;
    }

    /**
     * @return deep copy of the JSON snapshot, allowing editors to mutate the tree safely.
     */
    public JsonElement snapshot() {
        return snapshot.deepCopy();
    }

    /**
     * Locates a node by path.
     *
     * @param path target path.
     * @return optional node; empty when path was unknown during schema capture.
     */
    public Optional<Node> find(ValuePath path) {
        Objects.requireNonNull(path, "path");
        return Optional.ofNullable(lookup.get(path));
    }
}
