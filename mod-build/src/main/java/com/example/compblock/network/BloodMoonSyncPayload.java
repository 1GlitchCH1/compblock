package com.example.compblock.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BloodMoonSyncPayload(boolean active) implements CustomPacketPayload {

    public static final Type<BloodMoonSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("compblock", "blood_moon_sync"));

    public static final StreamCodec<ByteBuf, BloodMoonSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    BloodMoonSyncPayload::active,
                    BloodMoonSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
