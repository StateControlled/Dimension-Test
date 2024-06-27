package com.statecontrolled.dimensiontest.world.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

/**
 * CustomChunkGenerator class is based on {@code FlatLevelSource} and {@code NoiseBasedChunkGenerator}.
 * It generates a flatlands-style world.
 *
 * @see net.minecraft.world.level.levelgen.FlatLevelSource
 * @see NoiseBasedChunkGenerator
 **/
public class TestChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<TestChunkGenerator> CODEC =
            RecordCodecBuilder.mapCodec(
                    (generatorInstance) -> generatorInstance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
                    ).apply(generatorInstance, generatorInstance.stable(TestChunkGenerator::new))
            );

    private static final Block DEFAULT_CEILING = Blocks.BEDROCK;
    private static final Block DEFAULT_BASE = Blocks.BEDROCK;
    private final List<FlatLayerInfo> LAYERS_INFO = new ArrayList<>();
    private final List<BlockState> LAYERS = Lists.newArrayList();

    /**
     * Constructs a new instance of the CustomChunkGenerator.
     */
    public TestChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseGeneratorSettings) {
        super(biomeSource, noiseGeneratorSettings);
        setLayers();
    }

    /**
     * Defines the block layers that the {@link TestChunkGenerator} will use to build its chunks.
     */
    private void setLayers() {
        setFlatLayerInfo();
        updateLayers();
    }

    private void setFlatLayerInfo() {
        FlatLayerInfo layer0 = new FlatLayerInfo(1, DEFAULT_BASE);
        FlatLayerInfo layer1 = new FlatLayerInfo(63, Blocks.POLISHED_BLACKSTONE);
        FlatLayerInfo layer2 = new FlatLayerInfo(63, Blocks.QUARTZ_BLOCK);
        FlatLayerInfo layer3 = new FlatLayerInfo(1, DEFAULT_CEILING);

        LAYERS_INFO.add(layer0); // bottom layer
        LAYERS_INFO.add(layer1);
        LAYERS_INFO.add(layer2);
        LAYERS_INFO.add(layer3); // top layer
    }

    /**
     * Related to {@link FlatLevelGeneratorSettings#updateLayers()} method.
     */
    private void updateLayers() {
        this.LAYERS.clear();

        for(FlatLayerInfo flatlayerinfo : LAYERS_INFO) {
            for(int i = 0; i < flatlayerinfo.getHeight(); i++) {
                this.LAYERS.add(flatlayerinfo.getBlockState());
            }
        }
    }

    public List<FlatLayerInfo> getLayerInfo() {
        return LAYERS_INFO;
    }

    public int getDepth() {
        return LAYERS.size();
    }

    /**
     * This is really just a slightly modified copy of the {@link net.minecraft.world.level.levelgen.FlatLevelSource FlatLevelSource}
     * {@link net.minecraft.world.level.levelgen.FlatLevelSource#fillFromNoise(Blender, RandomState, StructureManager, ChunkAccess) fillFromNoise()} method.
     **/
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender,
                                                        RandomState random,
                                                        StructureManager structureManager,
                                                        ChunkAccess chunk) {
        List<BlockState> list = this.LAYERS;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        Heightmap heightmap0 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for(int i = 0; i < Math.min(chunk.getHeight(), list.size()); ++i) {
            BlockState blockstate = list.get(i);

            if (blockstate != null) {
                int y = chunk.getMinBuildHeight() + i;

                for(int x = 0; x < 16; ++x) { // 16 is from chunk dimensions : 16 x 16
                    for(int z = 0; z < 16; ++z) {
                        chunk.setBlockState(mutableBlockPos.set(x, y, z), blockstate, false);
                        heightmap0.update(x, y, z, blockstate);
                        heightmap1.update(x, y, z, blockstate);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    /**
     * Copy of FlatLevelSource method.
     **/
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        List<BlockState> list = this.LAYERS;

        for(int i = Math.min(list.size(), level.getMaxBuildHeight()) - 1; i >= 0; --i) {
            BlockState blockstate = list.get(i);
            if (blockstate != null && type.isOpaque().test(blockstate)) {
                return level.getMinBuildHeight() + i + 1;
            }
        }
        return level.getMinBuildHeight();
    }

    @Override
    public MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public static Block getDefaultCeiling() {
        return DEFAULT_CEILING;
    }

    public static Block getDefaultBase() {
        return DEFAULT_BASE;
    }

}
