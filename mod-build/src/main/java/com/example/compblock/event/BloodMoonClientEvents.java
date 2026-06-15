package com.example.compblock.event;

import com.example.compblock.CompBlockMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = CompBlockMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class BloodMoonClientEvents {

    private static final ResourceLocation MOON_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");

    // Tracks previous state so we can call allChanged() exactly once per toggle.
    // This field is only touched on the Render thread (inside RenderLevelStageEvent).
    private static boolean prevActive = false;

    // ── Fog / ambient colour ─────────────────────────────────────────────────

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (!ClientBloodMoonState.active) return;
        // Push the fog colour strongly toward red.
        event.setRed(Math.min(1.0f, event.getRed() + 0.6f));
        event.setGreen(event.getGreen() * 0.05f);
        event.setBlue(event.getBlue()  * 0.05f);
    }

    // ── Main render hook — runs on Render thread ─────────────────────────────
    //
    //  KEY FIX: renderSky() creates its OWN internal PoseStack and applies
    //  camera.rotation() to it.  The PoseStack inside RenderLevelStageEvent
    //  is the outer renderLevel() stack, which has NO camera rotation.
    //  Every previous version drew in raw world coordinates → invisible.
    //
    //  Solution: build a fresh PoseStack and apply camera.rotation() ourselves,
    //  exactly mirroring what renderSky() does internally.

    @SubscribeEvent
    public static void onRenderLevelAfterSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc  = Minecraft.getInstance();
        ClientLevel lvl = mc.level;

        // ── Detect Blood Moon toggle → rebuild chunk colour geometry ──────────
        // Safe here because RenderLevelStageEvent always fires on the Render thread.
        boolean nowActive = ClientBloodMoonState.active;
        if (nowActive != prevActive) {
            prevActive = nowActive;
            if (lvl != null) mc.levelRenderer.allChanged();
            return; // skip this frame; chunks rebuild next frame
        }

        if (!nowActive || lvl == null) return;

        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        // Build the view-space PoseStack that renderSky() uses internally.
        Camera camera = mc.gameRenderer.getMainCamera();
        PoseStack ps = new PoseStack();
        ps.mulPose(camera.rotation());   // ← THE critical line that was missing

        drawRedSkyOverlay(ps);
        drawRedMoon(ps, lvl, partial);
    }

    // ── Red sky overlay ───────────────────────────────────────────────────────
    // Draws a translucent red box around the sky.
    // Depth test OFF + depth mask OFF → renders on top of the sky but is
    // overwritten by terrain/entities (rendered later with depth writes ON).

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

        // Top (+Y)
        buf.addVertex(m, -S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S, -S).setColor(R, G, B, A);
        // Bottom (-Y — overdrawn by terrain, harmless)
        buf.addVertex(m, -S, -S, -S).setColor(R, G, B, A);
        buf.addVertex(m,  S, -S, -S).setColor(R, G, B, A);
        buf.addVertex(m,  S, -S,  S).setColor(R, G, B, A);
        buf.addVertex(m, -S, -S,  S).setColor(R, G, B, A);
        // North (-Z)
        buf.addVertex(m,  S, -S, -S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m, -S, -S, -S).setColor(R, G, B, A);
        // South (+Z)
        buf.addVertex(m, -S, -S,  S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m,  S, -S,  S).setColor(R, G, B, A);
        // West (-X)
        buf.addVertex(m, -S, -S,  S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S,  S).setColor(R, G, B, A);
        buf.addVertex(m, -S,  S, -S).setColor(R, G, B, A);
        buf.addVertex(m, -S, -S, -S).setColor(R, G, B, A);
        // East (+X)
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

    // ── Red moon ──────────────────────────────────────────────────────────────
    // Drawn solid (no blending) so it completely covers the vanilla white moon.
    // Vertex layout and rotations mirror vanilla LevelRenderer.renderSky() exactly.

    private static void drawRedMoon(PoseStack ps, ClientLevel level, float partial) {
        int phase = level.getMoonPhase();
        int col = phase % 4,       row = phase / 4 % 2;
        float u0 = col / 4.0f,    v0 = row / 2.0f;
        float u1 = (col+1)/4.0f,  v1 = (row+1)/2.0f;
        float timeOfDay = level.getTimeOfDay(partial);

        ps.pushPose();
        // Same celestial rotations vanilla uses when drawing the moon:
        ps.mulPose(Axis.YP.rotationDegrees(-90.0F));
        ps.mulPose(Axis.XP.rotationDegrees(timeOfDay * 360.0F));
        ps.mulPose(Axis.XP.rotationDegrees(180.0F));   // moon is opposite to sun
        Matrix4f m = ps.last().pose();

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.disableBlend();                   // SOLID → covers vanilla moon fully
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, MOON_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 0.0f, 0.0f, 1.0f);   // red tint

        Tesselator tess = Tesselator.getInstance();
        // Vertex order matches vanilla moon draw call:
        //   (-20, 100, 20) → (20, 100, 20) → (20, 100,-20) → (-20, 100,-20)
        BufferBuilder buf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buf.addVertex(m, -20.0f, 100.0f,  20.0f).setUv(u0, v1);
        buf.addVertex(m,  20.0f, 100.0f,  20.0f).setUv(u1, v1);
        buf.addVertex(m,  20.0f, 100.0f, -20.0f).setUv(u1, v0);
        buf.addVertex(m, -20.0f, 100.0f, -20.0f).setUv(u0, v0);
        BufferUploader.drawWithShader(buf.buildOrThrow());

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        ps.popPose();
    }
}
