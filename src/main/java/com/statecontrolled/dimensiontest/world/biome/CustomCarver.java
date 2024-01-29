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
import net.minecraft.util.valueproviders.UniformFloat;
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
    public boolean carve(CarvingContext pContext,
                         CaveCarverConfiguration pConfig,
                         ChunkAccess pChunk,
                         Function<BlockPos, Holder<Biome>> pBiomeAccessor,
                         RandomSource pRandom,
                         Aquifer pAquifer,
                         ChunkPos pChunkPos,
                         CarvingMask pCarvingMask) {

        int i = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
        int j = pRandom.nextInt(pRandom.nextInt(pRandom.nextInt(this.getCaveBound()) + 1) + 1);

        for(int k = 0; k < j; ++k) {
            double d0 = pChunkPos.getBlockX(pRandom.nextInt(16));
            double d1 = pConfig.y.sample(pRandom, pContext);
            double d2 = pChunkPos.getBlockZ(pRandom.nextInt(16));
            double d3 = pConfig.horizontalRadiusMultiplier.sample(pRandom);
            double d4 = pConfig.verticalRadiusMultiplier.sample(pRandom);
            double d5 = UniformFloat.of(-0.1F, 0.1F).sample(pRandom);
            
            WorldCarver.CarveSkipChecker worldcarver$carveskipchecker = (p_159202_, p_159203_, p_159204_, p_159205_, p_159206_) ->
                    shouldSkip(p_159203_, p_159204_, p_159205_, d5);

            int l = 1;
            if (pRandom.nextInt(4) == 0) {
                double d6 = pConfig.yScale.sample(pRandom);
                float f1 = 1.0F + pRandom.nextFloat() * 6.0F;
                this.createRoom(pContext, pConfig, pChunk, pBiomeAccessor, pAquifer, d0, d1, d2, f1, d6, pCarvingMask, worldcarver$carveskipchecker);
                l += pRandom.nextInt(4);
            }

            for(int k1 = 0; k1 < l; ++k1) {
                float f = pRandom.nextFloat() * (float) (Math.PI * 2);
                float f3 = (pRandom.nextFloat() - 0.5F) / 4.0F;
                float f2 = this.getThickness(pRandom);
                int i1 = i - pRandom.nextInt(i / 4);
                this.createTunnel(
                        pContext,
                        pConfig,
                        pChunk,
                        pBiomeAccessor,
                        pRandom.nextLong(),
                        pAquifer,
                        d0,
                        d1,
                        d2,
                        d3,
                        d4,
                        f2,
                        f,
                        f3,
                        0,
                        i1,
                        this.getYScale(),
                        pCarvingMask,
                        worldcarver$carveskipchecker
                );
            }
        }
        return true;
    }

    @Override
    public void createRoom(CarvingContext pContext,
                              CaveCarverConfiguration pConfig,
                              ChunkAccess pChunk,
                              Function<BlockPos, Holder<Biome>> pBiomeAccessor,
                              Aquifer pAquifer,
                              double pX,
                              double pY,
                              double pZ,
                              float pRadius,
                              double pHorizontalVerticalRatio,
                              CarvingMask pCarvingMask,
                              WorldCarver.CarveSkipChecker pSkipChecker) {

        double horizontalRadius = 1.5 + (double) (Mth.sin((float) (Math.PI / 2)) * pRadius);
        double verticalRadius = horizontalRadius * pHorizontalVerticalRatio;
        this.carveEllipsoid(pContext, pConfig, pChunk, pBiomeAccessor, pAquifer, pX + 1.0, pY, pZ, horizontalRadius, verticalRadius, pCarvingMask, pSkipChecker);
    }

    @Override
    public void createTunnel(CarvingContext pContext,
                                CaveCarverConfiguration pConfig,
                                ChunkAccess pChunk,
                                Function<BlockPos, Holder<Biome>> pBiomeAccessor,
                                long pSeed,
                                Aquifer pAquifer,
                                double pX,
                                double pY,
                                double pZ,
                                double pHorizontalRadiusMultiplier,
                                double pVerticalRadiusMultiplier,
                                float pThickness,
                                float pYaw,
                                float pPitch,
                                int pBranchIndex,
                                int pBranchCount,
                                double pHorizontalVerticalRatio,
                                CarvingMask pCarvingMask,
                                WorldCarver.CarveSkipChecker pSkipChecker) {

        RandomSource randomsource = RandomSource.create(pSeed);
        int b = randomsource.nextInt(pBranchCount / 2) + pBranchCount / 4;
        boolean flag = randomsource.nextInt(6) == 0;
        float f = 0.0F;
        float f1 = 0.0F;

        for(int j = pBranchIndex; j < pBranchCount; ++j) {
            double d0 = 1.5 + (double)(Mth.sin((float) Math.PI * (float)j / (float)pBranchCount) * pThickness);
            double d1 = d0 * pHorizontalVerticalRatio;
            float f2 = Mth.cos(pPitch);
            pX += Mth.cos(pYaw) * f2;
            pY += Mth.sin(pPitch);
            pZ += Mth.sin(pYaw) * f2;
            pPitch *= flag ? 0.92F : 0.7F;
            pPitch += f1 * 0.1F;
            pYaw += f * 0.1F;
            f1 *= 0.9F;
            f *= 0.75F;
            f1 += (randomsource.nextFloat() - randomsource.nextFloat()) * randomsource.nextFloat() * 2.0F;
            f += (randomsource.nextFloat() - randomsource.nextFloat()) * randomsource.nextFloat() * 4.0F;
            if (j == b && pThickness > 1.0F) {
                for (int i = 0; i < 2; i++) {
                    this.createTunnel(
                            pContext,
                            pConfig,
                            pChunk,
                            pBiomeAccessor,
                            randomsource.nextLong(),
                            pAquifer,
                            pX,
                            pY,
                            pZ,
                            pHorizontalRadiusMultiplier,
                            pVerticalRadiusMultiplier,
                            randomsource.nextFloat() * 0.5F + 0.5F,
                            pYaw - (float) (Math.PI / 2),
                            pPitch / 3.0F,
                            j,
                            pBranchCount,
                            1.0,
                            pCarvingMask,
                            pSkipChecker
                    );
                }

                return;
            }

            if (randomsource.nextInt(4) != 0) {
                if (!canReach(pChunk.getPos(), pX, pZ, j, pBranchCount, pThickness)) {
                    return;
                }

                this.carveEllipsoid(
                        pContext,
                        pConfig,
                        pChunk,
                        pBiomeAccessor,
                        pAquifer,
                        pX,
                        pY,
                        pZ,
                        d0 * pHorizontalRadiusMultiplier,
                        d1 * pVerticalRadiusMultiplier,
                        pCarvingMask,
                        pSkipChecker
                );
            }
        }
    }

    /**
     * Reconfigure to carve squares
     **/
    @Override
    public boolean carveEllipsoid(CarvingContext pContext,
                                  CaveCarverConfiguration pConfig,
                                  ChunkAccess pChunk,
                                  Function<BlockPos, Holder<Biome>> pBiomeAccessor,
                                  Aquifer pAquifer,
                                  double pX,
                                  double pY,
                                  double pZ,
                                  double pHorizontalRadius,
                                  double pVerticalRadius,
                                  CarvingMask pCarvingMask,
                                  WorldCarver.CarveSkipChecker pSkipChecker) {

        ChunkPos chunkpos = pChunk.getPos();
        double d0 = chunkpos.getMiddleBlockX();
        double d1 = chunkpos.getMiddleBlockZ();
        double d2 = 16.0 + pHorizontalRadius * 2.0;
        if (!(Math.abs(pX - d0) > d2) && !(Math.abs(pZ - d1) > d2)) {
            int i = chunkpos.getMinBlockX();
            int j = chunkpos.getMinBlockZ();
            int k = Math.max(Mth.floor(pX - pHorizontalRadius) - i - 1, 0);
            int l = Math.min(Mth.floor(pX + pHorizontalRadius) - i, 15);
            int i1 = Math.max(Mth.floor(pY - pVerticalRadius) - 1, pContext.getMinGenY() + 1);
            int j1 = pChunk.isUpgrading() ? 0 : 7;
            int k1 = Math.min(Mth.floor(pY + pVerticalRadius) + 1, pContext.getMinGenY() + pContext.getGenDepth() - 1 - j1);
            int l1 = Math.max(Mth.floor(pZ - pHorizontalRadius) - j - 1, 0);
            int i2 = Math.min(Mth.floor(pZ + pHorizontalRadius) - j, 15);
            boolean flag = false;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

            for(int j2 = k; j2 <= l; ++j2) {
                int k2 = chunkpos.getBlockX(j2);
                double d3 = ((double)k2 + 0.5 - pX) / pHorizontalRadius;

                for(int l2 = l1; l2 <= i2; ++l2) {
                    int i3 = chunkpos.getBlockZ(l2);
                    double d4 = ((double)i3 + 0.5 - pZ) / pHorizontalRadius;
                    if (!(d3 * d3 + d4 * d4 >= 1.0)) {
                        MutableBoolean mutableboolean = new MutableBoolean(false);

                        for(int j3 = k1; j3 > i1; --j3) {
                            double d5 = ((double)j3 - 0.5 - pY) / pVerticalRadius;
                            if (!pSkipChecker.shouldSkip(pContext, d3, d5, d4, j3) && (!pCarvingMask.get(j2, j3, l2))) {
                                pCarvingMask.set(j2, j3, l2);
                                blockpos$mutableblockpos.set(k2, j3, i3);
                                flag |= this.carveBlock(
                                        pContext,
                                        pConfig,
                                        pChunk,
                                        pBiomeAccessor,
                                        pCarvingMask,
                                        blockpos$mutableblockpos,
                                        blockpos$mutableblockpos1,
                                        pAquifer,
                                        mutableboolean
                                );
                            }
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
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
