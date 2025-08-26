package com.alexthw.sauce.common.recipe.jei;

import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.common.recipe.CharmChargingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CharmChargingRecipeCategory implements IRecipeCategory<CharmChargingRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Sauce.MODID, "charm_charging");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public CharmChargingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(120, 60);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(net.minecraft.world.item.Items.ENCHANTED_BOOK));
        this.title = Component.translatable("jei.sauce.charm_charging");
    }

    @Override
    public @NotNull RecipeType<CharmChargingRecipe> getRecipeType() {
        return JeiSaucePlugin.CHARM_CHARGING_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CharmChargingRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 20)
                .addItemStack(new ItemStack(recipe.input()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 20)
                .addItemStack(new ItemStack(recipe.input()));

        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addItemStack(new ItemStack(recipe.input()));
    }
}
