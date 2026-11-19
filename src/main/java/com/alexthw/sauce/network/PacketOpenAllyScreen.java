package com.alexthw.sauce.network;

import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.client.gui.GuiAlliesScreen;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PacketOpenAllyScreen extends AbstractPacket {


    public static final StreamCodec<RegistryFriendlyByteBuf, PacketOpenAllyScreen> CODEC = StreamCodec.ofMember(PacketOpenAllyScreen::toBytes, PacketOpenAllyScreen::new);
    public static final Type<PacketOpenAllyScreen> TYPE = new Type<>(Sauce.prefix("open_ally_screen"));


    public PacketOpenAllyScreen() {
    }

    public PacketOpenAllyScreen(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Minecraft.getInstance().setScreen(new GuiAlliesScreen());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
