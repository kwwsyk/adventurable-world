package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;

import java.util.Objects;

/**
 * Represents a value encoded using {@code Either<L, R>} where only one side is active at a time.
 */
public final class EitherNode extends Node {

    private final Node left;
    private final Node right;
    private final Side activeSide;

    public EitherNode(ValuePath path,
                      JsonElement defaultValue,
                      Lifecycle lifecycle,
                      Node left,
                      Node right,
                      Side activeSide) {
        super(path, NodeKind.EITHER, defaultValue, lifecycle);
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
        this.activeSide = Objects.requireNonNull(activeSide, "activeSide");
    }

    /**
     * @return left payload descriptor.
     */
    public Node left() {
        return left;
    }

    /**
     * @return right payload descriptor.
     */
    public Node right() {
        return right;
    }

    /**
     * @return which side is active in the captured snapshot.
     */
    public Side activeSide() {
        return activeSide;
    }

    /** Possible active sides in an {@link EitherNode}. */
    public enum Side {
        LEFT,
        RIGHT
    }
}
