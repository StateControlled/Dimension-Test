package com.statecontrolled.dimensiontest.datagen.world;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.ModTags;
import com.statecontrolled.dimensiontest.world.biome.ModBiomes;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBiomeTagProvider extends BiomeTagsProvider {

    public ModBiomeTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
                               @Nullable ExistingFileHelper existingFileHelper) {

        super(output, lookupProvider, DimensionTest.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {

        this.tag(ModTags.Biomes.IS_MOD_BIOME);
//                .add(ModBiomes.BIOME_ONE)
//                .add(ModBiomes.BIOME_TWO)
//                .add(ModBiomes.BIOME_THREE);
    }
}
