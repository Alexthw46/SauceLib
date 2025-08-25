package com.alexthw.sauce.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class SauceLangProvider extends LanguageProvider {

    public SauceLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("item.sauce.source_fluid_bucket", "Liquefied Source Bucket");
        add("fluid_type.sauce.source_fluid", "Liquefied Source");
        add("block.sauce.source_fluid_block", "Liquified Source");

        add("curios.modifiers.an_focus", "While in spell focus slot:");
        add("curios.modifiers.bangle", "While in bangle slot:");
        add("curios.identifier.bundle", "Bundle");
        add("curios.identifier.bangle", "Bangle");
        add("sauce.perk.summon_power", "Summoning Spell Power");
        add("sauce.perk.manipulation_power", "Manipulation Spell Power");
        add("sauce.perk.abjuration_power", "Abjuration Spell Power");
        add("sauce.perk.necromancy_power", "Necromancy Spell Power");
        add("sauce.perk.air_power", "Air Spell Power");
        add("sauce.perk.earth_power", "Earth Spell Power");
        add("sauce.perk.fire_power", "Fire Spell Power");
        add("sauce.perk.water_power", "Water Spell Power");
        add("sauce.perk.elemental_power", "Elemental Spell Power");
        add("sauce.perk.spell_crit", "Spell Critical Strike Chance");
        add("sauce.perk.spell_crit_damage", "Spell Critical Damage Boost");
        add("sauce.perk.mana_discount", "Mana Cost Reduction");
    }
}
