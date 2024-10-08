package com.statecontrolled.dimensiontest.datagen;

import java.util.concurrent.CompletableFuture;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.datagen.block.ModBlockStateProvider;
import com.statecontrolled.dimensiontest.datagen.block.ModBlockTagProvider;
import com.statecontrolled.dimensiontest.datagen.item.ModItemModelProvider;
import com.statecontrolled.dimensiontest.datagen.item.ModItemTagProvider;
import com.statecontrolled.dimensiontest.datagen.world.ModBiomeTagProvider;
import com.statecontrolled.dimensiontest.datagen.world.WorldGenProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Automatic data generation
 **/
@EventBusSubscriber(modid = DimensionTest.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new WorldGenProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));

        ModBlockTagProvider blockTagGenerator = generator.addProvider(event.includeServer(),
                new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeServer(),
                new ModItemTagProvider(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(), new ModBiomeTagProvider(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));

    }

}
