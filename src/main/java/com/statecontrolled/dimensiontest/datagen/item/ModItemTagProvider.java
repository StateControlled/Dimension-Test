package com.statecontrolled.dimensiontest.datagen.item;

import java.util.concurrent.CompletableFuture;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
                               CompletableFuture<TagLookup<Block>> blockTags,
                               ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, DimensionTest.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider pProvider) {

    }
}
