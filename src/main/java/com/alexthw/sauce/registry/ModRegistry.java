package com.alexthw.sauce.registry;

import com.alexthw.sauce.fluid.SourceFluid;
import com.alexthw.sauce.item.ExampleCosmetic;
import com.alexthw.sauce.recipe.ElementalArmorRecipe;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import static com.alexthw.sauce.Sauce.MODID;
import static com.alexthw.sauce.Sauce.prefix;
import static net.minecraft.core.registries.Registries.ATTRIBUTE;
import static net.minecraft.core.registries.Registries.SOUND_EVENT;

public class ModRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(SOUND_EVENT, MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ATTRIBUTE, MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);


    public static void registerRegistries(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        SOUNDS.register(bus);
        ATTRIBUTES.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        RECIPES.register(bus);
        SERIALIZERS.register(bus);
    }

    public static final DeferredHolder<Item, ? extends Item> EXAMPLE;

    //this is an example of how to register a sound. You also need to add the sound to the sound.json file, referencing your ogg files, and a texture for the button under textures/sounds.
    //this example will use one of the existing sounds randomly
    public static DeferredHolder<SoundEvent, SoundEvent> EXAMPLE_FAMILY = SOUNDS.register("example_sound", () -> makeSound("example_sound"));
    public static SpellSound EXAMPLE_SPELL_SOUND = new SpellSound(ModRegistry.EXAMPLE_FAMILY, Component.literal("Example"), prefix("example_random_sound"));


    static {
        EXAMPLE = ITEMS.register("star_hat", () -> new ExampleCosmetic(new Item.Properties()));
    }

    static SoundEvent makeSound(@NotNull String name) {
        return SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    public static final DeferredHolder<FluidType, FluidType> SOURCE_FLUID_TYPE = FLUID_TYPES.register("source_fluid", SourceFluid::new);

    public static final DeferredHolder<Fluid, Fluid> SOURCE_FLUID = FLUIDS.register("source_fluid", () -> new BaseFlowingFluid.Source(fluidProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> SOURCE_FLUID_FLOWING = FLUIDS.register("source_fluid_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties()));
    public static final DeferredHolder<Block, LiquidBlock> SOURCE_FLUID_BLOCK = BLOCKS.register("source_fluid_block", () -> new LiquidBlock(SOURCE_FLUID_FLOWING.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noCollission().strength(100.0F).noLootTable()));
    public static final DeferredHolder<Item, Item> SOURCE_FLUID_BUCKET = ITEMS.register("source_fluid_bucket", () -> new BucketItem(SOURCE_FLUID.get(), basicItemProperties().craftRemainder(Items.BUCKET).stacksTo(1)));

    static Item.Properties basicItemProperties() {
        return new Item.Properties();
    }

    private static BaseFlowingFluid.Properties fluidProperties() {
        return new BaseFlowingFluid.Properties(SOURCE_FLUID_TYPE, SOURCE_FLUID, SOURCE_FLUID_FLOWING).block(SOURCE_FLUID_BLOCK).bucket(SOURCE_FLUID_BUCKET);
    }

    public static final DeferredHolder<Attribute, Attribute> SUMMON_POWER = PerkAttributes.registerAttribute("ars_elemental.perk.summon_power", (id) -> new RangedAttribute(id, 0, 0, 10000.0D), "ee3a4090-c5f5-4a26-a9c2-6044e9e609de");


    public static final DeferredHolder<RecipeType<?>, RecipeType<ElementalArmorRecipe>> ELEMENTAL_ARMOR_UP;
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ElementalArmorRecipe>> ELEMENTAL_ARMOR_UP_SERIALIZER;

    static {
        ELEMENTAL_ARMOR_UP = RECIPES.register("armor_upgrade", () -> RecipeType.simple(prefix("armor_upgrade")));
        ELEMENTAL_ARMOR_UP_SERIALIZER = SERIALIZERS.register("armor_upgrade", ElementalArmorRecipe.Serializer::new);
    }
}
