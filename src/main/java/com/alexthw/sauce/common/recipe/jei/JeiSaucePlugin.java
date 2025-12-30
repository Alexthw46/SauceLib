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
import java.util.function.Supplier;

import static mezz.jei.api.recipe.RecipeType.createFromDeferredVanilla;

@JeiPlugin
public class JeiSaucePlugin implements IModPlugin {

    public static final Supplier<RecipeType<RecipeHolder<ElementalArmorRecipe>>> ELEMENTAL_ARMOR_TYPE = createFromDeferredVanilla(ModRegistry.ELEMENTAL_ARMOR_UP);
    public static final Supplier<RecipeType<RecipeHolder<CharmChargingRecipe>>> CHARM_CHARGING_RECIPE_TYPE = createFromDeferredVanilla(ModRegistry.CHARM_CHARGING_TYPE);



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

    @SuppressWarnings("unchecked")
    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        List<RecipeHolder<ElementalArmorRecipe>> armorRecipes = new ArrayList<>();
        List<RecipeHolder<CharmChargingRecipe>> charmChargingRecipes = new ArrayList<>();
        for (RecipeHolder<?> i : manager.getRecipes()) {
            switch (i.value()) {
                case CharmChargingRecipe recipe -> charmChargingRecipes.add((RecipeHolder<CharmChargingRecipe>) i);
                case ElementalArmorRecipe recipe -> armorRecipes.add((RecipeHolder<ElementalArmorRecipe>) i);
                default -> {
                }
            }
        }
        registry.addRecipes(ELEMENTAL_ARMOR_TYPE.get(), armorRecipes);
        registry.addRecipes(CHARM_CHARGING_RECIPE_TYPE.get(), charmChargingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), ELEMENTAL_ARMOR_TYPE.get());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.IMBUEMENT_BLOCK), CHARM_CHARGING_RECIPE_TYPE.get());
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


