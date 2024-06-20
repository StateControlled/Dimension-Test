package com.statecontrolled.dimensiontest.world.cave;

import java.util.logging.Level;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.util.ModTags;

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
        DimensionTest.LOGGER.log(Level.INFO, "Carver Configuration");
        HolderGetter<Block> blockGetter = context.lookup(Registries.BLOCK);
        context.register(CUSTOM_CARVER_KEY,
            ModCarvers.CUSTOM_CARVER.get().configured(
                new CaveCarverConfiguration(
                    0.70F,                          // probability
                    UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(200)),      // y
                    ConstantFloat.of(1.0F),         // y scale
                    VerticalAnchor.aboveBottom(4),  // lava level
                    CarverDebugSettings.of(false, Blocks.RED_STAINED_GLASS.defaultBlockState()), // debug block
                    // TODO add mod blocks to replaceables
                    blockGetter.getOrThrow(ModTags.Blocks.CAVE_WALLS),  // blocks that can be replaced
                    UniformFloat.of(1.0F, 3.0F),    // horizontal
                    UniformFloat.of(1.0F, 3.0F),    // vertical
                    UniformFloat.of(0.0F, 1.0F)     // floor level
                )
            )
        );
    }

    private static ResourceKey<ConfiguredWorldCarver<?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, new ResourceLocation(DimensionTest.MOD_ID, name));
    }

}
