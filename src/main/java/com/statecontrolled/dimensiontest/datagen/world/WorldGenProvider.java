package com.statecontrolled.dimensiontest.datagen.world;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.world.biome.ModBiomes;
import com.statecontrolled.dimensiontest.world.cave.ModConfiguredCarvers;
import com.statecontrolled.dimensiontest.world.dimension.TestDimension;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public class WorldGenProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.BIOME, ModBiomes::bootstrap)
            .add(Registries.CONFIGURED_CARVER, ModConfiguredCarvers::bootstrap)
            .add(Registries.LEVEL_STEM, TestDimension::bootstrapStem)
            .add(Registries.DIMENSION_TYPE, TestDimension::bootstrapType);

    public WorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(DimensionTest.MOD_ID));
    }

}
