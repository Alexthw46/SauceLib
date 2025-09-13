package com.alexthw.sauce.registry;

import com.alexthw.sauce.api.item.components.CharmData;
import com.alexthw.sauce.api.item.components.SchoolCasterTomeData;
import com.alexthw.sauce.common.block.FocusEnhancedSpellTurretTile;
import com.alexthw.sauce.common.fluid.SourceFluid;
import com.alexthw.sauce.common.recipe.CharmChargingRecipe;
import com.alexthw.sauce.common.recipe.ElementalArmorRecipe;
import com.hollingsworth.arsnouveau.ArsNouveau;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import static com.alexthw.sauce.Sauce.MODID;
import static com.alexthw.sauce.Sauce.prefix;
import static net.minecraft.core.registries.Registries.ATTRIBUTE;
import static net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE;
import static net.minecraft.core.registries.Registries.SOUND_EVENT;

public class ModRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BLOCK_ENTITY_TYPE, MODID);
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
        BLOCK_ENTITIES.register(bus);
        ITEMS.register(bus);
        SOUNDS.register(bus);
        ATTRIBUTES.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        RECIPES.register(bus);
        SERIALIZERS.register(bus);
        CONDITION_CODECS.register(bus);
        DATA_COMPONENT_TYPES.register(bus);

        ATTRIBUTES.addAlias(ArsNouveau.prefix("ars_elemental.perk.summon_power"), ArsNouveau.prefix("sauce.perk.summon_power"));
        ATTRIBUTES.addAlias(ResourceLocation.fromNamespaceAndPath("not_enough_glyphs", "not_enough_glyphs.perk.mana_discount"), ArsNouveau.prefix("sauce.perk.mana_discount"));
        FLUIDS.addAlias(ResourceLocation.fromNamespaceAndPath("starbunclemania", "source_fluid"), prefix("source_fluid"));
        FLUIDS.addAlias(ResourceLocation.fromNamespaceAndPath("starbunclemania", "source_fluid_flowing"), prefix("source_fluid_flowing"));
        BLOCKS.addAlias(ResourceLocation.fromNamespaceAndPath("starbunclemania", "source_fluid_block"), prefix("source_fluid_block"));
        ITEMS.addAlias(ResourceLocation.fromNamespaceAndPath("starbunclemania", "source_fluid_bucket"), prefix("source_fluid_bucket"));
        DATA_COMPONENT_TYPES.addAlias(ResourceLocation.fromNamespaceAndPath("ars_elemental", "elemental_tome_caster"), prefix("school_tome_caster"));
        DATA_COMPONENT_TYPES.addAlias(ResourceLocation.fromNamespaceAndPath("ars_additions", "charm_data"), prefix("charm_data"));

    }

    //this is an example of how to register a sound. You also need to add the sound to the sound.json file, referencing your ogg files, and a texture for the button under textures/sounds.
    //this example will use one of the existing sounds randomly
    public static DeferredHolder<SoundEvent, SoundEvent> EXAMPLE_FAMILY = SOUNDS.register("example_sound", () -> makeSound("example_sound"));
    public static SpellSound EXAMPLE_SPELL_SOUND = new SpellSound(ModRegistry.EXAMPLE_FAMILY, Component.literal("Random"), prefix("example_random_sound"));

    /**
     * Use {@link BlockEntityTypeAddBlocksEvent} to add blocks to the BlockEntityType after registry.
     */
    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends FocusEnhancedSpellTurretTile>> FOCUS_TURRET = BLOCK_ENTITIES.register("focus_turret", () -> BlockEntityType.Builder.of(FocusEnhancedSpellTurretTile::new).build(null));

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

    public static final DeferredHolder<Attribute, Attribute> SUMMON_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.summon_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "ee3a4090-c5f5-4a26-a9c2-6044e9e609de"
    );
    public static final DeferredHolder<Attribute, Attribute> CONJURATION_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.summon_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "f1a2b3c4-d5e6-4f17-9a2b-1234567890aa"
    );
    public static final DeferredHolder<Attribute, Attribute> ABJURATION_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.abjuration_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "a1b2c3d4-e5f6-4a1b-9c8d-1234567890ab"
    );
    public static final DeferredHolder<Attribute, Attribute> ABJURATION_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.abjuration_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "b2c3d4e5-f6a1-4b9c-8d12-1234567890ac"
    );
    public static final DeferredHolder<Attribute, Attribute> NECROMANCY_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.necromancy_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "b2c3d4e5-f6a1-4b9c-8d12-234567890abc"
    );
    public static final DeferredHolder<Attribute, Attribute> NECROMANCY_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.necromancy_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "c3d4e5f6-a1b2-4c9d-8e12-234567890abd"
    );
    public static final DeferredHolder<Attribute, Attribute> MANIPULATION_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.manipulation_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "c3d4e5f6-a1b2-4c9d-8e12-34567890abcd"
    );
    public static final DeferredHolder<Attribute, Attribute> MANIPULATION_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.manipulation_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "d4e5f6a1-b2c3-4d9e-8f12-34567890abce"
    );
    public static final DeferredHolder<Attribute, Attribute> AIR_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.air_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "d4e5f6a1-b2c3-4d9e-8f12-4567890abcde"
    );
    public static final DeferredHolder<Attribute, Attribute> AIR_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.air_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "e5f6a1b2-c3d4-4e9f-8012-4567890abcef"
    );
    public static final DeferredHolder<Attribute, Attribute> EARTH_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.earth_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "e5f6a1b2-c3d4-4e9f-8012-567890abcdef"
    );
    public static final DeferredHolder<Attribute, Attribute> EARTH_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.earth_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "f6a1b2c3-d4e5-4f90-8112-567890abcdf0"
    );
    public static final DeferredHolder<Attribute, Attribute> FIRE_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.fire_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "f6a1b2c3-d4e5-4f90-8112-67890abcdef0"
    );
    public static final DeferredHolder<Attribute, Attribute> FIRE_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.fire_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "a7b8c9d0-e1f2-4a91-8212-67890abcdef1"
    );
    public static final DeferredHolder<Attribute, Attribute> WATER_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.water_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "a7b8c9d0-e1f2-4a91-8212-7890abcdef01"
    );
    public static final DeferredHolder<Attribute, Attribute> WATER_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.water_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "b8c9d0e1-f2a3-4b92-8312-7890abcdef02"
    );
    public static final DeferredHolder<Attribute, Attribute> ELEMENTAL_POWER = PerkAttributes.registerAttribute(
            "sauce.perk.elemental_power",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "b8c9d0e1-f2a3-4b92-8312-890abcdef012"
    );
    public static final DeferredHolder<Attribute, Attribute> ELEMENTAL_RESISTANCE = PerkAttributes.registerAttribute(
            "sauce.perk.elemental_resistance",
            (id) -> new RangedAttribute(id, 0, -1000, 1000.0D),
            "c9d0e1f2-a3b4-4c93-8412-890abc890013"
    );
    public static final DeferredHolder<Attribute, Attribute> MANA_DISCOUNT = PerkAttributes.registerAttribute(
            "sauce.perk.mana_discount",
            (id) -> new RangedAttribute(id, 0.0, -1000000, 1000000).setSyncable(true),
            "c9d0e1f2-a3b4-4c93-8412-90abcdef0123"
    );
    public static final DeferredHolder<Attribute, Attribute> SPELL_CRIT_DAMAGE = PerkAttributes.registerAttribute(
            "sauce.perk.spell_crit_damage",
            (id) -> new RangedAttribute(id, 25.0, -100.0, 1000.0).setSyncable(true),
            "d0e1f2a3-b4c5-4d94-8512-0abcdef05678"
    );
    public static final DeferredHolder<Attribute, Attribute> SPELL_CRIT = PerkAttributes.registerAttribute(
            "sauce.perk.spell_crit",
            (id) -> new RangedAttribute(id, 1.0, 0.0, 100.0).setSyncable(true),
            "d0e1f2a3-b4c5-4d94-8512-0abcdef01234"
    );

    public static final DeferredHolder<RecipeType<?>, RecipeType<ElementalArmorRecipe>> ELEMENTAL_ARMOR_UP = RECIPES.register("armor_upgrade", () -> RecipeType.simple(prefix("armor_upgrade")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ElementalArmorRecipe>> ELEMENTAL_ARMOR_UP_SERIALIZER = SERIALIZERS.register("armor_upgrade", ElementalArmorRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CharmChargingRecipe>> CHARM_CHARGING_TYPE = RECIPES.register("charm_charging", () -> RecipeType.simple(prefix("charm_charging")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CharmChargingRecipe>> CHARM_CHARGING_SERIALIZER = SERIALIZERS.register("charm_charging", CharmChargingRecipe.Serializer::new);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SchoolCasterTomeData>> E_TOME_CASTER = DATA_COMPONENT_TYPES.register("school_tome_caster", () -> DataComponentType.<SchoolCasterTomeData>builder().persistent(SchoolCasterTomeData.CODEC.codec()).networkSynchronized(SchoolCasterTomeData.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CharmData>> CHARM_DATA = DATA_COMPONENT_TYPES.register("charm_data",
            () -> DataComponentType.<CharmData>builder().persistent(CharmData.CODEC).networkSynchronized(CharmData.STREAM_CODEC).build()
    );

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }

}
