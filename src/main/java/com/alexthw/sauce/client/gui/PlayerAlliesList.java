package com.alexthw.sauce.client.gui;

import com.alexthw.sauce.api.AlliesSavedData;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class PlayerAlliesList extends ObjectSelectionList<PlayerAlliesList.PlayerListEntry> {

    private final AlliesSavedData.AllyData allies;
    private final Runnable onChanged;

    public static Map<UUID, Optional<GameProfile>> gameProfileCache = new ConcurrentHashMap<>();

    public PlayerAlliesList(
            Minecraft minecraft,
            int width,
            int height,
            int y,
            int itemHeight,
            AlliesSavedData.AllyData allies,
            Runnable onChanged
    ) {
        super(minecraft, width, height, y, itemHeight);
        this.allies = allies;
        this.onChanged = onChanged;
        updateEntries();
    }

    protected boolean removeEntry(PlayerListEntry entry) {
        boolean removed = allies.getPlayers().remove(entry.playerUUID);

        if (removed && entry == getSelected()) {
            setSelected(null);
        }

        if (removed) {
            updateEntries();
            onChanged.run();
        }

        return removed;
    }

    public void updateEntries() {
        clearEntries();

        for (UUID ally : allies.getPlayers()) {
            addEntry(new PlayerListEntry(ally, this));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() - 6;
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected void renderListSeparators(@NotNull GuiGraphics graphics) {
    }

    @Override
    protected void renderListBackground(@NotNull GuiGraphics graphics) {
        graphics.blit(
                prefix("textures/gui/menu_paper.png"),
                getX() - 5, getY(), 0, 0, 108, 108, 108, 108
        );
    }

    public static class PlayerListEntry extends ObjectSelectionList.Entry<PlayerListEntry> {

        private final UUID playerUUID;
        private final PlayerAlliesList parent;

        public PlayerListEntry(UUID playerUUID, PlayerAlliesList parent) {
            this.playerUUID = playerUUID;
            this.parent = parent;
        }

        @Override
        public void render(
                GuiGraphics graphics,
                int index,
                int y,
                int x,
                int entryWidth,
                int entryHeight,
                int mouseX,
                int mouseY,
                boolean isSelected,
                float partialTicks
        ) {
            Optional<GameProfile> profile =
                    gameProfileCache.getOrDefault(playerUUID, Optional.empty());

            String name = profile.map(GameProfile::getName).orElse("Unknown Player");

            graphics.drawString(
                    Minecraft.getInstance().font,
                    name,
                    x + 8,
                    y + 5,
                    0,
                    false
            );
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.empty();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return button == 0 && parent.removeEntry(this);
        }
    }

}