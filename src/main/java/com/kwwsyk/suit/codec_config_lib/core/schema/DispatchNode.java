package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a codec dispatch where a discriminator key selects one of the registered sub-codecs.
 */
public final class DispatchNode extends Node {

    private final String discriminatorKey;
    private final Map<String, Node> payloads;
    private final String activeType;

    public DispatchNode(ValuePath path,
                        JsonElement defaultValue,
                        Lifecycle lifecycle,
                        String discriminatorKey,
                        Map<String, Node> payloads,
                        String activeType) {
        super(path, NodeKind.DISPATCH, defaultValue, lifecycle);
        this.discriminatorKey = Objects.requireNonNull(discriminatorKey, "discriminatorKey");
        this.payloads = Map.copyOf(payloads);
        this.activeType = Objects.requireNonNull(activeType, "activeType");
    }

    /**
     * @return field name that stores the type discriminator.
     */
    public String discriminatorKey() {
        return discriminatorKey;
    }

    /**
     * @return available payload schemas keyed by type id.
     */
    public Map<String, Node> payloads() {
        return payloads;
    }

    /**
     * @return type id active in the captured snapshot.
     */
    public String activeType() {
        return activeType;
    }
}
