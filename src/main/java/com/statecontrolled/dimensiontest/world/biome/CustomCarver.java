package com.statecontrolled.dimensiontest.world.biome;

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;
import com.statecontrolled.dimensiontest.world.chunk.CustomChunkGenerator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class CustomCarver extends CaveWorldCarver {
    private static final int PITCH_CHANGE_CHANCE = 16;

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
                         Aquifer pAquifer,
                         ChunkPos chunkPos,
                         CarvingMask carvingMask) {

        // define starting position
        double x = chunkPos.getBlockX(random.nextInt(16));
        double z = chunkPos.getBlockZ(random.nextInt(16));
        double y = configuration.y.sample(random, context) - 16;

        int iBound = random.nextInt(random.nextInt(random.nextInt(15) + 1) + 1);
        double horizontalRadiusMul = randomInRange(random, 1, 3);
        double verticalRadiusMul   = randomInRange(random, 1, 3);
        double check = 0;

        float thickness = 2.0F;

        Aquifer aquifer = Aquifer.createDisabled(
            (pX, pY, pZ) -> new Aquifer.FluidStatus(CustomChunkGenerator.globalGetSeaLevel(), Blocks.WATER.defaultBlockState())
        );

        for(int i = 0; i < iBound; i++) {
            WorldCarver.CarveSkipChecker carveSkipChecker = (carvingContext, rX, rY, rZ, w) -> skipCheck(rX, rY, rZ, check);

            int jBound = 1;

            if (random.nextInt(4) == 0) {
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
                float yaw   = randomOne(random);
                float pitch = randomOne(random) / 4.0F;

                // Strongly prefer straight, level tunnels on the X or Z axis.
                if (random.nextInt(PITCH_CHANGE_CHANCE) <= PITCH_CHANGE_CHANCE - 2) {
                    pitch = 0;
                }

                // This seems to be vital in determining how dense the caves are.
                int branchCount = 108 - random.nextInt(32);

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
            //
        }
        return true;
    }

    /**
     * @return -1, 0, or 1
     **/
    private int randomOne(RandomSource random) {
        return random.nextInt(3) - 1;
    }

    /**
     * Generates a random number within a given range.
     * @param min   minimum bound (inclusive)
     * @param max   maximum bound (inclusive)
     * @return      A random number between the minimum and maximum
     **/
    private int randomInRange(RandomSource random, int min, int max) {
        return random.nextInt(min, max +1);
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

        // Create tunnels on Z-axis or X-axis
        RandomSource random = RandomSource.create(seed);
        int rBranchCountTest = random.nextInt(branchCount / 2) + branchCount / 4;
        // Check determines if X, Y, or Z are to be incremented
        int check = random.nextInt(16); // 0 - 15

        for(int i = branchIndex; i < branchCount; i++) {

            if (check < 7) {
                z += 1;
            } else if (check >=7 && check < 14) {
                x += 1;
            } else {
                y += 1;
            }

            y += pitch;

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

            if (random.nextInt(4) != 0) {
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
        // END TUNNELS
    }

    /**
     * Reconfigured to carve squares
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

            int i       = Math.max(Mth.floor(x - horizontalRadius) - minBlockX - 1, 0);
            int j       = Math.max(Mth.floor(z - horizontalRadius) - minBlockZ - 1, 0);
            int k       = Math.max(Mth.floor(y - verticalRadius)   - 1, context.getMinGenY() + 1);

            int xBound  = Math.min(Mth.floor(x + horizontalRadius) - minBlockX, 15);
            int yBound  = Math.min(Mth.floor(y + verticalRadius)   + 1, context.getMinGenY() + context.getGenDepth() - 1 - 7);
            int zBound  = Math.min(Mth.floor(z + horizontalRadius) - minBlockZ, 15);
            boolean flag = false;

            BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos checkPosition = new BlockPos.MutableBlockPos();

            for(int mX = i; mX <= xBound; mX++) {
                int posX = chunkpos.getBlockX(mX);

                for(int mZ = j; mZ <= zBound; mZ++) {
                    int posZ = chunkpos.getBlockZ(mZ);

                    MutableBoolean surfaceCheck = new MutableBoolean(false);

                    for(int mY = yBound; mY > k; mY--) {
                        carvingMask.set(mX, mY, mZ);
                        position.set(posX, mY, posZ);

                        BlockState blockstate = chunkAccess.getBlockState(position);
                        // TODO adjust for custom blocks
                        if (!blockstate.is(Blocks.GRANITE)) {
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
            }
            return flag;
        } else {
            return false;
        }
    }

    private static boolean skipCheck(double x, double y, double z, double check) {
        if (y < check) {
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
