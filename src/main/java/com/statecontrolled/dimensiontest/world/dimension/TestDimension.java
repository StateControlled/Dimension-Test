package com.statecontrolled.dimensiontest.world.dimension;

import java.util.List;
import java.util.OptionalLong;

import com.mojang.datafixers.util.Pair;
import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.world.biome.ModBiomes;
import com.statecontrolled.dimensiontest.world.chunk.CustomChunkGenerator;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class TestDimension {
    public static final ResourceKey<LevelStem> M_LEVEL_STEM          = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(DimensionTest.MOD_ID, "m_dimension"));

    public static final ResourceKey<Level> M_LEVEL_KEY               = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(DimensionTest.MOD_ID, "m_dimension"));

    public static final ResourceKey<DimensionType>  M_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(DimensionTest.MOD_ID, "m_dimension"));

    private TestDimension() {
        ;
    }

    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(M_DIMENSION_TYPE, new DimensionType(
                        OptionalLong.of(6000),  // freeze time at
                        true,   // hasSkylight
                        false,  // hasCeiling
                        false,  // ultrawarm
                        false,  // natural
                        16.0,   // coordinate scale
                        true,   // bedWorks
                        false,  // respawn anchor works
                        -64,    // min Y
                        384,    // height
                        320,    // logical height
                        BlockTags.INFINIBURN_OVERWORLD,
                        BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                        0.1f,  // ambient light
                        new DimensionType.MonsterSettings(false, false, ConstantInt.of(4), 4)
                )
        );
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);

        MultiNoiseBiomeSource biomeSource = MultiNoiseBiomeSource.createFromList(
                new Climate.ParameterList<>(
                        List.of(
                                Pair.of(Climate.parameters(0.75F, 0.6F, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f), biomeRegistry.getOrThrow(ModBiomes.BIOME_ONE)),
                                Pair.of(Climate.parameters(0.80F, 0.7F, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f), biomeRegistry.getOrThrow(ModBiomes.BIOME_TWO)),
                                Pair.of(Climate.parameters(0.85F, 0.8F, 0.0F, 0.0f, 0.0f, 1.0f, 0.0f), biomeRegistry.getOrThrow(ModBiomes.BIOME_THREE))
                        )
                )
        );

        HolderGetter<NoiseGeneratorSettings> noiseGeneratorSettings = context.lookup(Registries.NOISE_SETTINGS);

        // CUSTOM NOISE GENERATION
        CustomChunkGenerator customChunk = new CustomChunkGenerator(biomeSource, noiseGeneratorSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD));
        LevelStem levelStem = new LevelStem(dimensionTypes.getOrThrow(M_DIMENSION_TYPE), customChunk);

        // FINISH
        context.register(M_LEVEL_STEM, levelStem);
    }

}
