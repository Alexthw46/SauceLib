package com.alexthw.sauce.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import top.theillusivec4.curios.api.CuriosApi;

import static com.alexthw.sauce.Sauce.prefix;

public class SauceTags {

    public static String[] curioSlots = {"an_focus", "curio", "back", "belt", "body", "bracelet", "charm", "feet", "head", "hands", "necklace", "ring", "spellbook"};

    static TagKey<Item> curiosTag(String key) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, key));
    }

    final TagKey<Block> ARCHWOOD_LEAVES = BlockTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "leaves/archwood_leaves"));

    public static final TagKey<Item> CURIO_SPELL_FOCUS = curiosTag("an_focus");
    public static final TagKey<Item> CURIO_BANGLE = curiosTag("bracelet");
    public static final TagKey<Item> SUMMON_SHARDS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "magic_shards"));
    public static final TagKey<Item> SPELLBOOK = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "spellbook"));
    public static final TagKey<Item> PRISM_LENS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "spell_prism_lens"));

    public static final TagKey<Biome> HAS_LIBRARY = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("ars_additions", "has_structure/arcane_library"));
    public static final TagKey<Biome> HAS_NEXUS = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("ars_additions", "has_structure/nexus_tower"));

    public static TagKey<DamageType> FIRE_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("fire_damage"));
    public static TagKey<DamageType> WATER_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("water_damage"));
    public static TagKey<DamageType> EARTH_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("earth_damage"));
    public static TagKey<DamageType> AIR_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("air_damage"));


}
