package com.statecontrolled.dimensiontest.item;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.item.portal.DimensionalThreadModulator;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DimensionTest.MOD_ID);

    public static final DeferredItem<Item> SAPPHIRE = ITEMS.register("sapphire",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DIMENSIONAL_THREAD_MODULATOR = ITEMS.register("dimensional_thread_modulator",
            () -> new DimensionalThreadModulator(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
