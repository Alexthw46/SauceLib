package com.alexthw.sauce.event;

import alexthw.ars_elemental.common.entity.mages.EntityMageBase;
import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.registry.ModRegistry;
import com.alexthw.sauce.registry.SauceTags;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeEventHandler {

    public static final Map<SpellSchool, Holder<Attribute>> schoolToPowerAttribute = new ConcurrentHashMap<>();
    public static final Map<SpellSchool, Holder<Attribute>> schoolToDefenseAttribute = new ConcurrentHashMap<>();

    public static void linkSchoolToAttribute(SpellSchool school, Holder<Attribute> powerAttribute, Holder<Attribute> defenseAttribute) {
        if (powerAttribute != null)
            schoolToPowerAttribute.put(school, powerAttribute);
        if (defenseAttribute != null)
            schoolToDefenseAttribute.put(school, defenseAttribute);
    }

    static {
        linkSchoolToAttribute(SpellSchools.MANIPULATION, ModRegistry.MANIPULATION_POWER, ModRegistry.MANIPULATION_RESISTANCE);
        linkSchoolToAttribute(SpellSchools.CONJURATION, ModRegistry.SUMMON_POWER, ModRegistry.CONJURATION_RESISTANCE);
        linkSchoolToAttribute(SpellSchools.ABJURATION, ModRegistry.ABJURATION_POWER, ModRegistry.ABJURATION_RESISTANCE);
        linkSchoolToAttribute(SpellSchools.NECROMANCY, ModRegistry.NECROMANCY_POWER, ModRegistry.NECROMANCY_RESISTANCE);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL_AIR, ModRegistry.AIR_POWER, ModRegistry.AIR_RESISTANCE);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL_WATER, ModRegistry.WATER_POWER, ModRegistry.WATER_RESISTANCE);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL_EARTH, ModRegistry.EARTH_POWER, ModRegistry.EARTH_RESISTANCE);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL_FIRE, ModRegistry.FIRE_POWER, ModRegistry.FIRE_RESISTANCE);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL, ModRegistry.ELEMENTAL_POWER, ModRegistry.ELEMENTAL_RESISTANCE);
    }

    @SubscribeEvent
    public static void discountSpell(final SpellCostCalcEvent event) {
        if (event.context.getCaster() instanceof LivingCaster caster) {
            if (caster.livingEntity instanceof Player player && !(player instanceof FakePlayer)) {
                AttributeInstance perk = player.getAttribute(ModRegistry.MANA_DISCOUNT);
                if (perk != null) {
                    event.currentCost -= (int) perk.getValue();
                }
            }
        }
    }

    @SubscribeEvent
    public static void critChance(SpellDamageEvent.Pre pre) {
        if (!Sauce.ENABLE_SPELL_CRIT) return;
        if (pre.caster instanceof Player player && !(player instanceof FakePlayer)) {
            AttributeInstance critChance = player.getAttribute(ModRegistry.SPELL_CRIT);
            AttributeInstance critDamage = player.getAttribute(ModRegistry.SPELL_CRIT_DAMAGE);
            if (critChance != null && critDamage != null) {
                double chance = critChance.getValue();
                if (chance > 0 && pre.caster.getRandom().nextDouble() < (chance / 100)) {
                    pre.damage *= (float) (1 + critDamage.getValue() / 100.0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void elementalDefense(SpellDamageEvent.Pre pre) {
        if (!(pre.target instanceof LivingEntity living)) return;
        if (living instanceof Player || living instanceof EntityMageBase) {
            Set<SpellSchool> schools = new HashSet<>();
            // we don't have the spell part, so we need to deduce the schools from the damage type tags
            for (Map.Entry<SpellSchool, Holder<Attribute>> entry : schoolToDefenseAttribute.entrySet()) {
                SpellSchool school = entry.getKey();
                List<TagKey<DamageType>> tags = SauceTags.SCHOOL_TO_DAMAGE_TYPES.getOrDefault(school, List.of());
                if (tags.stream().anyMatch(tag -> pre.damageSource.is(tag))) {
                    schools.add(school);
                    // Added to support Elemancy mixed schools
                    // Avoids including all elemental schools, since all elemental glyphs have ELEMENTAL as one of the schools
                    if (school != SpellSchools.ELEMENTAL)
                        schools.addAll(school.getSubSchools());
                }
            }
            // Translate the schools into attributes and apply the resistances
            for (SpellSchool school : schools) {
                Holder<Attribute> attribute = schoolToDefenseAttribute.get(school);
                if (attribute != null) {
                    AttributeInstance attrInstance = living.getAttribute(attribute);
                    if (attrInstance != null) {
                        double resistance = attrInstance.getValue();
                        if (resistance != 0) {
                            pre.damage *= (float) Math.pow(2.0, -resistance / 100.0);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void empowerBySchool(SpellModifierEvent event) {
        if (event.spellPart == null) return;
        List<SpellSchool> schools = event.spellPart.spellSchools;
        if (schools.isEmpty()) return;
        for (SpellSchool school : schools) {
            empowerSchool(event, school);
            if (school != SpellSchools.ELEMENTAL)
                for (SpellSchool subSchool : school.getSubSchools()) {
                    empowerSchool(event, subSchool);
                }
        }
    }

    private static void empowerSchool(SpellModifierEvent event, SpellSchool school) {
        Holder<Attribute> attribute = schoolToPowerAttribute.get(school);
        if (attribute != null && event.caster != null) {
            double power = event.caster.getAttributes().hasAttribute(attribute) ? event.caster.getAttributeValue(attribute) : 0;
            if (power != 0) {
                event.builder.addDamageModifier(power);
            }
        }
    }

}
