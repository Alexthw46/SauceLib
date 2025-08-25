package com.alexthw.sauce.event;

import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeEventHandler {

    static Map<SpellSchool, Holder<Attribute>> schoolToAttribute = new ConcurrentHashMap<>();

    public static void linkSchoolToAttribute(SpellSchool school, Holder<Attribute> attribute) {
        schoolToAttribute.put(school, attribute);
    }

    static {
        linkSchoolToAttribute(SpellSchools.MANIPULATION, ModRegistry.MANIPULATION_POWER);
        linkSchoolToAttribute(SpellSchools.CONJURATION, ModRegistry.SUMMON_POWER);
        linkSchoolToAttribute(SpellSchools.ABJURATION, ModRegistry.ABJURATION_POWER);
        linkSchoolToAttribute(SpellSchools.NECROMANCY, ModRegistry.NECROMANCY_POWER);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL_AIR, ModRegistry.AIR_POWER);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL_WATER, ModRegistry.WATER_POWER);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL_EARTH, ModRegistry.EARTH_POWER);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL_FIRE, ModRegistry.FIRE_POWER);
        linkSchoolToAttribute(SpellSchools.ELEMENTAL, ModRegistry.ELEMENTAL_POWER);
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
    public static void empowerBySchool(SpellModifierEvent event) {
        List<SpellSchool> schools = event.spellPart.spellSchools;
        if (schools.isEmpty()) return;
        for (SpellSchool school : schools) {
            empowerSchool(event, school);
            for (SpellSchool subSchool : school.getSubSchools()) {
                empowerSchool(event, subSchool);
            }
        }
    }

    private static void empowerSchool(SpellModifierEvent event, SpellSchool school) {
        Holder<Attribute> attribute = schoolToAttribute.get(school);
        if (attribute != null && event.caster != null) {
            double power = event.caster.getAttributes().hasAttribute(attribute) ? event.caster.getAttributeValue(attribute) : 0;
            if (power != 0) {
                event.builder.addDamageModifier(power);
            }
        }
    }

}
