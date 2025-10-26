package com.kwwsyk.suit.adventurableworld.data.worldgen;

import com.kwwsyk.suit.adventurableworld.data.worldgen.providers.ModDatapackProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class EventRegister {

    @SubscribeEvent
    public static void generate(GatherDataEvent event){

        RegistrySetBuilder Builder = new RegistrySetBuilder();
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        //FeaturesReg.addFeature(Builder);
        ConfiguredFeatures.addConfiguredFeature(Builder);
        PlacedFeatures.addPlacedFeatures(Builder);
        BiomeModifiers.addBiomeModifier(Builder);

        DataGenerator.PackGenerator pack = dataGenerator.getBuiltinDatapack(
                true,
                DatapackMeta.NAMESPACE,
                "adventurable_world"
        );

        ModDatapackProvider dataProvider = event.createProvider((output, future)-> new ModDatapackProvider(output,future,Builder));

        pack.addProvider(put-> dataProvider);
    }
}
