package com.example.compblock.client;

import com.example.compblock.CompBlockMod;
import com.example.compblock.event.ClientBloodMoonState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = CompBlockMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BloodMoonColorHandlers {

    private static final int BLOOD_RED     = 0x8B0000;
    private static final int DEFAULT_WATER = 0x3F76E4;

    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, world, pos, tintIndex) -> {
                    if (ClientBloodMoonState.active) return BLOOD_RED;
                    if (world instanceof LevelReader lr && pos != null) {
                        return lr.getBiome(pos).value().getWaterColor();
                    }
                    return DEFAULT_WATER;
                },
                Blocks.WATER
        );
    }
}
