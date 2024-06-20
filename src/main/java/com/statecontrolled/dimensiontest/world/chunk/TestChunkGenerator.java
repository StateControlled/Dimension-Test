package com.statecontrolled.dimensiontest.world.chunk;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

/**
 * Chunk generator similar to FlatLevelSource
 **/
public class TestChunkGenerator extends ChunkGenerator {
    public static final Codec<TestChunkGenerator> CODEC =
            RecordCodecBuilder.create((instance) ->
                    instance.group(
                            FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(TestChunkGenerator::getSettings),
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter((chunkGenerator) -> chunkGenerator.biomeSource)
                    ).apply(instance, instance.stable(TestChunkGenerator::new))
            );

    private final int GENERATION_DEPTH;
    private final int SEA_LEVEL;
    private final FlatLevelGeneratorSettings SETTINGS;

    /**
     * Constructor.
     * @param flatLevelGeneratorSettings    settings
     */
    public TestChunkGenerator(FlatLevelGeneratorSettings flatLevelGeneratorSettings, BiomeSource biomeSource) {
        //super(new FixedBiomeSource(flatLevelGeneratorSettings.getBiome()), Util.memoize(flatLevelGeneratorSettings::adjustGenerationSettings));
        super(biomeSource, Util.memoize(flatLevelGeneratorSettings::adjustGenerationSettings));
        this.SETTINGS = flatLevelGeneratorSettings;
        this.GENERATION_DEPTH = 384;
        this.SEA_LEVEL = -63;
        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Init Custom Chunk Generator");
    }

    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> structureSetLookup, RandomState randomState, long seed) {
        Stream<Holder<StructureSet>> stream =
                this.SETTINGS.structureOverrides().map(HolderSet::stream).orElseGet(
                        () -> structureSetLookup.listElements().map((reference) -> reference)
                );
        return ChunkGeneratorStructureState.createForFlat(randomState, seed, this.biomeSource, stream);
    }

    public FlatLevelGeneratorSettings getSettings() {
        return this.SETTINGS;
    }

    @Override
    public Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunkAccess) {

    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return level.getMinBuildHeight() + Math.min(level.getHeight(), this.SETTINGS.getLayers().size());
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        List<BlockState> list = this.SETTINGS.getLayers();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        Heightmap heightmap0 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for(int i = 0; i < Math.min(chunk.getHeight(), list.size()); ++i) {
            BlockState blockstate = list.get(i);

            if (blockstate != null) {
                int j = chunk.getMinBuildHeight() + i;

                for(int k = 0; k < 16; ++k) {
                    for(int l = 0; l < 16; ++l) {
                        chunk.setBlockState(mutableBlockPos.set(k, j, l), blockstate, false);
                        heightmap0.update(k, j, l, blockstate);
                        heightmap1.update(k, j, l, blockstate);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        List<BlockState> list = this.SETTINGS.getLayers();

        for(int i = Math.min(list.size(), level.getMaxBuildHeight()) - 1; i >= 0; --i) {
            BlockState blockstate = list.get(i);
            if (blockstate != null && type.isOpaque().test(blockstate)) {
                return level.getMinBuildHeight() + i + 1;
            }
        }
        return level.getMinBuildHeight();
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        return new NoiseColumn(
                height.getMinBuildHeight(),
                this.SETTINGS
                        .getLayers()
                        .stream()
                        .limit(height.getHeight())
                        .map((blockState) ->
                                blockState == null ? Blocks.AIR.defaultBlockState() : blockState
                        )
                        .toArray(BlockState[]::new)
        );
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        ;
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random,
                             BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {

    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getGenDepth() {
        return GENERATION_DEPTH;
    }

    @Override
    public int getSeaLevel() {
        return SEA_LEVEL;
    }
}
