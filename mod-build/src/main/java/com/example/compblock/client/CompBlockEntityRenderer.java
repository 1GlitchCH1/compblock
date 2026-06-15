package com.example.compblock.client;

import com.example.compblock.blockentity.CompBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CompBlockEntityRenderer implements BlockEntityRenderer<CompBlockEntity> {

    public CompBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CompBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Animation is handled by the block model "screen" element (texture #1 = pcconsoleanim1).
        // The .mcmeta flipbook animates automatically in all contexts:
        // inventory, hand, and placed block.
    }
}
