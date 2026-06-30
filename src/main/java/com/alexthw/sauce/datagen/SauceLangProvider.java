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

        add("block.sauce.source_jar_core", "Source Jar Core");
        add("block.sauce.source_jar_frame", "Source Jar Frame");
        add("block.sauce.source_jar_valve", "Source Jar Valve");

        add("ars_nouveau.school.necromancy", "Anima");
        add("ars_nouveau.rune.rune_necromancy", "Anima Rune");
        add("item.sauce.anima_essence", "Anima Essence");

        add("curios.modifiers.an_focus", "While in spell focus slot:");
        add("curios.modifiers.bangle", "While in bangle slot:");
        add("curios.identifier.bundle", "Bundle");
        add("curios.identifier.bangle", "Bangle");
        add("jei.sauce.charm_charging", "Charm Charging");
        add("tooltip.sauce.charm.desc", "Rechargeable");
        add("tooltip.sauce.charm.charges", "Charges %s / %s");
        add("tooltip.sauce.caster_tome", "Mimics the abilities of a school focus.");
        add("sauce.perk.summon_power", "Summoning Spell Power");
        add("sauce.perk.summon_power.desc", "Increases the power of summoning spells.");
        add("sauce.perk.manipulation_power", "Manipulation Spell Power");
        add("sauce.perk.manipulation_power.desc", "Increases the power of manipulation spells.");
        add("sauce.perk.abjuration_power", "Abjuration Spell Power");
        add("sauce.perk.abjuration_power.desc", "Increases the power of abjuration spells.");
        add("sauce.perk.necromancy_power", "Anima Spell Power");
        add("sauce.perk.necromancy_power.desc", "Increases the power of anima spells.");
        add("sauce.perk.air_power", "Air Spell Power");
        add("sauce.perk.air_power.desc", "Increases the power of air spells.");
        add("sauce.perk.earth_power", "Earth Spell Power");
        add("sauce.perk.earth_power.desc", "Increases the power of earth spells.");
        add("sauce.perk.fire_power", "Fire Spell Power");
        add("sauce.perk.fire_power.desc", "Increases the power of fire spells.");
        add("sauce.perk.water_power", "Water Spell Power");
        add("sauce.perk.water_power.desc", "Increases the power of water spells.");
        add("sauce.perk.elemental_power", "Elemental Spell Power");
        add("sauce.perk.elemental_power.desc", "Increases the power of elemental spells.");
        add("sauce.perk.summon_resistance", "Conjuration Spell Resistance");
        add("sauce.perk.summon_resistance.desc", "Reduces damage taken from summoning spells and summons. Exponential decay formula.");
        add("sauce.perk.manipulation_resistance", "Manipulation Spell Resistance");
        add("sauce.perk.manipulation_resistance.desc", "Reduces damage taken from manipulation spells and blockshaping. Exponential decay formula.");
        add("sauce.perk.abjuration_resistance", "Abjuration Spell Resistance");
        add("sauce.perk.abjuration_resistance.desc", "Reduces damage taken from abjuration spells. Exponential decay formula.");
        add("sauce.perk.necromancy_resistance", "Anima Spell Resistance");
        add("sauce.perk.necromancy_resistance.desc", "Reduces damage taken from anima spells. Exponential decay formula.");
        add("sauce.perk.air_resistance", "Air Spell Resistance");
        add("sauce.perk.air_resistance.desc", "Reduces damage taken from air spells. Exponential decay formula.");
        add("sauce.perk.earth_resistance", "Earth Spell Resistance");
        add("sauce.perk.earth_resistance.desc", "Reduces damage taken from earth spells. Exponential decay formula.");
        add("sauce.perk.fire_resistance", "Fire Spell Resistance");
        add("sauce.perk.fire_resistance.desc", "Reduces damage taken from fire spells. Exponential decay formula.");
        add("sauce.perk.water_resistance", "Water Spell Resistance");
        add("sauce.perk.water_resistance.desc", "Reduces damage taken from water spells. Exponential decay formula.");
        add("sauce.perk.elemental_resistance", "Elemental Spell Resistance");
        add("sauce.perk.elemental_resistance.desc", "Reduces damage taken from elemental spells. Exponential decay formula.");
        add("sauce.perk.spell_crit_chance", "Spell Critical Strike Chance");
        add("sauce.perk.spell_crit_chance.desc", "Chance of critical strikes with spells.");
        add("sauce.perk.spell_crit_damage_modifier", "Spell Critical Damage Boost");
        add("sauce.perk.spell_crit_damage_modifier.desc", "Damage increase of spell critical strikes.");
        add("sauce.perk.spell_damage_multiplier", "Spell Damage Multiplier (Ars)");
        add("sauce.perk.spell_damage_multiplier.desc", "Damage Multiplier of Ars Nouveau spells.");
        add("sauce.perk.mana_discount", "Mana Cost Reduction");
        add("sauce.perk.mana_discount.desc", "Reduces the mana cost of spells.");
        add("sauce.perk.mana_discount.fire", "Fire Glyph Cost Reduction");
        add("sauce.perk.mana_discount.fire.desc", "Reduces the mana cost of fire spells.");
        add("sauce.perk.mana_discount.water", "Water Glyph Cost Reduction");
        add("sauce.perk.mana_discount.water.desc", "Reduces the mana cost of water spells.");
        add("sauce.perk.mana_discount.air", "Air Glyph Cost Reduction");
        add("sauce.perk.mana_discount.air.desc", "Reduces the mana cost of air spells.");
        add("sauce.perk.mana_discount.earth", "Earth Glyph Cost Reduction");
        add("sauce.perk.mana_discount.earth.desc", "Reduces the mana cost of earth spells.");
        add("sauce.perk.mana_discount.necromancy", "Anima Glyph Cost Reduction");
        add("sauce.perk.mana_discount.necromancy.desc", "Reduces the mana cost of anima spells.");
        add("sauce.perk.mana_discount.abjuration", "Abjuration Glyph Cost Reduction");
        add("sauce.perk.mana_discount.abjuration.desc", "Reduces the mana cost of abjuration spells.");
        add("sauce.perk.mana_discount.manipulation", "Manipulation Glyph Cost Reduction");
        add("sauce.perk.mana_discount.manipulation.desc", "Reduces the mana cost of manipulation spells.");
        add("sauce.perk.mana_discount.conjuration", "Conjuration Glyph Cost Reduction");
        add("sauce.perk.mana_discount.conjuration.desc", "Reduces the mana cost of conjuration spells.");
        add("sauce.perk.mana_discount.elemental", "Elemental Glyph Cost Reduction");
        add("sauce.perk.mana_discount.elemental.desc", "Reduces the mana cost of glyphs belonging to an elemental school.");

        add("ars_nouveau.tier.prerequired", "Requires Tier %s upgrade");
        add("ars_nouveau.subform_icon_tooltip", "Subforms: Gives the rest of the spell a new form or edits its behavior.");

        add("sauce.tooltip.buffer", "Source Buffer %d / %d");
        add("sauce.tooltip.transfer_rate", "Transfer Rate %d Source/s");
        add("sauce.tooltip.cost", "Expending %d Source/cast");
        add("sauce.tooltip.required", "Requires %d Source");

        add("effect.sauce.rage", "Rage");
        add("effect.sauce.rage.desc", "Causes the target to attack nearby entities, even allies, and deal more melee damage.");

        add("effect.sauce.spell_crit_up", "Spell Affinity");
        add("effect.sauce.spell_crit_up.desc", "Increases critical strike chance of spells.");

        add("effect.sauce.mana_cost_down", "Arcanist Blessing");
        add("effect.sauce.mana_cost_down.desc", "Decreases mana cost of spells.");

        add("effect.sauce.contingency", "Contingency Spell");
        add("effect.sauce.contingency.desc", "When a specific event triggers, it will cast the spell on the entity.");
        add("ars_nouveau.contingency_icon_tooltip", "Contingencies: Stores the rest of the spell for later, resuming it when the specified condition is met. Only one can be active at a time.");

    }
}
