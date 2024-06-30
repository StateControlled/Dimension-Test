package com.statecontrolled.dimensiontest.item.portal;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.world.dimension.TestDimension;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

/**
 * This item will transport the user to the custom dimension when held and right-clicked
 */
public class DimensionalThreadModulator extends Item {
    private static final int MAX_SEARCH_EXTENT = 32;

    public DimensionalThreadModulator(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        try {
            ItemStack itemStack = player.getItemInHand(interactionHand);

            player.getCooldowns().addCooldown(this, 80);

            if ((player instanceof ServerPlayer serverPlayer) && player.canUsePortal(true)) {
                level.playSound(serverPlayer, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        SoundEvents.PORTAL_TRAVEL, SoundSource.NEUTRAL, 1.0f, 0.35f);

                if (handlePortal(serverPlayer, serverPlayer.blockPosition())) {
                    DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Teleportation Success");
                    player.awardStat(Stats.ITEM_USED.get(this));

                    return InteractionResultHolder.success(itemStack);
                } else {
                    DimensionTest.LOGGER.log(java.util.logging.Level.WARNING, "Player not teleported (47)");
                    return InteractionResultHolder.fail(itemStack);
                }
            } else {
                DimensionTest.LOGGER.log(java.util.logging.Level.WARNING, "Player not teleported (51)");
                return InteractionResultHolder.fail(itemStack);
            }
        } catch (Exception e) {
            DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "DimensionalThreadModulatorItem operation FAILED : " + e.getMessage(), e);
            return InteractionResultHolder.fail(player.getItemInHand(interactionHand));
        }
    }

    private boolean handlePortal(ServerPlayer player, BlockPos blockPosition) {
        try {
            ResourceKey<Level> currentDimension = player.level().dimension();

            if (currentDimension.equals(TestDimension.M_LEVEL_KEY)) {
                ServerLevel teleportWorld = player.server.getLevel(Level.OVERWORLD);

                if (teleportWorld == null) {
                    return false;
                }

                Vec3 spawn = findPlayerSpawnPosition(teleportWorld, blockPosition);

                DimensionTransition dt = new DimensionTransition(
                        teleportWorld,
                        spawn,
                        Vec3.ZERO,
                        0.0F,
                        0.0F,
                        false,
                        DimensionTransition.DO_NOTHING
                );

                player.changeDimension(dt);

            } else if (currentDimension.equals(Level.OVERWORLD)) {
                ServerLevel teleportWorld = player.server.getLevel(TestDimension.M_LEVEL_KEY);

                if (teleportWorld == null) {
                    return false;
                }

                Vec3 spawn = findPlayerSpawnPosition(teleportWorld, blockPosition);

                DimensionTransition dt = new DimensionTransition(
                        teleportWorld,
                        spawn,
                        Vec3.ZERO,
                        0.0F,
                        0.0F,
                        false,
                        DimensionTransition.DO_NOTHING
                );

                player.changeDimension(dt);

            } else {
                player.displayClientMessage(Component.literal("Teleporting from " + currentDimension + " is not supported!"), true);
                DimensionTest.LOGGER.log(java.util.logging.Level.INFO, "Teleporting from " + currentDimension + " is not supported!");
                return false;
            }
            return true;
        } catch (Exception e) {
            DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "Failed to handle player change dimension : " + e.getMessage(), e);
            return false;
        }
    }

    private Vec3 findPlayerSpawnPosition(ServerLevel world, BlockPos playerPosition) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int xPos = playerPosition.getX();
        int zPos = playerPosition.getZ();

        // Try to find a location to spawn the player.
        for (int y = 32; y < 64; y++) {
            for (int x = xPos; x < xPos + MAX_SEARCH_EXTENT; x++) {
                for (int z = zPos; z < zPos + MAX_SEARCH_EXTENT; z++) {
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

        for (int y = 32; y < 64; y++) {
            for (int x = xPos; x < xPos + MAX_SEARCH_EXTENT; x++) {
                for (int z = zPos; z < zPos + MAX_SEARCH_EXTENT; z++) {
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
        DimensionTest.LOGGER.log(java.util.logging.Level.SEVERE, "FAILED to find a place to spawn the player!");
        return Vec3.ZERO;
    }

    private boolean isReplaceable(Level world, BlockPos pos) {
        try {
            BlockState state = world.getBlockState(pos);
            return  state.getBlock() == Blocks.STONE    || state.getBlock() == Blocks.GRANITE   ||
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
