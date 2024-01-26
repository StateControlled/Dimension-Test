package com.statecontrolled.dimensiontest.world.biome;

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;
import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class CustomCarver extends CaveWorldCarver {

    public CustomCarver(Codec<CaveCarverConfiguration> pCodec) {
        super(pCodec);
    }

    /**
     * Objective : reduce randomness to force tunnels and rooms to be rectangular with flat floors.
     **/
    @Override
    public boolean carve(CarvingContext context, CaveCarverConfiguration configuration, ChunkAccess chunkAccess,
                         Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource randomSource,
                         Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {

        DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Test Custom Carver");
        int i = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1) + 1;
        int j = randomSource.nextInt(randomSource.nextInt(randomSource.nextInt(16)) + 1);

        for(int k = 0; k < j; k++) {
            double rBlockX = chunkPos.getBlockX(randomSource.nextInt(16));
            double rBlockY = configuration.y.sample(randomSource, context);
            double rBlockZ = chunkPos.getBlockZ(randomSource.nextInt(16));
            double rHoz = configuration.horizontalRadiusMultiplier.sample(randomSource);
            double rVer = configuration.verticalRadiusMultiplier.sample(randomSource);
            int d5 = 48;

            WorldCarver.CarveSkipChecker carveskipchecker = (cContext, a, b, c, ign) -> shouldSkip(a, b, c, d5);

            int mLimit = 1;
            if (randomSource.nextInt(4) == 0) {
                double rScaleY = configuration.yScale.sample(randomSource) + 1.0;
                float rRadius = 1.0F + (randomSource.nextFloat() * 8.0F);

                this.createRoom(context, configuration, chunkAccess, biomeAccessor, aquifer,
                        rBlockX, rBlockY, rBlockZ, rRadius, rScaleY, carvingMask, carveskipchecker
                );

                mLimit += randomSource.nextInt(4);
            }

            int branchIndex = 0;
            for(int m = 0; m < mLimit; m++) {
                float yaw       = 0; //randomSource.nextFloat() * ((float) Math.PI * 2.0F);
                float pitch     = 0; // (randomSource.nextFloat() - 0.5F) / 4.0F;
                float thickness = randomSource.nextInt(5, 11); //this.getThickness(randomSource);
                int branchCount = Math.abs(i - randomSource.nextInt(i / 4));
                long seed       = randomSource.nextLong();
                double rScaleY  = this.getYScale();

                this.createTunnel(
                        context, configuration, chunkAccess, biomeAccessor,
                        seed, aquifer, rBlockX, rBlockY, rBlockZ,
                        rHoz, rVer, thickness, yaw, pitch, branchIndex, branchCount,
                        rScaleY, carvingMask, carveskipchecker
                );
            }
        }
        return true;
    }

    @Override
    protected void createRoom(CarvingContext context, CaveCarverConfiguration config, ChunkAccess pChunk,
                              Function<BlockPos, Holder<Biome>> pBiomeAccessor, Aquifer pAquifer,
                              double x, double y, double z, float radius,
                              double horizontalVerticalRatio, CarvingMask carvingMask, WorldCarver.CarveSkipChecker skipChecker) {

        double d0 = 1.0 + radius;
        double d1 = d0 * horizontalVerticalRatio;
        this.carveCube(context, config, pChunk, pBiomeAccessor, pAquifer, x + 1, y, z + 1, d0, d1, carvingMask, skipChecker);
    }

    @Override
    protected void createTunnel(CarvingContext context, CaveCarverConfiguration config, ChunkAccess chunk,
                                Function<BlockPos, Holder<Biome>> biomeAccessor, long seed,
                                Aquifer aquifer, double x, double y, double z,
                                double horizontalRadiusMultiplier, double verticalRadiusMultiplier,
                                float thickness, float yaw, float pitch, int branchIndex,
                                int branchCount, double horizontalVerticalRatio,
                                CarvingMask carvingMask, WorldCarver.CarveSkipChecker skipChecker) {

        RandomSource randomsource = RandomSource.create(seed);
        int i = randomsource.nextInt(branchCount / 2) + (branchCount / 4);
        float yawAdjust = 0.0F;

        for(int j = branchIndex; j < branchCount; ++j) {
            double d0 = 1.5 + (double) (Mth.sin((float) (Math.PI * (float) j) / (float) branchCount) * thickness);
            double d1 = d0 * horizontalVerticalRatio;
            float h = Mth.cos(pitch);

            x += h;
            z += h;
            System.out.println(x + ", " + z);
            yaw += (yawAdjust * 0.1F);
            yawAdjust += (randomsource.nextFloat() - randomsource.nextFloat()) * randomsource.nextFloat() * 4.0F;

            if (j == i && thickness > 1.0F) {
                for (int c = 0; c < 2; c++) {
                    this.createTunnel(
                            context, config, chunk, biomeAccessor, randomsource.nextLong(),
                            aquifer, x, y, z, horizontalRadiusMultiplier, verticalRadiusMultiplier,
                            randomsource.nextFloat() * 0.5F + 0.5F, yaw - ((float) Math.PI / 2.0F), pitch / 3.0F, j,
                            branchCount, 1.0D, carvingMask, skipChecker
                    );
                }
                return;
            }

            if (randomsource.nextInt(4) != 0) {
                if (!canReach(chunk.getPos(), x, z, j, branchCount, thickness)) {
                    return;
                }

                this.carveCube(
                        context, config, chunk,
                        biomeAccessor, aquifer,
                        x, y, z,
                        d0 * horizontalRadiusMultiplier,
                        d1 * verticalRadiusMultiplier,
                        carvingMask, skipChecker
                );
            }
        }

    }

    /**
     * I think this will carve a rectangular room
     **/
    private void carveCube(CarvingContext context, CaveCarverConfiguration config, ChunkAccess chunk,
                           Function<BlockPos, Holder<Biome>> biomeAccessor, Aquifer aquifer,
                           double x, double y, double z, double horizontalRadius,
                           double verticalRadius, CarvingMask carvingMask, WorldCarver.CarveSkipChecker skipChecker) {

        ChunkPos chunkpos = chunk.getPos();
        double middleX = chunkpos.getMiddleBlockX();
        double middleZ = chunkpos.getMiddleBlockZ();
        double hRadiusUp = 16 + (horizontalRadius * 2);

        if (!(Math.abs(x - middleX) > hRadiusUp) && !(Math.abs(z - middleZ) > hRadiusUp)) {
            int minX = chunkpos.getMinBlockX();
            int minZ = chunkpos.getMinBlockZ();

            int xHorizMin = Math.max(Mth.floor(x - horizontalRadius) - minX - 1, 0);
            int xHorizMax = Math.min(Mth.floor(x + horizontalRadius) - minX, 15);

            int yMinFloor = Math.max(Mth.floor(y - verticalRadius) - 2, context.getMinGenY() + 1);
            int yMaxFloor = Math.min(Mth.floor(y + verticalRadius) + 2, context.getMinGenY() + context.getGenDepth() - 8);

            int zHorizMin = Math.max(Mth.floor(z - horizontalRadius) - minZ - 1, 0);
            int zHorizMax = Math.min(Mth.floor(z + horizontalRadius) - minZ, 15);

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

            for(int a = xHorizMin; a < xHorizMax + 1; a++) {
                int blockX = chunkpos.getBlockX(a);

                for(int b = zHorizMin; b < zHorizMax + 1; b++) {
                    int blockZ = chunkpos.getBlockZ(b);

                    MutableBoolean mutableboolean = new MutableBoolean(true); // do i need this?

                    for(int c = yMaxFloor; c > yMinFloor; c--) {
                        carvingMask.set(a, c, b);
                        pos.set(blockX, c, blockZ);
                        this.carveBlock(context, config, chunk, biomeAccessor, carvingMask, pos, checkPos, aquifer, mutableboolean);
                    }
                }
            }
        }
    }

    private static boolean shouldSkip(double relativeX, double relativeY, double relativeZ, double minRelativeY) {
        if (relativeY <= minRelativeY) {
            return true;
        } else {
            return (relativeX * relativeX) + (relativeY * relativeY) + (relativeZ * relativeZ) >= 1.0;
        }
    }

}
