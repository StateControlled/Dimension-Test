package com.statecontrolled.dimensiontest.datagen.block;

import java.util.concurrent.CompletableFuture;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.util.ModTags;
import com.statecontrolled.dimensiontest.block.ModBlocks;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DimensionTest.MOD_ID, existingFileHelper);
    }


    @Override
    protected void addTags(Provider pProvider) {

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.SAPPHIRE_BLOCK.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
            .add(ModBlocks.SAPPHIRE_BLOCK.get());

        this.tag(BlockTags.OVERWORLD_CARVER_REPLACEABLES)
            .add(ModBlocks.SAPPHIRE_BLOCK.get());

        this.tag(ModTags.Blocks.CAVE_WALLS)
                .add(Blocks.WHITE_CONCRETE)
                .add(Blocks.QUARTZ_BLOCK)
                .add(Blocks.POLISHED_BLACKSTONE)
                .add(Blocks.BLACK_CONCRETE);

    }
}
