package com.statecontrolled.dimensiontest.datagen;

import java.util.concurrent.CompletableFuture;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.block.ModBlocks;
import com.statecontrolled.dimensiontest.item.ModItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SAPPHIRE.get(), 9)
                .requires(ModBlocks.SAPPHIRE_BLOCK.get(), 1)
                .unlockedBy("has_item", has(ModBlocks.SAPPHIRE_BLOCK.get()))
                .save(recipeOutput, register("sapphire_block_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SAPPHIRE_BLOCK.get(), 1)
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .define('S', ModItems.SAPPHIRE.get())
                .unlockedBy("has_item", has(ModItems.SAPPHIRE.get()))
                .save(recipeOutput, register("sapphire_recipe"));

    }

    private ResourceLocation register(String path) {
        return new ResourceLocation(DimensionTest.MOD_ID, path);
    }

}
