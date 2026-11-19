package com.alexthw.sauce.client.gui;

import com.alexthw.sauce.api.AlliesSavedData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class EntityAlliesList extends ObjectSelectionList<EntityAlliesList.EntityListEntry> {
    private static final ResourceLocation SCROLLER_SPRITE = prefix("scroller");

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
        return getX() - 1;
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

    @Override
    protected boolean scrollbarVisible() {
        return false;
    }

    protected boolean isScrollbarVisible() {
        return super.scrollbarVisible();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isScrollbarVisible()) {
            int l = this.getScrollbarPosition();
            int i1 = (int) ((float) (this.height * this.height) / (float) this.getMaxPosition());
            i1 = Mth.clamp(i1, 32, this.height - 8);
            int k = (int) this.getScrollAmount() * (this.height - i1) / this.getMaxScroll() + this.getY();
            if (k < this.getY()) {
                k = this.getY();
            }

            RenderSystem.enableBlend();
            guiGraphics.blitSprite(SCROLLER_SPRITE, l, k + 2, 5, i1);
            RenderSystem.disableBlend();
        }
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