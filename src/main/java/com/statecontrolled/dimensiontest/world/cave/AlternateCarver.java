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
    private static final float SLOPE = 4.0F * 2;

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
    @Override
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

            int limit = 1;
            if (randomSource.nextInt(4) == 0) {
                double d6 = carverConfiguration.yScale.sample(randomSource);
                float f1 = 1.0F + randomSource.nextFloat() * 6.0F;

                this.createRoom(carvingContext, carverConfiguration, chunk, biomeAccessor, aquifer, x, y, z, f1, d6, carvingMask, carveSkipChecker);
                limit += randomSource.nextInt(4) + 1;
            }

            for (int t = 0; t < limit; t++) {
                float yaw = randomSource.nextInt(64) < 8 ? randomOneOrZero(randomSource) : 0;
                float pitch = randomSource.nextInt(64) < 8 ? randomOneOrZero(randomSource) / SLOPE : 0;
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

    @Override
    protected float getThickness(RandomSource randomSource) {
        float f = (float) randomEvenNumberInRange(randomSource, 2, 12);

        if (randomSource.nextInt(10) == 0) {
            f *= (randomEvenNumberInRange(randomSource, 0, 8) + 2.0F);
        }

        return f;
    }

    @Override
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

    @Override
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
     * Reconfigured to carve rectangular rooms
     * @param skipChecker Used to skip certain blocks within the carved region.
     */
    @Override
    protected boolean carveEllipsoid(CarvingContext context,
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

        if (y < 64) {
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

                        if (!blockstate.is(CustomChunkGenerator.getDefaultCeiling())) {
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

    /**
     * Carves a single block, replacing it with the appropriate state if possible, and handles replacing exposed dirt with grass.
     *
     * @param carvePos          The position to carve at. The method does not mutate this position.
     * @param checkPos          An additional mutable block position object to be used and modified by the method
     * @param hasReachedSurface Set to true if the block carved was the surface, which is checked as being either grass or mycelium
     */
    @Override
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
     * @param minInclusive   the minimum bound (inclusive)
     * @param maxInclusive   the maximum bound (inclusive)
     * @return      a random even number within the range [minInclusive, maxInclusive]
     * @throws      IllegalArgumentException if there is no even number in the range
     */
    public static int randomEvenNumberInRange(RandomSource random, int minInclusive, int maxInclusive) {
        int evenMin = (minInclusive % 2 == 0) ? minInclusive : minInclusive + 1;
        int evenMax = (maxInclusive % 2 == 0) ? maxInclusive : maxInclusive - 1;

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
