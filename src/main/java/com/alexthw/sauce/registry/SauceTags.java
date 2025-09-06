package com.alexthw.sauce.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public static final Map<SpellSchool, List<TagKey<DamageType>>> SCHOOL_TO_DAMAGE_TYPES = new ConcurrentHashMap<>(Map.of(
            SpellSchools.ELEMENTAL_FIRE, List.of(FIRE_DAMAGE),
            SpellSchools.ELEMENTAL_WATER, List.of(WATER_DAMAGE),
            SpellSchools.ELEMENTAL_EARTH, List.of(EARTH_DAMAGE),
            SpellSchools.ELEMENTAL_AIR, List.of(AIR_DAMAGE),
            SpellSchools.ELEMENTAL, List.of(FIRE_DAMAGE, WATER_DAMAGE, EARTH_DAMAGE, AIR_DAMAGE)
    ));

}
