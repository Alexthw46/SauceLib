package com.alexthw.sauce.api.item;

import com.alexthw.sauce.api.item.components.CharmData;
import com.alexthw.sauce.registry.ModRegistry;
import com.alexthw.sauce.util.CharmUtil;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AbstractCharm extends ArsNouveauCurio {
    private final int uses;

    public AbstractCharm(Item.Properties properties, int uses) {
        super(properties.stacksTo(1).durability(uses).component(ModRegistry.CHARM_DATA, new CharmData(uses)));
        this.uses = uses;
    }

    public AbstractCharm(int uses) {
        super(ModRegistry.defaultItemProperties().stacksTo(1).durability(uses).component(ModRegistry.CHARM_DATA, new CharmData(uses)));
        this.uses = uses;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);

        tooltip2.add(Component.translatable("tooltip.sauce.charm.desc").withStyle(ChatFormatting.GRAY));

        int charges = CharmData.getOrDefault(stack, uses).charges();
        tooltip2.add(Component.translatable("tooltip.sauce.charm.charges", charges, uses).withStyle(ChatFormatting.GOLD));

        String descKey = Util.makeDescriptionId("tooltip", BuiltInRegistries.ITEM.getKey(this));
        tooltip2.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        return uses;
    }

    @Override
    public int getDamage(@NotNull ItemStack stack) {
        return uses - CharmData.getOrDefault(stack, uses).charges();
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        int charges = CharmData.getOrDefault(stack, uses).charges();
        return charges != uses;
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean canElytraFly(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return super.canElytraFly(stack, entity);
    }

    @Override
    public void setDamage(@NotNull ItemStack stack, int damage) {
    }

    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return CharmUtil.isEnabled(stack);
    }
}