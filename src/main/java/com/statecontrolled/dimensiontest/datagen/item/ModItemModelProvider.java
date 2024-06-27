package com.statecontrolled.dimensiontest.datagen.item;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.item.ModItems;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DimensionTest.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerItem(ModItems.SAPPHIRE, "generated");
        registerItem(ModItems.DIMENSIONAL_THREAD_MODULATOR, "handheld");
    }

    private void registerItem(DeferredItem<Item> item, String subDirectory) {
        withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/" + subDirectory))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(DimensionTest.MOD_ID, "item/" + item.getId().getPath()));
    }

}
