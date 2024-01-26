package com.statecontrolled.dimensiontest.jei;

import org.jetbrains.annotations.NotNull;

import com.statecontrolled.dimensiontest.DimensionTest;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEICompatibility implements IModPlugin {

    public JEICompatibility() {
        ;
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(DimensionTest.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        //registration.addRecipeCategories();
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        //RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        //registration.addRecipes();
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        //registration.addRecipeClickArea();
    }

}
