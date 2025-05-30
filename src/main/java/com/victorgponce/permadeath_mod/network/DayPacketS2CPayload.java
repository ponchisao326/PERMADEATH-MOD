package com.victorgponce.permadeath_mod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DayPacketS2CPayload(int day) implements CustomPayload {
    public static final Identifier ID_S2C = Identifier.of("permadeath", "day_message");
    public static final CustomPayload.Id<DayPacketS2CPayload> ID = new CustomPayload.Id<>(ID_S2C);
    public static final PacketCodec<RegistryByteBuf, DayPacketS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, DayPacketS2CPayload::day,
            DayPacketS2CPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}