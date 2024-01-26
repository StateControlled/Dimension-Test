package com.statecontrolled.dimensiontest.world.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;

public class DimChunkGenerator extends NoiseBasedChunkGenerator {
    public static final Codec<DimChunkGenerator> CODEC =
            RecordCodecBuilder.create(
                    (generatorInstance) -> generatorInstance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
                    ).apply(generatorInstance, generatorInstance.stable(DimChunkGenerator::new))
            );

    private final List<FlatLayerInfo> LAYERS_INFO = new ArrayList<>();
    private final List<BlockState> LAYERS = Lists.newArrayList();
    private final int GENERATION_DEPTH = 384;
    private static final int SEA_LEVEL = -32;
    private final Supplier<Aquifer.FluidPicker> globalFluidPicker;

    public DimChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseGeneratorSettings) {
        super(biomeSource, noiseGeneratorSettings);
        setLayers();
        this.globalFluidPicker = Suppliers.memoize(DimChunkGenerator::createFluidPicker);
    }

    private static Aquifer.FluidPicker createFluidPicker() {
        Aquifer.FluidStatus lavaAquifer = new Aquifer.FluidStatus(-60, Blocks.LAVA.defaultBlockState());
        int seaLevel = SEA_LEVEL;
        Aquifer.FluidStatus waterAquifer = new Aquifer.FluidStatus(seaLevel, Blocks.WATER.defaultBlockState());
        //Aquifer.FluidStatus aquifer$fluidstatus2 = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
        return (x, y, z) -> y < Math.min(-60, seaLevel) ? lavaAquifer : waterAquifer;
    }

    private void setLayers() {
        //DimensionTest.LOGGER.log(Level.INFO, "Set layers of chunk generator");
        setFlatLayerInfo();
        updateLayers();
    }

    private void setFlatLayerInfo() {
        FlatLayerInfo layer0 = new FlatLayerInfo(1, Blocks.BEDROCK);
        FlatLayerInfo layer1 = new FlatLayerInfo(63, Blocks.DEEPSLATE);
        FlatLayerInfo layer2 = new FlatLayerInfo(63, Blocks.STONE); // set to 319 for deeper
        FlatLayerInfo layer3 = new FlatLayerInfo(1, Blocks.QUARTZ_BLOCK);

        LAYERS_INFO.add(layer0); // bottom layer
        LAYERS_INFO.add(layer1);
        LAYERS_INFO.add(layer2);
        LAYERS_INFO.add(layer3); // top layer
    }

    private void updateLayers() {
        this.LAYERS.clear();

        for(FlatLayerInfo flatlayerinfo : LAYERS_INFO) {
            for(int i = 0; i < flatlayerinfo.getHeight(); i++) {
                this.LAYERS.add(flatlayerinfo.getBlockState());
                //DimensionTest.LOGGER.log(Level.INFO, "Added layer " + LAYERS.get(i).getBlock());
            }
        }
    }

    public List<FlatLayerInfo> getLayerInfo() {
        return LAYERS_INFO;
    }

    /**
     * This is really just a copy of the Flatlands fillFromNoise method.
     **/
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState random,
                                                        StructureManager structureManager, ChunkAccess chunk) {
        List<BlockState> list = this.LAYERS;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        Heightmap heightmap0 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for(int i = 0; i < Math.min(chunk.getHeight(), list.size()); ++i) {
            BlockState blockstate = list.get(i);

            if (blockstate != null) {
                int y = chunk.getMinBuildHeight() + i;

                for(int x = 0; x < 16; ++x) {
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

    ////////////////////////////////////////////////////////////////////////////////
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
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {
//        ChunkPos pos = chunkAccess.getPos();
//        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Build Surface. ChunkPos : " + pos);
//
//        DimensionType t = level.dimensionType();
//        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Read Dimension Type : " + t);

    }

///////////////////////////////////////////////////////////////////////////////////////////

//    private void t() {
//        if (destinationWorld.dimension() == AbandonedUndergroundDimension.ABANDONEDUNDERGROUND_LEVEL) {
//            LevelChunk chunk = destinationWorld.getChunk(0, 0);
//            if (chunk.getStatus() != ChunkStatus.EMPTY) {
//                StructureTemplate spawnStructure = destinationWorld.getStructureManager().getOrCreate(new ResourceLocation(AbandonedUnderground.MOD_ID, "corridor_cross"));
//                if (spawnStructure != null) {
//                    spawnStructure.placeInWorld(
//                            destinationWorld,
//                            BlockPos.containing(0, 64, 0),
//                            BlockPos.containing(0, 64, 0),
//                            new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false),
//                            destinationWorld.random,
//                            3
//                    );
//                }
//            }
//        }
//    }

    // Use Carver?
//    @Override
//    public void applyCarvers(WorldGenRegion level, long seed, RandomState random,
//                             BiomeManager biomeManager, StructureManager structureManager,
//                             ChunkAccess chunk, GenerationStep.Carving step) {
//
//        BiomeManager biomemanager = biomeManager.withDifferentSource((x, y, z) -> this.biomeSource.getNoiseBiome(x, y, z, random.sampler()));
//        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
//        int radius = 8;
//        ChunkPos chunkpos = chunk.getPos();
//        NoiseChunk noisechunk = chunk.getOrCreateNoiseChunk((p_224250_) -> this.createNoiseChunk(p_224250_, structureManager, Blender.of(level), random));
//        Aquifer aquifer = noisechunk.aquifer();
//        CarvingContext carvingcontext =
//                new CarvingContext(this, level.registryAccess(),
//                        chunk.getHeightAccessorForGeneration(), noisechunk, random, this.generatorSettings().value().surfaceRule());
//
//        CarvingMask carvingmask = ((ProtoChunk) chunk).getOrCreateCarvingMask(step);
//
//        for(int j = -radius; j <= radius; ++j) {
//            for(int k = -radius; k <= radius; ++k) {
//                ChunkPos chunkpos1 = new ChunkPos(chunkpos.x + j, chunkpos.z + k);
//                ChunkAccess chunkaccess = level.getChunk(chunkpos1.x, chunkpos1.z);
//                BiomeGenerationSettings biomegenerationsettings =
//                    chunkaccess.carverBiome(() -> this.getBiomeGenerationSettings(
//                            this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkpos1.getMinBlockX()),
//                            0, QuartPos.fromBlock(chunkpos1.getMinBlockZ()), random.sampler())
//                        )
//                    );
//
//                Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = biomegenerationsettings.getCarvers(step);
//                int l = 0;
//
//                for(Holder<ConfiguredWorldCarver<?>> holder : iterable) {
//                    ConfiguredWorldCarver<?> configuredworldcarver = holder.value();
//                    worldgenrandom.setLargeFeatureSeed(seed + (long)l, chunkpos1.x, chunkpos1.z);
//
//                    if (configuredworldcarver.isStartChunk(worldgenrandom)) {
//                        configuredworldcarver.carve(carvingcontext, chunk, biomemanager::getBiome, worldgenrandom, aquifer, chunkpos1, carvingmask);
//                    }
//
//                    ++l;
//                }
//            }
//        }
//    }

//    private NoiseChunk createNoiseChunk(ChunkAccess pChunk, StructureManager pStructureManager, Blender pBlender, RandomState pRandom) {
//        return NoiseChunk.forChunk(pChunk, pRandom, Beardifier.forStructuresInChunk(pStructureManager, pChunk.getPos()),
//            this.generatorSettings().value(), this.globalFluidPicker.get(), pBlender);
//    }

    @Override
    public int getGenDepth() {
        return GENERATION_DEPTH;
    }

    @Override
    public int getSeaLevel() {
        return SEA_LEVEL;
    }

    @Override
    public Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
}
