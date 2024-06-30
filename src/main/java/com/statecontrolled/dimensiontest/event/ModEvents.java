package com.statecontrolled.dimensiontest.event;

import com.statecontrolled.dimensiontest.DimensionTest;
import com.statecontrolled.dimensiontest.command.ListStructuresCommand;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

@EventBusSubscriber(modid = DimensionTest.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new ListStructuresCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}
