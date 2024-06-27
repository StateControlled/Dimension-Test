package com.statecontrolled.dimensiontest.util;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items {

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(DimensionTest.MOD_ID, name));
        }

        private static TagKey<Item> NeoForgeTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("neoforge", name));
        }

    }

    public static class Blocks {
        public static final TagKey<Block> CAVE_WALLS = tag("cave_walls");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(DimensionTest.MOD_ID, name));
        }

        private static TagKey<Block> NeoForgeTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("neoforge", name));
        }

    }

    public static class Biomes {
        public static final TagKey<Biome> IS_MOD_BIOME = tag("is_mod_biome");

        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(DimensionTest.MOD_ID, name));
        }

    }

}
