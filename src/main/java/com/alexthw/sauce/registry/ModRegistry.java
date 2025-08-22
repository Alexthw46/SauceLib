package com.alexthw.sauce.registry;

import com.alexthw.sauce.api.item.components.CharmData;
import com.alexthw.sauce.api.item.components.SchoolCasterTomeData;
import com.alexthw.sauce.common.fluid.SourceFluid;
import com.alexthw.sauce.common.item.ExampleCosmetic;
import com.alexthw.sauce.common.recipe.CharmChargingRecipe;
import com.alexthw.sauce.common.recipe.ConfigCondition;
import com.alexthw.sauce.common.recipe.ElementalArmorRecipe;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
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
import net.neoforged.neoforge.common.conditions.ICondition;
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
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MODID);

    public static void registerRegistries(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        SOUNDS.register(bus);
        ATTRIBUTES.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        RECIPES.register(bus);
        SERIALIZERS.register(bus);
        CONDITION_CODECS.register(bus);
        DATA_COMPONENT_TYPES.register(bus);
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

    public static final DeferredHolder<Attribute, Attribute> SUMMON_POWER = PerkAttributes.registerAttribute("sauce.perk.summon_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "ee3a4090-c5f5-4a26-a9c2-6044e9e609de"
    );

    public static final DeferredHolder<Attribute, Attribute> ABJURATION_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.abjuration_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "a1b2c3d4-e5f6-4a1b-9c8d-1234567890ab"
    );
    public static final DeferredHolder<Attribute, Attribute> NECROMANCY_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.necromancy_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "b2c3d4e5-f6a1-4b9c-8d12-234567890abc"
    );
    public static final DeferredHolder<Attribute, Attribute> MANIPULATION_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.manipulation_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "c3d4e5f6-a1b2-4c9d-8e12-34567890abcd"
    );
    public static final DeferredHolder<Attribute, Attribute> AIR_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.air_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "d4e5f6a1-b2c3-4d9e-8f12-4567890abcde"
    );
    public static final DeferredHolder<Attribute, Attribute> EARTH_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.earth_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "e5f6a1b2-c3d4-4e9f-8012-567890abcdef"
    );
    public static final DeferredHolder<Attribute, Attribute> FIRE_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.fire_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "f6a1b2c3-d4e5-4f90-8112-67890abcdef0"
    );
    public static final DeferredHolder<Attribute, Attribute> WATER_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.water_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "a7b8c9d0-e1f2-4a91-8212-7890abcdef01"
    );
    public static final DeferredHolder<Attribute, Attribute> ELEMENTAL_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.elemental_power",
            (id) -> new RangedAttribute(id, 0, 0, 10000.0D),
            "b8c9d0e1-f2a3-4b92-8312-890abcdef012"
    );


    public static final DeferredHolder<RecipeType<?>, RecipeType<ElementalArmorRecipe>> ELEMENTAL_ARMOR_UP = RECIPES.register("armor_upgrade", () -> RecipeType.simple(prefix("armor_upgrade")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ElementalArmorRecipe>> ELEMENTAL_ARMOR_UP_SERIALIZER = SERIALIZERS.register("armor_upgrade", ElementalArmorRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CharmChargingRecipe>> CHARM_CHARGING_TYPE = RECIPES.register("charm_charging", () -> RecipeType.simple(prefix("charm_charging")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CharmChargingRecipe>> CHARM_CHARGING_SERIALIZER = SERIALIZERS.register("charm_charging", CharmChargingRecipe.Serializer::new);


    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<ConfigCondition>> CONFIG_CONDITION = CONDITION_CODECS.register("config", () -> ConfigCondition.CODEC);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SchoolCasterTomeData>> E_TOME_CASTER = DATA_COMPONENT_TYPES.register("school_tome_caster", () -> DataComponentType.<SchoolCasterTomeData>builder().persistent(SchoolCasterTomeData.CODEC.codec()).networkSynchronized(SchoolCasterTomeData.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CharmData>> CHARM_DATA = DATA_COMPONENT_TYPES.register("charm_data",
            () -> DataComponentType.<CharmData>builder().persistent(CharmData.CODEC).networkSynchronized(CharmData.STREAM_CODEC).build()
    );

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }
}
