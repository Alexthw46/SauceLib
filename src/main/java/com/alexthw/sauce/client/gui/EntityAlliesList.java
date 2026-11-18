package com.alexthw.sauce.client.gui;

import com.alexthw.sauce.api.AlliesSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class EntityAlliesList extends ObjectSelectionList<EntityAlliesList.EntityListEntry> {

    private final AlliesSavedData.AllyData allies;
    private final Runnable onChanged;

    public EntityAlliesList(
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

    protected boolean removeEntry(EntityListEntry entry) {
        boolean removed = allies.getEntities().remove(entry.entityId);

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

        for (ResourceLocation entity : allies.getEntities()) {
            addEntry(new EntityListEntry(entity, this));
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

    public static class EntityListEntry extends ObjectSelectionList.Entry<EntityListEntry> {

        private final ResourceLocation entityId;
        private final EntityAlliesList parent;

        public EntityListEntry(ResourceLocation entityId, EntityAlliesList parent) {
            this.entityId = entityId;
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
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);

            Component name = type.getDescription();

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
            return Component.translatable(
                    BuiltInRegistries.ENTITY_TYPE.get(entityId).getDescriptionId()
            );
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return button == 0 && parent.removeEntry(this);
        }
    }
}