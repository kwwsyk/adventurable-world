package com.kwwsyk.suit.adventurableworld.data.worldgen;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runtime holder for {@link WorldgenSettings} allowing the client side UI to
 * alter the values used when registering the datapack backed features.
 */
public final class WorldgenSettingsManager {

    private static final AtomicReference<WorldgenSettings> CURRENT = new AtomicReference<>(WorldgenSettings.defaults());

    private WorldgenSettingsManager() {
    }

    public static WorldgenSettings get() {
        return CURRENT.get();
    }

    public static void set(WorldgenSettings settings) {
        CURRENT.set(Objects.requireNonNull(settings, "settings"));
    }
}
