package com.alexthw.sauce.network;

import com.alexthw.sauce.api.AlliesSavedData;
import com.hollingsworth.arsnouveau.common.network.Networking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SauceNet {

    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar reg = event.registrar("1");
        reg.playBidirectional(PacketSetAllies.TYPE, PacketSetAllies.CODEC, Networking::handle);
    }


    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!e.getEntity().getCommandSenderWorld().isClientSide() && e.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getCommandSenderWorld() instanceof ServerLevel level) {
                var allies = AlliesSavedData.getAllies(level, serverPlayer.getUUID());
                Networking.sendToPlayerClient(new PacketSetAllies(serverPlayer.getUUID(), allies), serverPlayer);
            }
        }
    }

}
