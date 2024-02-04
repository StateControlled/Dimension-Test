package com.statecontrolled.dimensiontest.world.biome;

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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

public class CustomCaveCarver extends CaveWorldCarver {

    public CustomCaveCarver(Codec<CaveCarverConfiguration> codec) {
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

        int j = random.nextInt(16);

        for(int k = 0; k < j; k++) {
            double blockX = chunkPos.getBlockX(random.nextInt(16));
            double blockY = configuration.y.sample(random, context);
            double blockZ = chunkPos.getBlockZ(random.nextInt(16));

            double horizontalRadiusMul = 2.0; // configuration.horizontalRadiusMultiplier.sample(random);
            double verticalRadiusMul = 2.0; // configuration.verticalRadiusMultiplier.sample(random);
            double d5 = UniformFloat.of(-0.1F, 0.1F).sample(random);

            WorldCarver.CarveSkipChecker carveSkipChecker = (carvingContext, relativeX, relativeY, relativeZ, p_159206_) ->
                    skipCheck(relativeX, relativeY, relativeZ, d5);

            int l = 1;
            if (random.nextInt(4) == 0) {
                double horizontalVerticalRatio = configuration.yScale.sample(random);
                float radius = 1.0F + random.nextFloat() * 6.0F;

                this.createRoom(
                        context,
                        configuration,
                        chunkAccess,
                        biomeAccessor,
                        aquifer,
                        blockX,
                        blockY,
                        blockZ,
                        radius,
                        horizontalVerticalRatio,
                        carvingMask,
                        carveSkipChecker
                );

                l += random.nextInt(4);
            }

            for(int k1 = 0; k1 < l; k1++) {
                float yaw       = (float) weightedRandomNumber(random) / 8; // random.nextFloat() * (float) (Math.PI * 2);
                float pitch     = (float) weightedRandomNumber(random) / 8; // (random.nextFloat() - 0.5F) / 4.0F; // -0.125, 0.125
                float thickness = 2.0F; // this.getThickness(random);
                int branchCount = 112 - random.nextInt(32); // i - random.nextInt(i / 4);

                this.createTunnel(
                        context,
                        configuration,
                        chunkAccess,
                        biomeAccessor,
                        random.nextLong(),  // seed
                        aquifer,
                        blockX,
                        blockY,
                        blockZ,
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

    /**
     * Return a random number between -1 and 1 biased towards zero
     **/
    private double weightedRandomNumber(RandomSource random) {
        double bias = 0.75;
        double gaussian = random.nextGaussian() * bias;
        return Math.max(-1.0, Math.min(1.0, gaussian));
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

        RandomSource randomSource = RandomSource.create(seed);
        int rBranchCountTest = randomSource.nextInt(branchCount / 2) + branchCount / 4;
        boolean flag = randomSource.nextInt(6) == 0;
        float f = 0.0F;
        float f1 = 0.0F;

        for(int j = branchIndex; j < branchCount; j++) {
            double d0 = 1.5 + (double) (Mth.sin((float) Math.PI * (float) j / (float) branchCount) * thickness);
            double d1 = d0 * horizontalVerticalRatio;
            float f2 = Mth.cos(pitch);
            x += Mth.cos(yaw) * f2;
            y += Mth.sin(pitch);
            z += Mth.sin(yaw) * f2;
            pitch *= flag ? 0.92F : 0.7F;
            pitch += f1 * 0.1F;
            yaw += f * 0.1F;
            f1 *= 0.9F;
            f *= 0.75F;
            f1 += (randomSource.nextFloat() - randomSource.nextFloat()) * randomSource.nextFloat() * 2.0F;
            f += (randomSource.nextFloat() - randomSource.nextFloat()) * randomSource.nextFloat() * 4.0F;

            if (j == rBranchCountTest && thickness > 1.0F) {
                for (int i = 0; i < 2; i++) {
                    this.createTunnel(
                            context,
                            configuration,
                            chunkAccess,
                            biomeAccessor,
                            randomSource.nextLong(),    // seed
                            aquifer,
                            x,
                            y,
                            z,
                            horizontalRadiusMultiplier,
                            verticalRadiusMultiplier,
                            thickness,  //randomSource.nextFloat() * 0.5F + 0.5F, // thickness
                            yaw, // yaw - 1.5F,        //(float) (Math.PI / 2),
                            pitch, // pitch / 3.0F,
                            j,          // j = branchIndex
                            branchCount,
                            1.0,        // horizontalVerticalRatio
                            carvingMask,
                            skipChecker
                    );
                }
                return;
            }

            if (randomSource.nextInt(4) != 0) {
                // j = branchIndex
                if (!canReach(chunkAccess.getPos(), x, z, j, branchCount, thickness)) {
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
                        horizontalRadiusMultiplier, // d0 * horizontalRadiusMultiplier,
                        verticalRadiusMultiplier,   // d1 * verticalRadiusMultiplier,
                        carvingMask,
                        skipChecker
                );
            }
        }
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
        double d2 = 16.0 + horizontalRadius * 2.0;

        if (!(Math.abs(x - midX) > d2) && !(Math.abs(z - midZ) > d2)) {
            int minBlockX = chunkpos.getMinBlockX();
            int minBlockZ = chunkpos.getMinBlockZ();

            int k = Math.max(Mth.floor(x - horizontalRadius) - minBlockX - 1, 0);
            int l = Math.min(Mth.floor(x + horizontalRadius) - minBlockX, 15);
            int i1 = Math.max(Mth.floor(y - verticalRadius) - 1, context.getMinGenY() + 1);

            int upgrade = chunkAccess.isUpgrading() ? 0 : 7;
            int k1 = Math.min(Mth.floor(y + verticalRadius) + 1, context.getMinGenY() + context.getGenDepth() - 1 - upgrade);
            //int l1 = Math.max(Mth.floor(z - horizontalRadius) - minBlockZ - 1, 0);
            int i2 = Math.min(Mth.floor(z + horizontalRadius) - minBlockZ, 15);
            boolean flag = false;
            BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos checkPosition = new BlockPos.MutableBlockPos();

            for(int maskX = k; maskX <= l; maskX++) {
                int posX = chunkpos.getBlockX(maskX);
                double relX = ((double) posX + 0.5 - x) / horizontalRadius;

                //for(int maskZ = l1; maskZ <= i2; maskZ++) {
                for(int maskZ = k; maskZ <= i2; maskZ++) {
                    int posZ = chunkpos.getBlockZ(maskZ);
                    double relZ = ((double) posZ + 0.5 - z) / horizontalRadius;

                    //if (!(relX * relX + relZ * relZ >= 1.0)) {
                    MutableBoolean surfaceCheck = new MutableBoolean(false);

                    for(int maskY = k1; maskY > i1; maskY--) {
                        double relY = ((double) maskY - 0.5 - y) / verticalRadius;

                        //if (!skipChecker.shouldSkip(context, relX, relY, relZ, maskY) && (!carvingMask.get(maskX, maskY, maskZ))) {
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
                        //}
                    }
                    //}
                }
            }
            return flag;
        } else {
            return false;
        }
    }

    private static boolean skipCheck(double relativeX, double relativeY, double relativeZ, double minRelativeY) {
        if (relativeY <= minRelativeY) {
            return true;
        } else {
            return (relativeX * relativeX) + (relativeY * relativeY) + (relativeZ * relativeZ) >= 1.0;
        }
    }

}

//        try (ServerLevel serverLevel = chunkAccess.getWorldForge().getServer().getLevel(TestDimension.M_LEVEL_KEY)) {
//            DimensionTest.LOGGER.log(Level.INFO, "Try structure position in level : " + serverLevel.toString());
//
//            if (chunkAccess.getStatus() != ChunkStatus.EMPTY) {
//                StructureTemplate template = serverLevel.getStructureManager().getOrCreate(new ResourceLocation(DimensionTest.MOD_ID, "corridor_cross"));
//                if (template != null) {
//                    DimensionTest.LOGGER.log(Level.INFO, "Structure : " + template.toString());
//                }
//                template.placeInWorld(
//                        serverLevel,
//                        BlockPos.ZERO,
//                        new BlockPos(chunkPos.getBlockX(0), 68, chunkPos.getBlockZ(0)),
//                        new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false),
//                        serverLevel.random,
//                        3
//                );
//            }
//        } catch (Exception e) {
//            DimensionTest.LOGGER.log(Level.WARNING, "Failed to place structure : " + e.getMessage(), e);
//            return false;
//        }
