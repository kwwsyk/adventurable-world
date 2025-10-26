package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.Map;
import java.util.Objects;

/**
 * Describes a numeric provider codec such as {@code IntProvider} or {@code FloatProvider}.
 */
public final class ProviderNode extends Node {

    private final String providerType;
    private final Map<String, Node> arguments;

    public ProviderNode(ValuePath path,
                        JsonElement defaultValue,
                        Lifecycle lifecycle,
                        String providerType,
                        Map<String, Node> arguments) {
        super(path, NodeKind.PROVIDER, defaultValue, lifecycle);
        this.providerType = Objects.requireNonNull(providerType, "providerType");
        this.arguments = Map.copyOf(arguments);
    }

    /**
     * @return identifier that selects which provider subtype is active.
     */
    public String providerType() {
        return providerType;
    }

    /**
     * @return arguments passed to the active provider subtype.
     */
    public Map<String, Node> arguments() {
        return arguments;
    }
}
