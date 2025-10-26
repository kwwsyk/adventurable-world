package com.kwwsyk.suit.codec_config_lib.client.gui.test;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber
public class ScreenDebugEvent {

    @SubscribeEvent
    public static void info(ScreenEvent.Render.Post event){
        ScreenDebug.debugInfo(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public static void click(ScreenEvent.KeyPressed.Post event){
        ScreenDebug.click(event.getKeyCode(), event.getScreen());
    }
}
