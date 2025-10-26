package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a JSON array. Children preserve their index order to allow reorder operations and to
 * simplify error reporting when codecs emit positional messages.
 */
public final class ListNode extends Node {

    private final List<Node> elements;

    public ListNode(ValuePath path,
                    JsonElement defaultValue,
                    Lifecycle lifecycle,
                    List<Node> elements) {
        super(path, NodeKind.LIST, defaultValue, lifecycle);
        Objects.requireNonNull(elements, "elements");
        this.elements = List.copyOf(elements);
    }

    /**
     * @return children captured in their original order.
     */
    public List<Node> elements() {
        return elements;
    }
}
