package com.canoestudio.retrofuturemccore.client;

import com.canoestudio.retrofuturemccore.api.item.zoom.RetroZoomOverlay;
import com.canoestudio.retrofuturemccore.api.item.zoom.RetroZoomRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RetroZoomClientHandler {

    @SubscribeEvent
    public void updateFov(FOVUpdateEvent event) {
        RetroZoomRegistry.ActiveZoom zoom = RetroZoomRegistry.getActiveZoom(event.getEntity(),
                Minecraft.getMinecraft().getRenderPartialTicks());
        if (zoom != null) {
            event.setNewfov(event.getFov() * zoom.getFovMultiplier());
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) {
            return;
        }

        RetroZoomRegistry.ActiveZoom zoom = RetroZoomRegistry.getActiveZoom(mc.player, event.getPartialTicks());
        if (zoom != null) {
            RetroZoomOverlay overlay = zoom.getOverlay();
            if (overlay != null) {
                renderOverlaySafely(overlay, event);
            }
        }
    }

    private static void renderOverlaySafely(RetroZoomOverlay overlay, RenderGameOverlayEvent.Post event) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            overlay.render(event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(),
                    event.getPartialTicks());
        } finally {
            GlStateManager.popMatrix();
            restoreGuiRenderState();
        }
    }

    private static void restoreGuiRenderState() {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
