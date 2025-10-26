package com.kwwsyk.suit.codec_config_lib.api;

import com.kwwsyk.suit.codec_config_lib.core.schema.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Registry that keeps track of {@link EditorProvider} instances.
 */
public final class EditorProviderRegistry {

    private final List<EditorProvider> providers = new ArrayList<>();

    /**
     * Registers a new provider.
     *
     * @param provider provider to add.
     */
    public void register(EditorProvider provider) {
        providers.add(Objects.requireNonNull(provider, "provider"));
    }

    /**
     * @return immutable snapshot of registered providers.
     */
    public List<EditorProvider> providers() {
        return Collections.unmodifiableList(providers);
    }

    /**
     * Resolves the first provider capable of rendering the supplied node.
     *
     * @param node node under inspection.
     * @return optional editor handle.
     */
    public Optional<Object> resolve(Node node) {
        for (EditorProvider provider : providers) {
            if (provider.supports(node)) {
                Optional<Object> entry = provider.create(node);
                if (entry.isPresent()) {
                    return entry;
                }
            }
        }
        return Optional.empty();
    }
}
