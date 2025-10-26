package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.Objects;

/**
 * Captures a registry-backed holder reference such as {@code Holder<PlacedFeature>}.
 */
public final class HolderNode extends Node {

    private final String registryKey;

    public HolderNode(ValuePath path,
                      JsonElement defaultValue,
                      Lifecycle lifecycle,
                      String registryKey) {
        super(path, NodeKind.HOLDER, defaultValue, lifecycle);
        this.registryKey = Objects.requireNonNull(registryKey, "registryKey");
    }

    /**
     * @return fully qualified registry key associated with this holder.
     */
    public String registryKey() {
        return registryKey;
    }
}
