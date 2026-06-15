package com.example.compblock.mixin;

import com.example.compblock.event.ClientBloodMoonState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Intercepts the water tint-colour lookup inside LiquidBlockRenderer.tesselate()
 * so water surfaces turn blood-red during a Blood Moon.
 *
 * The vanilla (and NeoForge 21.1.x) LiquidBlockRenderer calls
 *   Minecraft.getBlockColors().getColor(blockState, level, pos, 0)
 * to obtain the water tint.  We redirect that call and return 0x8B0000
 * whenever a Blood Moon is active and the block is water.
 *
 * require=0 means the game will not crash if the call site is absent
 * (e.g. if a future NeoForge patch removes it).
 */
@Mixin(net.minecraft.client.renderer.LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {

    private static final int BLOOD_RED = 0x8B0000;

    @Redirect(
        method = "tesselate",
        require = 0,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/color/block/BlockColors;getColor(" +
                     "Lnet/minecraft/world/level/block/state/BlockState;" +
                     "Lnet/minecraft/world/level/BlockAndTintGetter;" +
                     "Lnet/minecraft/core/BlockPos;I)I"
        )
    )
    private int redirectWaterColor(
            BlockColors blockColors,
            BlockState state,
            BlockAndTintGetter getter,
            BlockPos pos,
            int tintIndex) {
        if (ClientBloodMoonState.active && state.is(Blocks.WATER)) {
            return BLOOD_RED;
        }
        return blockColors.getColor(state, getter, pos, tintIndex);
    }
}
