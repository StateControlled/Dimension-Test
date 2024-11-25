package com.statecontrolled.dimensiontest.world.cave;

import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;
import com.statecontrolled.dimensiontest.world.chunk.CustomChunkGenerator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class AlternateCarver extends CaveWorldCarver {

    public AlternateCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    /**
     * Carves the given chunk with caves that originate from the given {@code chunkPos}.
     * This method is invoked 289 times in order to generate each chunk (once for every position in an 8 chunk radius, or 17x17 chunk area, centered around the target chunk).
     *
     * @see net.minecraft.world.level.chunk.ChunkGenerator#applyCarvers
     *
     * @param chunk    The chunk to be carved
     * @param chunkPos The chunk position this carver is being called from
     */
    public boolean carve(
            CarvingContext carvingContext,
            CaveCarverConfiguration carverConfiguration,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            RandomSource randomSource,
            Aquifer aquifer,
            ChunkPos chunkPos,
            CarvingMask carvingMask
    ) {
        int i = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
        int j = randomSource.nextInt(randomSource.nextInt(randomSource.nextInt(this.getCaveBound()) + 1) + 1);

        for (int k = 0; k < j; k++) {
            double x = chunkPos.getBlockX(randomSource.nextInt(16));
            double y = carverConfiguration.y.sample(randomSource, carvingContext);
            double z = chunkPos.getBlockZ(randomSource.nextInt(16));
            double horizontalRadiusMul = carverConfiguration.horizontalRadiusMultiplier.sample(randomSource);
            double verticalRadiusMul = carverConfiguration.verticalRadiusMultiplier.sample(randomSource);
            double minRelativeY = UniformFloat.of(0.0F, 0.9F).sample(randomSource); // carverConfiguration.floorLevel.sample(randomSource);

            WorldCarver.CarveSkipChecker carveSkipChecker = (sCarvingContext, p_159203_, relativeY, relativeZ, p_159206_) ->
                    shouldSkip(p_159203_, relativeY, relativeZ, minRelativeY);

            int l = 1;
            if (randomSource.nextInt(4) == 0) {
                double d6 = carverConfiguration.yScale.sample(randomSource);
                float f1 = 1.0F + randomSource.nextFloat() * 6.0F;
                this.createRoom(carvingContext, carverConfiguration, chunk, biomeAccessor, aquifer, x, y, z, f1, d6, carvingMask, carveSkipChecker);
                l += randomSource.nextInt(4);
            }

            for (int k1 = 0; k1 < l; k1++) {
                float yaw = randomSource.nextInt(64) < 8 ? randomOneOrZero(randomSource) : 0;
                float pitch = randomSource.nextInt(64) < 8 ? randomOneOrZero(randomSource) : 0;
                float thickness = this.getThickness(randomSource);
                int branchCount = i - randomSource.nextInt(i / 4);

                this.createTunnel(
                        carvingContext,
                        carverConfiguration,
                        chunk,
                        biomeAccessor,
                        randomSource.nextLong(),
                        aquifer,
                        x,
                        y,
                        z,
                        horizontalRadiusMul,
                        verticalRadiusMul,
                        thickness,
                        yaw,
                        pitch,
                        0,
                        branchCount,
                        this.getYScale(),
                        carvingMask,
                        carveSkipChecker
                );
            }
        }

        return true;
    }

    protected float getThickness(RandomSource randomSource) {
        float f = (float) randomEvenNumberInRange(randomSource, 2, 12);

        if (randomSource.nextInt(10) == 0) {
            f *= (randomEvenNumberInRange(randomSource, 0, 8) + 2.0F);
        }

        return f;
    }

    protected void createRoom(
            CarvingContext carvingContext,
            CaveCarverConfiguration carverConfiguration,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            Aquifer aquifer,
            double x,
            double y,
            double z,
            float radius,
            double horizontalVerticalRatio,
            CarvingMask carvingMask,
            WorldCarver.CarveSkipChecker skipChecker
    ) {
        double horizontalRadius = 1.5 + (double) (Mth.sin((float) (Math.PI / 2)) * radius);
        double verticalRadius = horizontalRadius * horizontalVerticalRatio;
        this.carveEllipsoid(
                carvingContext,
                carverConfiguration,
                chunk,
                biomeAccessor,
                aquifer,
                x + 1.0,
                y,
                z,
                horizontalRadius,
                verticalRadius,
                carvingMask,
                skipChecker);
    }

    protected void createTunnel(
            CarvingContext carvingContext,
            CaveCarverConfiguration carverConfiguration,
            ChunkAccess chunk,
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
            WorldCarver.CarveSkipChecker skipChecker
    ) {
        RandomSource randomSource = RandomSource.create(seed);
        int i = randomSource.nextInt(branchCount / 2) + branchCount / 4;

        // Check determines if X, Y, or Z are to be incremented
        int test = 16;
        int check = randomSource.nextInt(test); // 0 to test (exclusive)
        int increment = getOne(randomSource);

        for (int j = branchIndex; j < branchCount; j++) {
            double horizontalRad = 1.5 + (double) (Mth.sin((float) Math.PI * (float) j / (float) branchCount) * thickness);
            double verticalRad = horizontalRad * horizontalVerticalRatio;

            if (check < ((test * 7) / 16)) {
                z += increment;
            } else if (check < ((test * 14) / 16)) {
                x += increment;
            } else {
                y += increment;
            }

            y += pitch;

            if (j == i && thickness > 1.0F) {
                for (int r = 0; r < 2; r++) {
                    this.createTunnel(
                            carvingContext,
                            carverConfiguration,
                            chunk,
                            biomeAccessor,
                            randomSource.nextLong(),
                            aquifer,
                            x,
                            y,
                            z,
                            horizontalRadiusMultiplier,
                            verticalRadiusMultiplier,
                            randomSource.nextFloat() * 0.5F + 0.5F,
                            yaw - (float) (Math.PI / 2),
                            pitch / 3.0F,
                            j,
                            branchCount,
                            1.0,
                            carvingMask,
                            skipChecker
                    );
                }

                return;
            }

            if (randomSource.nextInt(4) != 0) {
                if (!canReach(chunk.getPos(), x, z, j, branchCount, thickness)) {
                    return;
                }

                this.carveEllipsoid(
                        carvingContext,
                        carverConfiguration,
                        chunk,
                        biomeAccessor,
                        aquifer,
                        x,
                        y,
                        z,
                        horizontalRad * horizontalRadiusMultiplier,
                        verticalRad * verticalRadiusMultiplier,
                        carvingMask,
                        skipChecker
                );
            }
        }
    }

    /**
     * Carves blocks in an ellipsoid (more accurately a spheroid), defined by a center (x, y, z) position,
     * with a horizontal and vertical radius (the semi-axes)
     *
     * @param skipChecker Used to skip certain blocks within the carved region.
     */
    protected boolean carveEllipsoid(
            CarvingContext carvingContext,
            CaveCarverConfiguration carverConfiguration,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            Aquifer aquifer,
            double x,
            double y,
            double z,
            double horizontalRadius,
            double verticalRadius,
            CarvingMask carvingMask,
            WorldCarver.CarveSkipChecker skipChecker
    ) {
        ChunkPos chunkPos = chunk.getPos();
        double middleBlockX = chunkPos.getMiddleBlockX();
        double middleBlockZ = chunkPos.getMiddleBlockZ();
        double d2 = 16.0 + horizontalRadius * 2.0;

        if (!(Math.abs(x - middleBlockX) > d2) && !(Math.abs(z - middleBlockZ) > d2)) {
            int minBlockX = chunkPos.getMinBlockX();
            int minBlockZ = chunkPos.getMinBlockZ();

            int k = Math.max(Mth.floor(x - horizontalRadius) - minBlockX - 1, 0);
            int l = Math.min(Mth.floor(x + horizontalRadius) - minBlockX, 15);

            int i1 = Math.max(Mth.floor(y - verticalRadius) - 1, carvingContext.getMinGenY() + 1);
            int j1 = chunk.isUpgrading() ? 0 : 7;
            int k1 = Math.min(Mth.floor(y + verticalRadius) + 1, carvingContext.getMinGenY() + carvingContext.getGenDepth() - 1 - j1);

            int l1 = Math.max(Mth.floor(z - horizontalRadius) - minBlockZ - 1, 0);
            int i2 = Math.min(Mth.floor(z + horizontalRadius) - minBlockZ, 15);
            boolean flag = false;

            BlockPos.MutableBlockPos blockPos1 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockPos2 = new BlockPos.MutableBlockPos();

            for (int j2 = k; j2 <= l; j2++) {
                int k2 = chunkPos.getBlockX(j2);
                double d3 = ((double) k2 + 0.5 - x) / horizontalRadius;

                for (int l2 = l1; l2 <= i2; l2++) {
                    int i3 = chunkPos.getBlockZ(l2);
                    double d4 = ((double) i3 + 0.5 - z) / horizontalRadius;

                    if (!(d3 * d3 + d4 * d4 >= 1.0)) {
                        MutableBoolean mutableboolean = new MutableBoolean(false);

                        for (int j3 = k1; j3 > i1; j3--) {
                            double d5 = ((double)j3 - 0.5 - y) / verticalRadius;

                            if (!skipChecker.shouldSkip(carvingContext, d3, d5, d4, j3) && (!carvingMask.get(j2, j3, l2) || isDebugEnabled(carverConfiguration))) {
                                carvingMask.set(j2, j3, l2);
                                blockPos1.set(k2, j3, i3);
                                flag |= this.carveBlock(
                                        carvingContext,
                                        carverConfiguration,
                                        chunk,
                                        biomeAccessor,
                                        carvingMask,
                                        blockPos1,
                                        blockPos2,
                                        aquifer,
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

    /**
     * Carves a single block, replacing it with the appropriate state if possible, and handles replacing exposed dirt with grass.
     *
     * @param carvePos          The position to carve at. The method does not mutate this position.
     * @param checkPos          An additional mutable block position object to be used and modified by the method
     * @param hasReachedSurface Set to true if the block carved was the surface, which is checked as being either grass or mycelium
     */
    protected boolean carveBlock(
            CarvingContext carvingContext,
            CaveCarverConfiguration carverConfiguration,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeGetter,
            CarvingMask carvingMask,
            BlockPos.MutableBlockPos carvePos,
            BlockPos.MutableBlockPos checkPos,
            Aquifer aquifer,
            MutableBoolean hasReachedSurface
    ) {
        BlockState carvePosBlockState = chunk.getBlockState(carvePos);


        if (carvePosBlockState.is(CustomChunkGenerator.getDefaultCeiling())) {
            hasReachedSurface.setTrue();
        }

        if (!this.canReplaceBlock(carverConfiguration, carvePosBlockState) && !isDebugEnabled(carverConfiguration)) {
            return false;
        } else {
            BlockState blockState2 = this.getCarveState(carvingContext, carverConfiguration, carvePos, aquifer);

            if (blockState2 == null) {
                return false;
            } else {
                chunk.setBlockState(carvePos, blockState2, false);
                if (aquifer.shouldScheduleFluidUpdate() && !blockState2.getFluidState().isEmpty()) {
                    chunk.markPosForPostprocessing(carvePos);
                }

                if (hasReachedSurface.isTrue()) {
                    checkPos.setWithOffset(carvePos, Direction.DOWN);

                    if (chunk.getBlockState(checkPos).is(Blocks.DIRT)) {
                        carvingContext.topMaterial(biomeGetter, chunk, checkPos, !blockState2.getFluidState().isEmpty())
                            .ifPresent(blockState -> {
                                chunk.setBlockState(checkPos, blockState, false);
                                if (!blockState.getFluidState().isEmpty()) {
                                    chunk.markPosForPostprocessing(checkPos);
                                }
                            }
                        );
                    }
                }

                return true;
            }
        }
    }

    /**
     * @return -1, 0, or 1
     **/
    private int randomOneOrZero(RandomSource random) {
        return random.nextInt(3) - 1;
    }

    /**
     * @return -1 or 1
     **/
    private int getOne(RandomSource random) {
        return random.nextInt(16) < 8 ? -1 : 1;
    }

    /**
     * Returns a random even number within the given range.
     *
     * @param min   the minimum bound (inclusive)
     * @param max   the maximum bound (inclusive)
     * @return      a random even number within the range [min, max]
     * @throws      IllegalArgumentException if there is no even number in the range
     */
    public static int randomEvenNumberInRange(RandomSource random, int min, int max) {
        int evenMin = (min % 2 == 0) ? min : min + 1;
        int evenMax = (max % 2 == 0) ? max : max - 1;

        if (evenMin > evenMax) {
            throw new IllegalArgumentException("No even numbers in the given range");
        }

        int range = ((evenMax - evenMin) / 2) + 1;
        return evenMin + (2 * random.nextInt(range));
    }

    @Nullable
    private BlockState getCarveState(CarvingContext carvingContext, CaveCarverConfiguration carverConfiguration, BlockPos blockPos, Aquifer aquifer) {
        if (blockPos.getY() <= carverConfiguration.lavaLevel.resolveY(carvingContext)) {
            return LAVA.createLegacyBlock();
        } else {
            BlockState blockstate = aquifer.computeSubstance(
                    new DensityFunction.SinglePointContext(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 0.0
            );

            if (blockstate == null) {
                return isDebugEnabled(carverConfiguration) ? carverConfiguration.debugSettings.getBarrierState() : null;
            } else {
                return isDebugEnabled(carverConfiguration) ? getDebugState(carverConfiguration, blockstate) : blockstate;
            }
        }
    }

    private static boolean isDebugEnabled(CarverConfiguration config) {
        return config.debugSettings.isDebugMode();
    }

    private static BlockState getDebugState(CarverConfiguration config, BlockState blockState) {
        if (blockState.is(Blocks.AIR)) {
            return config.debugSettings.getAirState();
        } else if (blockState.is(Blocks.WATER)) {
            BlockState blockstate = config.debugSettings.getWaterState();
            return blockstate.hasProperty(BlockStateProperties.WATERLOGGED)
                    ? blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE)
                    : blockstate;
        } else {
            return blockState.is(Blocks.LAVA) ? config.debugSettings.getLavaState() : blockState;
        }
    }

    private static boolean shouldSkip(double relative, double relativeY, double relativeZ, double minRelativeY) {
        return relativeY <= minRelativeY || ((relative * relative) + (relativeY * relativeY) + (relativeZ * relativeZ)) >= 1.0;
    }
}
