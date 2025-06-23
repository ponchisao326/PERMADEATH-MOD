package com.victorgponce.permadeath_mod.mixin.common;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.victorgponce.permadeath_mod.data.NetheriteItemHelper.prohibitedItems;

@Mixin(ServerPlayNetworkHandler.class)
public class NetheriteGrabberProhibiter {

    @Inject(
            method = "onClickSlot(Lnet/minecraft/network/packet/c2s/play/ClickSlotC2SPacket;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).player;

        if (player.isInCreativeMode()) return;
        // Solo procesar si coincide el syncId
        if (packet.syncId() != player.currentScreenHandler.syncId) return;

        int slotIndex = packet.slot();
        var handler = player.currentScreenHandler;

        // Filtrar índices inválidos
        if (slotIndex < 0 || slotIndex >= handler.slots.size()) return;

        var slot = handler.getSlot(slotIndex);
        if (prohibitedItems(slot.getStack().getItem())) {
            player.sendMessage(Text.of("¡You cannot grab this!"), false);
            ci.cancel();
        }
    }
}
