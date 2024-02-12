package com.statecontrolled.dimensiontest.world.biome;

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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

    public CustomCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    /**
     * Objective : reduce randomness to force tunnels and rooms to be rectangular with flat floors.
     **/
    @Override
    public boolean carve(CarvingContext context,
                         CaveCarverConfiguration configuration,
                         ChunkAccess chunkAccess,
                         Function<BlockPos, Holder<Biome>> biomeAccessor,
                         RandomSource random,
                         Aquifer aquifer,
                         ChunkPos chunkPos,
                         CarvingMask carvingMask) {

        int iBound = random.nextInt(random.nextInt(random.nextInt(15) + 1) + 1) + 1;

        // define starting position
        double x = chunkPos.getBlockX(random.nextInt(16));
        double y = configuration.y.sample(random, context) - 16;
        double z = chunkPos.getBlockZ(random.nextInt(16));

        if (y < 32) {
            y += 16;
        }

        double horizontalRadiusMul = 1.5;
        double verticalRadiusMul   = 1.5;
        double check = 0.5;

        float thickness = 2.0F;

        for(int i = 0; i < iBound; i++) {
            WorldCarver.CarveSkipChecker carveSkipChecker = (carvingContext, rX, rY, rZ, w) -> skipCheck(rX, rY, rZ, check);

            int jBound = 1;

            if (random.nextInt(3) == 0) {
                double horizontalVerticalRatio = configuration.yScale.sample(random);
                float radius = 1.0F + (random.nextFloat() * 8.0F);

                this.createRoom(
                        context,
                        configuration,
                        chunkAccess,
                        biomeAccessor,
                        aquifer,
                        x,
                        y,
                        z,
                        radius,
                        horizontalVerticalRatio,
                        carvingMask,
                        carveSkipChecker
                );

                jBound += random.nextInt(6);
            }

            for(int j = 0; j < jBound; j++) {
                float yaw       = randomOne(random);
                float pitch     = randomOne(random);

                int branchCount = 132 - random.nextInt(32);

                this.createTunnel(
                    context,
                    configuration,
                    chunkAccess,
                    biomeAccessor,
                    random.nextLong(),  // seed
                    aquifer,
                    x,
                    y,
                    z,
                    horizontalRadiusMul,
                    verticalRadiusMul,
                    thickness,
                    yaw,
                    pitch,
                    0,                  // branchIndex
                    branchCount,        // branchCount
                    this.getYScale(),   // horizontalVerticalRatio
                    carvingMask,
                    carveSkipChecker
                );
            }
        }
        return true;
    }

    private int randomOne(RandomSource random) {
        return random.nextInt(3) - 1;
    }

    @Override
    public void createRoom(CarvingContext context,
                            CaveCarverConfiguration configuration,
                            ChunkAccess chunkAccess,
                            Function<BlockPos, Holder<Biome>> biomeAccessor,
                            Aquifer aquifer,
                            double x,
                            double y,
                            double z,
                            float radius,
                            double horizontalVerticalRatio,
                            CarvingMask carvingMask,
                            WorldCarver.CarveSkipChecker skipChecker) {

        double horizontalRadius = 1.5 * radius;
        double verticalRadius = horizontalRadius * horizontalVerticalRatio;

        this.carveEllipsoid(
            context,
            configuration,
            chunkAccess,
            biomeAccessor,
            aquifer,
            x,
            y,
            z,
            horizontalRadius,
            verticalRadius,
            carvingMask,
            skipChecker
        );

    }

    @Override
    public void createTunnel(CarvingContext context,
                                CaveCarverConfiguration configuration,
                                ChunkAccess chunkAccess,
                                Function<BlockPos, Holder<Biome>> biomeAccessor,
                                long seed,
                                Aquifer aquifer,
                                double x,
                                double y,
                                double z,
                                double horizontalRadiusMultiplier,
                                double verticalRadiusMultiplier,
                                float thickness,
                                float yaw,
                                float pitch,
                                int branchIndex,
                                int branchCount,
                                double horizontalVerticalRatio,
                                CarvingMask carvingMask,
                                WorldCarver.CarveSkipChecker skipChecker) {

        RandomSource random = RandomSource.create(seed);
        int rBranchCountTest = random.nextInt(branchCount / 2) + branchCount / 4;

        // Create tunnels on Z-axis
        for(int i = branchIndex; i < branchCount; i++) {
            z += 1;

            y += randomOne(random);

            if (i == rBranchCountTest && thickness > 1.0F) {
                for (int t = 0; t < 2; t++) {
                    this.createTunnel(
                            context,
                            configuration,
                            chunkAccess,
                            biomeAccessor,
                            random.nextLong(),
                            aquifer,
                            x,
                            y,
                            z,
                            horizontalRadiusMultiplier,
                            verticalRadiusMultiplier,
                            thickness,
                            yaw,
                            pitch,
                            i,
                            branchCount,
                            1.0,
                            carvingMask,
                            skipChecker
                    );
                }
                return;
            }

            if (random.nextInt(3) != 0) {
                // i = branchIndex
                if (!canReach(chunkAccess.getPos(), x, z, i, branchCount, thickness)) {
                    return;
                }

                this.carveEllipsoid(
                        context,
                        configuration,
                        chunkAccess,
                        biomeAccessor,
                        aquifer,
                        x,
                        y,
                        z,
                        horizontalRadiusMultiplier,
                        verticalRadiusMultiplier,
                        carvingMask,
                        skipChecker
                );
            }
        }
        // END Z

        // Create tunnels on X-axis
        rBranchCountTest = random.nextInt(branchCount / 2) + branchCount / 4;

        for(int i = branchIndex; i < branchCount; i++) {
            x += 1;

            y += randomOne(random);

            if (i == rBranchCountTest && thickness > 1.0F) {
                for (int t = 0; t < 3; t++) {
                    this.createTunnel(
                            context,
                            configuration,
                            chunkAccess,
                            biomeAccessor,
                            random.nextLong(),
                            aquifer,
                            x,
                            y,
                            z,
                            horizontalRadiusMultiplier,
                            verticalRadiusMultiplier,
                            thickness,
                            yaw,
                            pitch,
                            i,
                            branchCount,
                            1.0,
                            carvingMask,
                            skipChecker
                    );
                }
                return;
            }

            if (random.nextInt(3) != 0) {
                // i = branchIndex
                if (!canReach(chunkAccess.getPos(), x, z, i, branchCount, thickness)) {
                    return;
                }

                this.carveEllipsoid(
                        context,
                        configuration,
                        chunkAccess,
                        biomeAccessor,
                        aquifer,
                        x,
                        y,
                        z,
                        horizontalRadiusMultiplier,
                        verticalRadiusMultiplier,
                        carvingMask,
                        skipChecker
                );
            }
        }
        // END X

    }

    /**
     * Reconfigure to carve squares
     **/
    @Override
    public boolean carveEllipsoid(CarvingContext context,
                                  CaveCarverConfiguration configuration,
                                  ChunkAccess chunkAccess,
                                  Function<BlockPos, Holder<Biome>> biomeAccessor,
                                  Aquifer aquifer,
                                  double x,
                                  double y,
                                  double z,
                                  double horizontalRadius,
                                  double verticalRadius,
                                  CarvingMask carvingMask,
                                  WorldCarver.CarveSkipChecker skipChecker) {

        ChunkPos chunkpos = chunkAccess.getPos();
        double midX = chunkpos.getMiddleBlockX();
        double midZ = chunkpos.getMiddleBlockZ();
        double check = 16.0 + horizontalRadius * 2.0;

        if (!(Math.abs(x - midX) > check) && !(Math.abs(z - midZ) > check)) {
            int minBlockX = chunkpos.getMinBlockX();
            int minBlockZ = chunkpos.getMinBlockZ();

            int i = Math.max(Mth.floor(x - horizontalRadius) - minBlockX - 1, 0);
            int xBound = Math.min(Mth.floor(x + horizontalRadius) - minBlockX, 15);
            int k = Math.max(Mth.floor(y - verticalRadius) - 1, context.getMinGenY() + 1);

            int yBound = Math.min(Mth.floor(y + verticalRadius) + 1, context.getMinGenY() + context.getGenDepth() - 1 - 7);
            int j = Math.max(Mth.floor(z - horizontalRadius) - minBlockZ - 1, 0);
            int zBound = Math.min(Mth.floor(z + horizontalRadius) - minBlockZ, 15);
            boolean flag = false;

            BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos checkPosition = new BlockPos.MutableBlockPos();

            for(int maskX = i; maskX <= xBound; maskX++) {
                int posX = chunkpos.getBlockX(maskX);

                for(int maskZ = j; maskZ <= zBound; maskZ++) {
                    int posZ = chunkpos.getBlockZ(maskZ);

                    MutableBoolean surfaceCheck = new MutableBoolean(false);

                    for(int maskY = yBound; maskY > k; maskY--) {
                        carvingMask.set(maskX, maskY, maskZ);
                        position.set(posX, maskY, posZ);
                        flag |= this.carveBlock(
                                context,
                                configuration,
                                chunkAccess,
                                biomeAccessor,
                                carvingMask,
                                position,
                                checkPosition,
                                aquifer,
                                surfaceCheck
                        );
                    }

                }
            }
            return flag;
        } else {
            return false;
        }
    }

    private static boolean skipCheck(double x, double y, double z, double maxY) {
        if (y < maxY) {
            return true;
        } else {
            return y >= 1.0;
        }
    }

    @Override
    public double getYScale() {
        return 1.0;
    }

}

