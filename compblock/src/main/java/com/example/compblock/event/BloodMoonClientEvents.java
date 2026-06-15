package com.example.compblock.event;

import com.example.compblock.CompBlockMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = CompBlockMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class BloodMoonClientEvents {

    private static boolean prevActive = false;

    // ── Fog / ambient colour ─────────────────────────────────────────────────

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (!ClientBloodMoonState.active) return;
        event.setRed(Math.min(1.0f, event.getRed() + 0.6f));
        event.setGreen(event.getGreen() * 0.05f);
        event.setBlue(event.getBlue()  * 0.05f);
    }

    // ── Main render hook ─────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onRenderLevelAfterSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc  = Minecraft.getInstance();
        ClientLevel lvl = mc.level;

        boolean nowActive = ClientBloodMoonState.active;
        if (nowActive != prevActive) {
            prevActive = nowActive;
            // Trigger full chunk rebuild so LiquidBlockRenderer re-queries water colour.
            // Called directly on the render thread — allChanged() is designed for it.
            if (lvl != null) mc.levelRenderer.allChanged();
            return;
        }

        if (!nowActive || lvl == null) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        PoseStack ps = new PoseStack();
        ps.mulPose(camera.rotation());

        drawRedSkyOverlay(ps);
        // NOTE: The moon is now red via the overridden moon_phases.png texture
        // (assets/minecraft/textures/environment/moon_phases.png inside this mod).
        // We do NOT draw a second moon here — that caused the duplicate + black-outline bug.
    }

    // ── Red sky overlay ───────────────────────────────────────────────────────

    private static void drawRedSkyOverlay(PoseStack ps) {
        ps.pushPose();

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f m = ps.last().pose();
        final float S = 100.0f;
        final float R = 0.8f, G = 0.0f, B = 0.0f, A = 0.30f;

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buf.addVertex(m, -S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S, -S).setColor(R, G, B, A);

        buf.addVertex(m, -S, -S, -S).setColor(R, G, B, A);
        buf.addVertex(m,  S, -S, -S).setColor(R, G, B, A);
        buf.addVertex(m,  S, -S,  S).setColor(R, G, B, A);
        buf.addVertex(m, -S, -S,  S).setColor(R, G, B, A);

        buf.addVertex(m,  S, -S, -S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m, -S, -S, -S).setColor(R, G, B, A);

        buf.addVertex(m, -S, -S,  S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S, -S,  S).setColor(R, G, B, A);

        buf.addVertex(m, -S, -S,  S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m, -S, -S, -S).setColor(R, G, B, A);

        buf.addVertex(m,  S, -S, -S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S, -S,  S).setColor(R, G, B, A);

        BufferUploader.drawWithShader(buf.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        ps.popPose();
    }
}
