package com.kwwsyk.suit.codec_config_lib.api;

import com.kwwsyk.suit.codec_config_lib.core.schema.Node;

import java.util.Optional;

/**
 * Service interface that lets callers provide custom editors for specific schema nodes. Providers
 * are discovered through {@link EditorProviderRegistry}.
 */
public interface EditorProvider {

    /**
     * Determines whether this provider can render the given node.
     *
     * @param node schema node under consideration.
     * @return {@code true} when the provider should be used.
     */
    boolean supports(Node node);

    /**
     * Creates an editor handle for the node.
     *
     * @param node node to render.
     * @return opaque editor descriptor. The return type is intentionally left open while the UI
     * pipeline is built.
     */
    Optional<Object> create(Node node);
}
