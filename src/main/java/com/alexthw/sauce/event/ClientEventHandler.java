package com.alexthw.sauce.event;

import com.alexthw.sauce.client.DynamicSourceJarRenderer;
import com.alexthw.sauce.client.FocusTurretRenderer;
import com.alexthw.sauce.registry.ModRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void bindRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModRegistry.FOCUS_TURRET.get(), FocusTurretRenderer::new);
        event.registerBlockEntityRenderer(ModRegistry.BIG_SOURCE_JAR.get(), DynamicSourceJarRenderer::new);
    }


}
