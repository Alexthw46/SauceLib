package com.alexthw.sauce.api;

import com.alexthw.sauce.network.PacketSetAllies;
import com.hollingsworth.arsnouveau.common.network.Networking;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class AlliesSavedData extends SavedData {

    public static class AllyData {

        private final Set<UUID> players = new HashSet<>();
        private final Set<ResourceLocation> entities = new HashSet<>();

        public Set<UUID> getPlayers() {
            return players;
        }

        public Set<ResourceLocation> getEntities() {
            return entities;
        }

        public void addPlayer(UUID uuid) {
            players.add(uuid);
        }

        public void removePlayer(UUID uuid) {
            players.remove(uuid);
        }

        public void addEntity(EntityType<?> entityType) {
            entities.add(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
        }

        public void removeEntity(EntityType<?> entityType) {
            entities.remove(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
        }

        public boolean isPlayerAlly(UUID uuid) {
            return players.contains(uuid);
        }

        public boolean isEntityAlly(EntityType<?> entityType) {
            return entities.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
        }
    }

    static AllyData localAllies = new AllyData();

    private final Map<UUID, AllyData> allies = new ConcurrentHashMap<>();

    public static AlliesSavedData get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(factory(), "ars_allies");
    }

    public static AllyData getAllies(ServerLevel world, UUID owner) {
        return get(world).allies.computeIfAbsent(owner, k -> new AllyData());
    }

    public static void setAllies(ServerLevel world, UUID owner, AllyData allyData) {
        var data = get(world);
        data.allies.put(owner, allyData);
        data.setDirty();
    }

    public static void setLocalAllies(AllyData allyData) {
        localAllies = allyData;
    }

    public static AllyData getLocalAllies() {
        return localAllies;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag alliesList = new ListTag();

        for (var entry : allies.entrySet()) {
            CompoundTag entryTag = new CompoundTag();

            entryTag.putUUID("Owner", entry.getKey());

            // Save players
            ListTag playersList = new ListTag();

            for (UUID player : entry.getValue().getPlayers()) {
                CompoundTag playerTag = new CompoundTag();
                playerTag.putUUID("UUID", player);
                playersList.add(playerTag);
            }

            entryTag.put("Players", playersList);

            // Save entity types
            ListTag entitiesList = new ListTag();

            for (ResourceLocation entity : entry.getValue().getEntities()) {
                entitiesList.add(StringTag.valueOf(entity.toString()));
            }

            entryTag.put("Entities", entitiesList);

            alliesList.add(entryTag);
        }

        tag.put("ArsAllies", alliesList);

        return tag;
    }

    public static AlliesSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        AlliesSavedData data = new AlliesSavedData();

        ListTag alliesList = tag.getList("ArsAllies", Tag.TAG_COMPOUND);

        for (int i = 0; i < alliesList.size(); i++) {
            CompoundTag entryTag = alliesList.getCompound(i);

            UUID owner = entryTag.getUUID("Owner");

            AllyData allyData = new AllyData();

            // Load players
            ListTag playersList = entryTag.getList("Players", Tag.TAG_COMPOUND);

            for (int j = 0; j < playersList.size(); j++) {
                allyData.addPlayer(playersList.getCompound(j).getUUID("UUID"));
            }

            // Load entity types
            ListTag entitiesList = entryTag.getList("Entities", Tag.TAG_STRING);

            for (int j = 0; j < entitiesList.size(); j++) {
                ResourceLocation id = ResourceLocation.parse(entitiesList.getString(j));

                allyData.getEntities().add(id);
            }

            data.allies.put(owner, allyData);
        }

        return data;
    }

    public static SavedData.Factory<AlliesSavedData> factory() {
        return new SavedData.Factory<>(AlliesSavedData::new, AlliesSavedData::load, null);
    }

    @SubscribeEvent
    public static void onSave(LevelEvent.Save event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel) || serverLevel.dimension() != ServerLevel.OVERWORLD || serverLevel.players().isEmpty()) {
            return;
        }

        serverLevel.players().forEach(player -> Networking.sendToPlayerClient(new PacketSetAllies(player.getUUID(), getAllies(serverLevel, player.getUUID())), player));
    }

    public static boolean isAlly(ServerLevel world, UUID owner, Entity entity) {
        AllyData data = getAllies(world, owner);

        if (entity instanceof Player player) {
            return data.isPlayerAlly(player.getUUID());
        }

        return data.isEntityAlly(entity.getType());
    }

}