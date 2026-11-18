package com.alexthw.sauce.common.item;

import com.alexthw.sauce.client.gui.GuiAlliesScreen;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FriendBookItem extends ModItem {

    public FriendBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (level.isClientSide)
            Minecraft.getInstance().setScreen(new GuiAlliesScreen());
        return super.use(level, player, usedHand);
    }

}
