package com.statecontrolled.dimensiontest.item.portal;

import java.util.function.Function;

import com.statecontrolled.dimensiontest.DimensionTest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.ITeleporter;

public class ThreadModulatorTeleporter implements ITeleporter {
    private static final int MAX_SEARCH_RADIUS = 16;
    private BlockPos teleporterPosition;

    public ThreadModulatorTeleporter(BlockPos pos) {
        this.teleporterPosition = pos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destinationWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        try {
            Entity e = repositionEntity.apply(false);
            if (!(e instanceof ServerPlayer serverPlayer)) {
                DimensionTest.LOGGER.log(java.util.logging.Level.WARNING, "Entity is not a ServerPlayer");
                return e;
            }

            LevelChunk chunk = (LevelChunk) destinationWorld.getChunk(teleporterPosition);

            Vec3 playerSpawnPosition;

            playerSpawnPosition = findPlayerSpawnPosition(destinationWorld, chunk, serverPlayer.blockPosition());

            if (playerSpawnPosition == null) {
                DimensionTest.LOGGER.log(java.util.logging.Level.WARNING, "Player spawn position is NULL");
                return e;
            }
            // Teleport the player to the corresponding coordinates in the destination dimension
            serverPlayer.teleportTo(playerSpawnPosition.x + 0.5, playerSpawnPosition.y + 1, playerSpawnPosition.z + 0.5);
            return e;
        } catch (Exception e) {
            DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "Failed to place player entity! " + e.getMessage(), e);
            return entity;
        }
    }

    private Vec3 findPlayerSpawnPosition(ServerLevel world, LevelChunk chunk, BlockPos position) {
        try {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            int min = world.getMinBuildHeight();
            int max = world.getMaxBuildHeight();
            int xPos = position.getX();
            int zPos = position.getZ();

            // Try to find a location to spawn the player.
            for (int y = max - 16; y > min; y--) {
                for (int x = xPos; x < xPos + MAX_SEARCH_RADIUS; x++) {
                    for (int z = zPos; z < zPos + MAX_SEARCH_RADIUS; z++) {
                        pos.set(x, y, z);
                        // Check blocks above and below current position in destination dimension for suitable spawn position
                        // If the position is suitable, spawn the player there
                        if ( world.getBlockState(pos.above(1)).isAir() && world.getBlockState(pos.above(2)).isAir() &&
                                world.getBlockState(pos.above(3)).isAir() && !(world.getBlockState(pos.below(1)).isAir()) ) {

                            return new Vec3(pos.getX(), pos.getY(), pos.getZ());
                        }
                    }
                }
            }

            for (int y = max - 16; y > min; y--) {
                for (int x = xPos; x < xPos + MAX_SEARCH_RADIUS; x++) {
                    for (int z = zPos; z < zPos + MAX_SEARCH_RADIUS; z++) {
                        pos.set(x, y, z);
                        // Check blocks above and below current position in destination dimension for suitable spawn position
                        // If the position is suitable, clear the area, and spawn the player there
                        if ( isReplaceable(world, pos) && isReplaceable(world, pos.above(1)) && isReplaceable(world, pos.above(2))
                                && isReplaceable(world, pos.above(3)) && !(world.getBlockState(pos.below(1)).isAir()) ) {

                            // Clear area for player spawn
                            world.setBlockAndUpdate(pos.above(1), Blocks.AIR.defaultBlockState());
                            world.setBlockAndUpdate(pos.above(2), Blocks.AIR.defaultBlockState());
                            world.setBlockAndUpdate(pos.above(3), Blocks.AIR.defaultBlockState());

                            return new Vec3(pos.getX(), pos.getY(), pos.getZ());
                        }
                    }
                }
            }
        } catch (Exception e) {
            DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "FAILED to find a place to spawn the player : " + e.getMessage(), e);
        }
        DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "FAILED to find a place to spawn the player!");
        return null;
    }

    private boolean isReplaceable(Level world, BlockPos pos) {
        try {
            BlockState state = world.getBlockState(pos);
            return state.getBlock() == Blocks.STONE     || state.getBlock() == Blocks.GRANITE   ||
                    state.getBlock() == Blocks.ANDESITE || state.getBlock() == Blocks.DIORITE   ||
                    state.getBlock() == Blocks.DIRT     || state.getBlock() == Blocks.GRAVEL    ||
                    state.getBlock() == Blocks.LAVA     || state.getBlock() == Blocks.WATER     ||
                    state.isAir();
        } catch (Exception e) {
            DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "Somehow, the isReplaceable() method has failed : " + e.getMessage(), e);
            return false;
        }
    }

}
