package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Describes a map where keys are user-editable strings. Differs from {@link GroupNode} because
 * entries are not part of a fixed record and can be added or removed dynamically.
 */
public final class MapNode extends Node {

    private final Map<String, Node> entries;

    public MapNode(ValuePath path,
                   JsonElement defaultValue,
                   Lifecycle lifecycle,
                   Map<String, Node> entries) {
        super(path, NodeKind.MAP, defaultValue, lifecycle);
        Objects.requireNonNull(entries, "entries");
        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }

    /**
     * @return current entries keyed by string.
     */
    public Map<String, Node> entries() {
        return entries;
    }
}
