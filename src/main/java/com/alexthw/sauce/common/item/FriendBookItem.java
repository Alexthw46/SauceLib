package com.alexthw.sauce.common.item;

import com.alexthw.sauce.client.gui.GuiAlliesScreen;
import com.alexthw.sauce.network.PacketOpenAllyScreen;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class FriendBookItem extends ModItem {

    public FriendBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (!level.isClientSide && player instanceof ServerPlayer sPlayer)
            Networking.sendToPlayerClient(new PacketOpenAllyScreen(), sPlayer);
        return super.use(level, player, usedHand);
    }

    @OnlyIn(Dist.CLIENT)
    public static void openGui() {
        Minecraft.getInstance().setScreen(new GuiAlliesScreen());
    }

}
