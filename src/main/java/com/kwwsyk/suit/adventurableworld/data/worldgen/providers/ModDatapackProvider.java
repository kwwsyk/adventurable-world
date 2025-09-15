package com.kwwsyk.suit.adventurableworld.data.worldgen.providers;

import com.kwwsyk.suit.adventurableworld.data.worldgen.BiomeModifiers;
import com.kwwsyk.suit.adventurableworld.data.worldgen.ConfiguredFeatures;
import com.kwwsyk.suit.adventurableworld.data.worldgen.DatapackMeta;
import com.kwwsyk.suit.adventurableworld.data.worldgen.PlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatapackProvider extends DatapackBuiltinEntriesProvider {


    /**
     * Constructs a new datapack provider which generates all registry objects
     * from the provided mods using the holder.
     *
     * @param output     the target directory of the data generator
     * @param registries a future of a lookup for registries and their objects
     */
    public ModDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder datapackEntriesBuilder) {
        super(output, registries, datapackEntriesBuilder,
                buildConditions(),
                Set.of(DatapackMeta.NAMESPACE));
    }

    private static Map<ResourceKey<?>,List<ICondition>> buildConditions(){
        Map<ResourceKey<?>,List<ICondition>> map =new HashMap<>();
        map.put(BiomeModifiers.SIMPLE_LADDER_FEATURE, List.of());
        map.put(ConfiguredFeatures.SHORT_LADDER_CONFIG, List.of());
        map.put(PlacedFeatures.SIMPLE_LADDER_PLACE, List.of());
        return map;
    }
}