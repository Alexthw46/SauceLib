package com.alexthw.sauce.network;

import com.alexthw.sauce.api.AlliesSavedData;
import com.alexthw.sauce.api.AlliesSavedData.AllyData;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketSetAllies extends AbstractPacket {

    public static final Type<PacketSetAllies> TYPE =
            new Type<>(ArsNouveau.prefix("set_allies"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetAllies> CODEC =
            StreamCodec.ofMember(PacketSetAllies::toBytes, PacketSetAllies::new);

    private final UUID owner;
    private final AllyData allies;

    public PacketSetAllies(UUID owner, AllyData allies) {
        this.owner = owner;
        this.allies = allies;
    }

    public PacketSetAllies(RegistryFriendlyByteBuf buf) {
        this.owner = buf.readUUID();

        this.allies = new AllyData();

        // Read players
        int playerCount = buf.readInt();

        for (int i = 0; i < playerCount; i++) {
            allies.addPlayer(buf.readUUID());
        }

        // Read entity types
        int entityCount = buf.readInt();

        for (int i = 0; i < entityCount; i++) {
            allies.getEntities().add(buf.readResourceLocation());
        }
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(owner);

        // Write players
        buf.writeInt(allies.getPlayers().size());

        allies.getPlayers().forEach(buf::writeUUID);

        // Write entity types
        buf.writeInt(allies.getEntities().size());

        allies.getEntities().forEach(buf::writeResourceLocation);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        AlliesSavedData.setAllies(
                minecraftServer.overworld(),
                owner,
                allies
        );
        if (!FMLEnvironment.production)
            System.out.println(
                    "Received allies data for " + player.getUUID()
                            + " with " + allies.getPlayers().size() + " players and "
                            + allies.getEntities().size() + " entity types"
            );
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        AlliesSavedData.setLocalAllies(allies);
        if (!FMLEnvironment.production)
            System.out.println(
                    "Received allies data for " + player.getUUID()
                            + " with " + allies.getPlayers().size() + " players and "
                            + allies.getEntities().size() + " entity types"
            );
    }
}