package com.statecontrolled.dimensiontest.util;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.block.ModBlocks;
import com.statecontrolled.dimensiontest.item.ModItems;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Adds a creative tab and all the mod items to that tab.
 **/
public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DimensionTest.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DIMENSION_TEST_TAB =
        CREATIVE_MODE_TABS.register("dimension_test",
            () -> CreativeModeTab.builder()
                .title(Component.translatable("creativetab.dimensiontest_tab"))
                .icon(() -> ModItems.DIMENSIONAL_THREAD_MODULATOR.get().getDefaultInstance())
                .displayItems(
                    (parameters, output) -> {
                        output.accept(ModItems.DIMENSIONAL_THREAD_MODULATOR.get());
                        output.accept(ModItems.SAPPHIRE.get());
                        output.accept(ModBlocks.SAPPHIRE_BLOCK.get());
                    }
                )
            .build()
        );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
