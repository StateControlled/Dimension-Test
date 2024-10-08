package com.statecontrolled.dimensiontest.command;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.world.dimension.TestDimension;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;

public class ListStructuresCommand {
    public ListStructuresCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mod").then(Commands.literal("list").then(Commands.literal("structures").executes(this::execute))));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        List<ResourceLocation> listOfStructures = listStructures(context);
        if (!listOfStructures.isEmpty()) {
            for (ResourceLocation r : listOfStructures) {
                context.getSource().sendSuccess(() -> Component.literal("Found structure : " + r.toString()), true);
            }
            return 1;
        } else {
            context.getSource().sendSuccess(() -> Component.literal("No structures found!"), true);
            return 0;
        }
    }

    private List<ResourceLocation> listStructures(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        ServerLevel level = player.getServer().getLevel(TestDimension.M_LEVEL_KEY);
        ResourceManager resourceManager =level.getServer().getResourceManager();
        return resourceManager.listResources("structures", (filename) -> filename.toString().endsWith(".nbt"))
                .keySet()
                .stream()
                .filter(resourceLocation -> resourceLocation.getNamespace().equals(DimensionTest.MOD_ID))
                .filter(resourceLocation -> resourceLocation.getPath().startsWith("structures"))
                .map(resourceLocation ->
                        ResourceLocation.fromNamespaceAndPath(
                                resourceLocation.getNamespace(),
                                resourceLocation.getPath().replaceAll("^structures/", "").replaceAll(".nbt$", "")
                        )
                )
                .toList();
    }

}
