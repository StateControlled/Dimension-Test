package com.statecontrolled.dimensiontest.block;

import static com.statecontrolled.dimensiontest.item.ModItems.ITEMS;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DimensionTest.MOD_ID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredBlock<Block> SAPPHIRE_BLOCK =
            BLOCKS.registerSimpleBlock("sapphire_block", BlockBehaviour.Properties.of().strength(0.15f).sound(SoundType.AMETHYST));

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    //public static final DeferredItem<BlockItem> SAPPHIRE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("sapphire_block", SAPPHIRE_BLOCK);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
