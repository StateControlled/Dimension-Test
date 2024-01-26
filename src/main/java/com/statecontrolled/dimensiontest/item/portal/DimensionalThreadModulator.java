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

/**
 * This item will transport the user to the custom dimension when held and right-clicked
 */
public class DimensionalThreadModulator extends Item {

    public DimensionalThreadModulator(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        try {
            ItemStack itemStack = player.getItemInHand(interactionHand);

            player.getCooldowns().addCooldown(this, 80);

            if ((player instanceof ServerPlayer serverPlayer) && !player.isPassenger() && !player.isVehicle()) {
                level.playSound(serverPlayer, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        SoundEvents.PORTAL_TRAVEL, SoundSource.NEUTRAL, 1.0f, 0.35f);

                if (handlePortal(serverPlayer, serverPlayer.blockPosition())) {
                    player.awardStat(Stats.ITEM_USED.get(this));

                    return InteractionResultHolder.success(itemStack);
                } else {
                    DimensionTest.LOGGER.log(java.util.logging.Level.WARNING, "Player not teleported");
                    return InteractionResultHolder.fail(itemStack);
                }
            } else {
                DimensionTest.LOGGER.log(java.util.logging.Level.WARNING, "Player not teleported");
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

                player.changeDimension(teleportWorld, new ThreadModulatorTeleporter(blockPosition));

            } else if (currentDimension.equals(Level.OVERWORLD)) {
                ServerLevel teleportWorld = player.server.getLevel(TestDimension.M_LEVEL_KEY);

                if (teleportWorld == null) {
                    return false;
                }

                player.changeDimension(teleportWorld, new ThreadModulatorTeleporter(blockPosition));

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

}
