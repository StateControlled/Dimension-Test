package com.statecontrolled.dimensiontest.world.dimension;

import java.util.List;
import java.util.OptionalLong;

import com.mojang.datafixers.util.Pair;
import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.world.biome.ModBiomes;
import com.statecontrolled.dimensiontest.world.chunk.DimChunkGenerator;

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
    public static final ResourceKey<LevelStem> M_LEVEL_STEM = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(DimensionTest.MOD_ID, "m_dimension"));

    public static final ResourceKey<Level>          M_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
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
                        1.0,    // coordinate scale
                        true,   // bedWorks
                        false,  // respawn anchor works
                        -64,    // min Y
                        384,    // height
                        320,    // logical height
                        BlockTags.INFINIBURN_OVERWORLD,
                        BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                        0.01f,  // ambient light
                        new DimensionType.MonsterSettings(false, false, ConstantInt.of(4), 4)
                )
        );
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Init LevelStem");

        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);

        //HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);

        //DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Setup layers");

        //List<FlatLayerInfo> layerInfo = getFlatLayerInfo();

//        Set<ResourceKey<StructureSet>> underground = Set.of(
//                ModStructures.CORRIDOR_CROSS,
//                ModStructures.CORRIDOR_EW,
//                ModStructures.CORRIDOR_NS
//        );

//        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Define Structures");

        // STRUCTURES
//        HolderGetter<StructureSet> structures = context.lookup(Registries.STRUCTURE_SET);
//        Set<ResourceKey<StructureSet>> vanillaUnderground = ImmutableSet.of(
//                BuiltinStructureSets.MINESHAFTS,
//                BuiltinStructureSets.ANCIENT_CITIES
//        );
//        HolderSet.Direct<StructureSet> direct = HolderSet.direct(
//            underground.stream()
//                .map(structures::getOrThrow)
//                .collect(Collectors.toList())
//        );

        // Define single biome for flatland generation
//        Holder<Biome> biomeSet = biomeRegistry.getOrThrow(ModBiomes.BIOME_ONE);

        // FLATLEVELGENERATOR SETTINGS
//        FlatLevelGeneratorSettings settings
//             = new FlatLevelGeneratorSettings(
//                Optional.of(direct),
//                biomeSet,
//                List.of()
//            ).withBiomeAndLayers(layerInfo, Optional.of(direct), biomeSet);

        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Define Biome Sources");
        MultiNoiseBiomeSource biomeSource = MultiNoiseBiomeSource.createFromList(
                new Climate.ParameterList<>(
                        List.of(
                                Pair.of(Climate.parameters(0.65F, 0.5F, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f), biomeRegistry.getOrThrow(ModBiomes.BIOME_ONE)),
                                Pair.of(Climate.parameters(0.85F, 0.7F, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f), biomeRegistry.getOrThrow(ModBiomes.BIOME_TWO)),
                                Pair.of(Climate.parameters(0.75F, 0.6F, 0.0F, 0.0f, 0.0f, 1.0f, 0.0f), biomeRegistry.getOrThrow(ModBiomes.BIOME_THREE))
                        )
                )
        );

        HolderGetter<NoiseGeneratorSettings> noiseGeneratorSettings = context.lookup(Registries.NOISE_SETTINGS);

        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Define Chunk Generator");

        // VANILLA FLATLAND GENERATION
//        FlatLevelSource flatlandChunkGenerator = new FlatLevelSource(settings);
//        LevelStem levelStem = new LevelStem(dimensionTypes.getOrThrow(M_DIMENSION_TYPE), flatlandChunkGenerator);

        // CUSTOM FLATLAND GENERATION
//        TestChunkGenerator testChunk = new TestChunkGenerator(settings, biomeSource);
//        LevelStem levelStem = new LevelStem(dimensionTypes.getOrThrow(M_DIMENSION_TYPE), testChunk);

        // CUSTOM NOISE GENERATION
        DimChunkGenerator customChunk = new DimChunkGenerator(biomeSource, noiseGeneratorSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD));
        LevelStem levelStem = new LevelStem(dimensionTypes.getOrThrow(M_DIMENSION_TYPE), customChunk);

        // FINISH
        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Register Dimension");
        context.register(M_LEVEL_STEM, levelStem);
    }

//    @NotNull
//    private static List<FlatLayerInfo> getFlatLayerInfo() {
//        FlatLayerInfo layer0 = new FlatLayerInfo(1, Blocks.BEDROCK);
//        FlatLayerInfo layer1 = new FlatLayerInfo(63, Blocks.DEEPSLATE);
//        FlatLayerInfo layer2 = new FlatLayerInfo(63, Blocks.STONE); // set to 319 for
//        FlatLayerInfo layer3 = new FlatLayerInfo(1, Blocks.QUARTZ_BLOCK);
//        FlatLayerInfo layer4 = new FlatLayerInfo(1, Blocks.BLACK_CONCRETE);
//
//        List<FlatLayerInfo> layerInfo = new ArrayList<>();
//        layerInfo.add(layer0); // bottom layer
//        layerInfo.add(layer1);
//        layerInfo.add(layer2);
//        layerInfo.add(layer3); // top layer
//        layerInfo.add(layer4);
//        return layerInfo;
//    }

}
