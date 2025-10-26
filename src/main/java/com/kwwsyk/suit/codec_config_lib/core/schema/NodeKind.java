package com.kwwsyk.suit.codec_config_lib.core.schema;

/**
 * Enumerates the structural shape of a schema node. Editors use this metadata to decide which
 * widget should be rendered for a given path.
 */
public enum NodeKind {
    /** Boolean primitive node. */
    BOOL,
    /** Numeric primitive node. */
    NUMBER,
    /** String primitive node. */
    STRING,
    /** Enum-like primitive node. */
    ENUM,
    /** JSON object / record node. */
    GROUP,
    /** JSON array node. */
    LIST,
    /** Map with string keys. */
    MAP,
    /** Optional value that can be toggled on/off. */
    OPTIONAL,
    /** Either left/right payload. */
    EITHER,
    /** Codec dispatch node with a discriminator. */
    DISPATCH,
    /** Registry holder reference. */
    HOLDER,
    /** Number provider abstraction. */
    PROVIDER,
    /** Placeholder for null values. */
    NULL
}
