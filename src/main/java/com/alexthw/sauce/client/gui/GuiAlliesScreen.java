package com.alexthw.sauce.client.gui;

import com.alexthw.sauce.api.AlliesSavedData;
import com.alexthw.sauce.network.PacketSetAllies;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.buttons.ClearButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.Optional;
import java.util.UUID;

import static com.alexthw.sauce.client.gui.PlayerAlliesList.gameProfileCache;

public class GuiAlliesScreen extends BaseBook {

    private AlliesSavedData.AllyData allies;

    private PlayerAlliesList playerList;
    private EntityAlliesList entityList;

    private CustomEditBox playerInput;
    private CustomEditBox entityInput;

    public GuiAlliesScreen() {
        super();
    }

    @Override
    public void init() {
        super.init();

        allies = AlliesSavedData.getLocalAllies();

        int listY = bookTop + 60;
        int listHeight = 103;

        // Player section
        playerInput = new CustomEditBox(
                font,
                bookLeft + 20,
                bookTop + 35,
                100,
                20,
                Component.translatable("gui.enter_player")
        ) {
            @Override
            public boolean charTyped(char codePoint, int modifiers) {
                // if the text color is red, reset it to white since the user is typing again
                playerInput.setTextColor(0xFF927F68);
                return super.charTyped(codePoint, modifiers);
            }
        };
        playerInput.setHint(Component.translatable("gui.enter_player"));
        playerInput.setMaxLength(36); // UUIDs are 36 characters (including dashes)
        addRenderableWidget(playerInput);

        addRenderableWidget(new GuiImageButton(bookLeft + 122, bookTop + 40, 50, 12, ArsNouveau.prefix("textures/gui/create_icon.png"), button1 -> addPlayer()));

        playerList = new PlayerAlliesList(
                minecraft,
                108,
                listHeight,
                listY,
                15,
                allies,
                this::syncAllies
        );

        playerList.setX(bookLeft + 25);
        addRenderableWidget(playerList);

        // Entity section
        entityInput = new CustomEditBox(
                font,
                bookRight - 132,
                bookTop + 35,
                100,
                20,
                Component.translatable("gui.enter_entity_type")
        );
        entityInput.setHint(
                Component.translatable("gui.enter_entity_type")
        );

        entityInput.setMaxLength(128);
        addRenderableWidget(entityInput);

        addRenderableWidget(new GuiImageButton(bookRight - 30, bookTop + 40, 50, 12, ArsNouveau.prefix("textures/gui/create_icon.png"), button1 -> addEntity()));

        entityList = new EntityAlliesList(
                minecraft,
                108,
                listHeight,
                listY,
                15,
                allies,
                this::syncAllies
        );

        entityList.setX(bookRight - 128);
        addRenderableWidget(entityList);

        // Back button
        addRenderableWidget(new ClearButton(
                bookRight - 71,
                bookBottom - 13,
                Component.translatable("ars_nouveau.spell_book_gui.close"),
                button -> onClose()
        ));
    }

    private void addPlayer() {
        String value = playerInput.getValue().trim();

        if (value.isEmpty()) {
            return;
        }

        try {
            UUID uuid = UUID.fromString(value);
            addPlayerUUID(uuid);
        } catch (IllegalArgumentException e) {
            SkullBlockEntity.fetchGameProfile(value).whenComplete(
                    (profile, throwable) -> {
                        if (minecraft != null) minecraft.execute(() -> {
                            if (profile.isPresent()) {
                                GameProfile gameProfile = profile.get();

                                gameProfileCache.put(
                                        gameProfile.getId(),
                                        Optional.of(gameProfile)
                                );

                                addPlayerUUID(gameProfile.getId());
                            } else {
                                playerInput.setTextColor(0xFF0000);
                            }
                        });
                    }
            );
        }
    }

    private void addPlayerUUID(UUID uuid) {
        if (allies.getPlayers().add(uuid)) {
            if (!gameProfileCache.containsKey(uuid)) {
                gameProfileCache.put(uuid, Optional.empty());

                SkullBlockEntity.fetchGameProfile(uuid).whenComplete(
                        (profile, throwable) -> profile.ifPresent(
                                gameProfile -> gameProfileCache.put(
                                        uuid,
                                        Optional.of(gameProfile)
                                )
                        )
                );
            }

            playerList.updateEntries();
            syncAllies();
        }

        playerInput.setValue("");
    }

    private void addEntity() {
        String value = entityInput.getValue().trim().toLowerCase();

        if (value.isEmpty()) {
            return;
        }

        try {
            ResourceLocation id = ResourceLocation.parse(value);

            if (BuiltInRegistries.ENTITY_TYPE.getOptional(id).isEmpty()) {
                entityInput.setTextColor(0xFF0000);
                return;
            }

            if (allies.getEntities().add(id)) {
                entityList.updateEntries();
                syncAllies();
            }

            entityInput.setValue("");

        } catch (Exception e) {
            entityInput.setTextColor(0xFF0000);
        }
    }

    private void syncAllies() {
        if (minecraft != null && minecraft.player != null) {
            AlliesSavedData.setLocalAllies(allies);

            Networking.sendToServer(
                    new PacketSetAllies(
                            minecraft.player.getUUID(),
                            allies
                    )
            );
        }
    }

    @Override
    public void drawBackgroundElements(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);

        graphics.drawString(
                font,
                Component.translatable("ars_nouveau.settings.allies.players"),
                20,
                22,
                -8355712,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("ars_nouveau.settings.allies.entities"),
                160,
                +22,
                -8355712,
                false
        );

    }

}