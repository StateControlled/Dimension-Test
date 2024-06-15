package com.statecontrolled.dimensiontest.world.cave;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.ModTags;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

/**
 * Custom carver configuration
 **/
public class CustomCarverConfiguration {
    public static final ResourceKey<ConfiguredWorldCarver<?>> CUSTOM_CARVER_KEY = createKey("custom_carver");

    public static void bootstrap(BootstapContext<ConfiguredWorldCarver<?>> context) {
        HolderGetter<Block> blockGetter = context.lookup(Registries.BLOCK);
        context.register(CUSTOM_CARVER_KEY,
            ModCarvers.CUSTOM_CARVER.get().configured(
                new CaveCarverConfiguration(
                    0.95F,                          // probability
                    UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(200)),      // y
                    ConstantFloat.of(0.75F),        // y scale
                    VerticalAnchor.aboveBottom(4),  // lava level
                    CarverDebugSettings.of(false, Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState()), // debug
                    // TODO add mod blocks to replaceables
                    blockGetter.getOrThrow(ModTags.Blocks.CAVE_WALLS),  // blocks that can be replaced
                    UniformFloat.of(0.85F, 2.0F),   // horizontal
                    UniformFloat.of(0.85F, 3.5F),   // vertical
                    UniformFloat.of(-0.1F, 0.1F)    // floor
                )
            )
        );
    }

    private static ResourceKey<ConfiguredWorldCarver<?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, new ResourceLocation(DimensionTest.MOD_ID, name));
    }

}
