package com.alexthw.sauce.common.recipe.jei;

import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.common.recipe.CharmChargingRecipe;
import com.alexthw.sauce.common.recipe.ElementalArmorRecipe;
import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JeiSaucePlugin implements IModPlugin {

    public static final RecipeType<ElementalArmorRecipe> ELEMENTAL_ARMOR_TYPE = RecipeType.create(Sauce.MODID, "armor_upgrade", ElementalArmorRecipe.class);
    public static final RecipeType<CharmChargingRecipe> CHARM_CHARGING_RECIPE_TYPE = RecipeType.create(Sauce.MODID, "charm_charging", CharmChargingRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Sauce.MODID, "main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new ElementalUpgradeRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
        registry.addRecipeCategories(
                new CharmChargingRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        List<ElementalArmorRecipe> armorRecipes = new ArrayList<>();
        List<CharmChargingRecipe> charmChargingRecipes = new ArrayList<>();
        for (RecipeHolder<?> i : manager.getRecipes()) {
            switch (i.value()) {
                case CharmChargingRecipe recipe -> charmChargingRecipes.add(recipe);
                case ElementalArmorRecipe recipe -> armorRecipes.add(recipe);
                default -> {
                }
            }
        }
        registry.addRecipes(ELEMENTAL_ARMOR_TYPE, armorRecipes);
        registry.addRecipes(CHARM_CHARGING_RECIPE_TYPE, charmChargingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), ELEMENTAL_ARMOR_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.IMBUEMENT_BLOCK), CHARM_CHARGING_RECIPE_TYPE);
    }


    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        if (!Sauce.SHOW_LIQUID_SOURCE) {
            IIngredientManager ingredientManager = jeiRuntime.getJeiHelpers().getIngredientManager();
            ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(ModRegistry.SOURCE_FLUID_BUCKET.get().getDefaultInstance()));
            ingredientManager.removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, List.of(new FluidStack(ModRegistry.SOURCE_FLUID.get(), 1000)));
        }
    }
}


