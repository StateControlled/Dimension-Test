package com.statecontrolled.dimensiontest.world.biome;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModCarvers {
    public static final ResourceKey<ConfiguredWorldCarver<?>> CARVER_KEY =
            ResourceKey.create(Registries.CONFIGURED_CARVER, new ResourceLocation(DimensionTest.MOD_ID, "custom_carver"));

    public static final WorldCarver<CaveCarverConfiguration> CUSTOM_CARVER =
            register("custom_carver", new CustomCarver(CaveCarverConfiguration.CODEC));

    public static void bootstrap(BootstapContext<ConfiguredWorldCarver<?>> pContext) {
        HolderGetter<Block> holdergetter = pContext.lookup(Registries.BLOCK);
        pContext.register(
                CARVER_KEY,
                CUSTOM_CARVER.configured(
                        new CaveCarverConfiguration(
                                0.95F,  // probability
                                UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(200)),      // y
                                UniformFloat.of(0.65F, 0.95F),  // y scale
                                VerticalAnchor.aboveBottom(4),  // lava level
                                CarverDebugSettings.of(false, Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState()), // debug
                                holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),   // replaceable
                                UniformFloat.of(0.85F, 2.0F),   // horizontal
                                UniformFloat.of(0.85F, 3.5F),   // vertical
                                UniformFloat.of(-0.1F, 0.1F)    // floor
                        )
                )
        );
    }

    private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String key, F carver) {
        return Registry.register(BuiltInRegistries.CARVER, key, carver);
    }
}
