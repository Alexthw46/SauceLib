package com.alexthw.sauce.common.recipe;

import com.alexthw.sauce.api.item.AbstractCharm;
import com.alexthw.sauce.registry.ModRegistry;
import com.alexthw.sauce.util.CharmUtil;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record CharmChargingRecipe(ResourceLocation id, Item input, int costPerCharge) implements IImbuementRecipe {
    @Override
    public boolean matches(ImbuementTile imbuementTile, @NotNull Level leve) {
        ItemStack reagent = imbuementTile.stack;
        if (reagent.getItem() instanceof AbstractCharm charm) {
            if (charm.getDamage(reagent) == 0) {
                return false;
            }

            return reagent.is(input);
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(ImbuementTile imbuementTile, HolderLookup.@NotNull Provider provider) {
        ItemStack reagent = imbuementTile.stack;
        ItemStack result = reagent.copy();
        CharmUtil.setCharges(result, result.getMaxDamage());
        return result;
    }

    @Override
    public int getSourceCost(ImbuementTile imbuementTile) {
        ItemStack reagent = imbuementTile.stack;
        return reagent.getDamageValue() * costPerCharge;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRegistry.CHARM_CHARGING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRegistry.CHARM_CHARGING_TYPE.get();
    }

    @Override
    public Component getCraftingStartedText(ImbuementTile imbuementTile) {
        return Component.translatable("chat.ars_additions.charm.charging_started", assemble(imbuementTile, imbuementTile.getLevel().registryAccess()).getHoverName());
    }

    @Override
    public Component getCraftingText(ImbuementTile imbuementTile) {
        return Component.translatable("tooltip.ars_additions.charm.charging", assemble(imbuementTile, imbuementTile.getLevel().registryAccess()).getHoverName());
    }

    @Override
    public Component getCraftingProgressText(ImbuementTile imbuementTile, int progress) {
        return Component.translatable("tooltip.ars_additions.charm.charging_progress", progress).withStyle(ChatFormatting.GOLD);
    }

    public static class Serializer implements RecipeSerializer<CharmChargingRecipe> {
        public static final MapCodec<CharmChargingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(CharmChargingRecipe::id),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(CharmChargingRecipe::input),
                Codec.INT.optionalFieldOf("costPerDamage", 10).forGetter(CharmChargingRecipe::costPerCharge)
        ).apply(instance, CharmChargingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CharmChargingRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, CharmChargingRecipe::id,
                ByteBufCodecs.registry(Registries.ITEM), CharmChargingRecipe::input,
                ByteBufCodecs.INT, CharmChargingRecipe::costPerCharge,
                CharmChargingRecipe::new
        );

        @Override
        public @NotNull MapCodec<CharmChargingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CharmChargingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}