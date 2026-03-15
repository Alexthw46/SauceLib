package com.alexthw.sauce.event;

import com.alexthw.sauce.api.item.IElementalArmor;
import com.alexthw.sauce.api.item.ISchoolFocus;
import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.entity.EntityEvokerFangs;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashMap;
import java.util.Set;

import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_AIR;
import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_EARTH;

public class DamageEventHandler {

    @SubscribeEvent
    public static void betterFilters(SpellDamageEvent.Pre event) {
        //if the spell has a filter, and the target of the attack is not valid, cancel the event
        // event.context.getCurrentIndex() - 1 is the current, we check the one before it
        if (event.context != null && event.context.getCurrentIndex() > 1 && event.context.getSpell().unsafeList().get(event.context.getCurrentIndex() - 2) instanceof IFilter filter) {
            if (!filter.shouldResolveOnEntity(event.target, event.target.level())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void elementalDamageReduction(LivingIncomingDamageEvent event) {
        var dealer = event.getSource().getEntity();
        var target = event.getEntity();


        //fetch the damage reduction from the armor according to the damage source
        HashMap<SpellSchool, Integer> elementalAbsorption = new HashMap<>();
        int light_pieces = 0;
        int medium_pieces = 0;
        int heavy_pieces = 0;
        int bonusReduction = 0;

        for (ItemStack stack : event.getEntity().getArmorSlots()) {
            if (stack.getItem() instanceof IElementalArmor armor) {
                if (armor.fillAbsorptions(event.getSource(), elementalAbsorption)) {
                    switch (armor.getTier()) {
                        case "light" -> light_pieces++;
                        case "medium" -> medium_pieces++;
                        case "heavy" -> heavy_pieces++;
                    }
                }
            }
        }
        bonusReduction += Mth.floor(light_pieces * 0.5 + medium_pieces + heavy_pieces * 1.5);

        boolean not_bypassEnchants = !event.getSource().is(DamageTypeTags.BYPASSES_ENCHANTMENTS);

        if (target instanceof Player player) {

            Set<SpellSchool> schools = ISchoolFocus.getFociSchools(target);

            // Handle Elemental Set bonus, regardless of type
            if (not_bypassEnchants && !elementalAbsorption.isEmpty()) {
                //if you have 4 pieces of the fire school, fire is removed. Apply the fire focus buff if you have it, since it wouldn't detect the fire otherwise
                if (elementalAbsorption.getOrDefault(SpellSchools.ELEMENTAL_FIRE, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FIRE)) {
                    target.clearFire();
                    // Apply the focus effect, since the fire is cleared early
                    if (schools.contains(SpellSchools.ELEMENTAL_FIRE)) {
                        target.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT, 200, 1));
                    }
                }

                //if you have 4 pieces of the water school, you get extra air when drowning
                if (elementalAbsorption.getOrDefault(SpellSchools.ELEMENTAL_WATER, 0) == 4 && event.getSource().is(DamageTypes.DROWN)) {
                    target.setAirSupply(target.getMaxAirSupply());
                    bonusReduction += 4;
                }

                //if you have 4 pieces of the earth school, you get extra food when you are low
                if (elementalAbsorption.getOrDefault(ELEMENTAL_EARTH, 0) == 4 && target.getEyePosition().y() < 20 && player.getFoodData().getFoodLevel() < 4) {
                    player.getFoodData().setFoodLevel(20);
                }

                //if you have 4 pieces of the air school, you get extra fall damage reduction
                if (elementalAbsorption.getOrDefault(ELEMENTAL_AIR, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FALL)) {
                    bonusReduction += 4;
                }

                // Cooldown for absorption effect using the iframes timer, to avoid exploit with continuous damage ticks
                if (target.invulnerableTime <= 10) {
                    // Based on how many pieces of the armor type, give a different effect
                    // light -> mana regen
                    // medium -> mana discount
                    // heavy -> overflow mana into heal
                    if (light_pieces >= 2) {
                        player.addEffect(new MobEffectInstance(ModRegistry.SPELL_CRIT_UP, 20 * 10, light_pieces / 2 - 1));
                    }
                    if (medium_pieces >= 2) {
                        player.addEffect(new MobEffectInstance(ModRegistry.DISCOUNT_MANA, 20 * 10, medium_pieces / 2 - 1));
                    }
                    if (bonusReduction >= 2) {
                        //At least full light set
                        //add the mana regen effect if some damage was absorbed
                        //If a special condition was triggered or enough heavy pieces were equipped
                        //convert the damage reduction into mana
                        var mana = CapabilityRegistry.getMana(target);
                        // At least full medium set or heavy-mix
                        if (mana != null && (heavy_pieces >= 2 || bonusReduction >= 4)) {
                            double manaToRestore = event.getOriginalAmount() * bonusReduction;
                            double extraMana = (manaToRestore + mana.getCurrentMana()) - mana.getMaxMana();
                            mana.addMana(manaToRestore);
                            // Convert extra mana into healing with full heavy build
                            if (extraMana > 0 && heavy_pieces >= 3) {
                                target.heal(Math.min((float) (extraMana * heavy_pieces / 100), 10f));
                            }
                        }
                        event.getEntity().addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 200));
                    }
                }
            }

            //reduce damage from elytra if you have air focus
            if (event.getSource().is(DamageTypes.FLY_INTO_WALL) && schools.contains(ELEMENTAL_AIR)) {
                event.setAmount(event.getAmount() * 0.2F);
            }

            // Damage reduction from indirect sources covered by Sauce Attributes
            if (dealer instanceof ISummon) {
                Holder<Attribute> attribute = ModRegistry.CONJURATION_RESISTANCE;
                if (attribute != null) {
                    AttributeInstance attrInstance = target.getAttribute(attribute);
                    if (attrInstance != null) {
                        double resistance = attrInstance.getValue();
                        if (resistance != 0) {
                            //avoid negatives
                            event.setAmount(Math.max(0.1F, event.getAmount() * (float) Math.pow(2.0, -resistance / 100.0)));
                        }
                    }
                }
            } else if (dealer instanceof EnchantedFallingBlock) {
                Holder<Attribute> attribute = ModRegistry.MANIPULATION_RESISTANCE;
                if (attribute != null) {
                    AttributeInstance attrInstance = target.getAttribute(attribute);
                    if (attrInstance != null) {
                        double resistance = attrInstance.getValue();
                        if (resistance != 0) {
                            // avoid negatives
                            event.setAmount(Math.max(0.1F, event.getAmount() * (float) Math.pow(2.0, -resistance / 100.0)));
                        }
                    }
                }
            }

        }

        if (bonusReduction > 0 && not_bypassEnchants && event.getOriginalAmount() > 0.1)
            event.setAmount(Math.max(0.1F, event.getAmount() * (1 - bonusReduction / 10F)));
    }

    @SubscribeEvent
    public static void empowerEntitiesBySchool(LivingDamageEvent.Pre event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity == null || !(sourceEntity.level() instanceof ServerLevel)) return;
        switch (sourceEntity) {
            case ISummon summon when summon.getOwner() instanceof Player player ->
                    event.setNewDamage((float) (event.getNewDamage() + player.getAttributeValue(ModRegistry.SUMMON_POWER)));
            case EnchantedFallingBlock fallingBlock when fallingBlock.getOwner() instanceof Player player ->
                    event.setNewDamage((float) (event.getNewDamage() + player.getAttributeValue(ModRegistry.MANIPULATION_POWER)));
            case EntityEvokerFangs fangs when fangs.getOwner() instanceof Player player ->
                    event.setNewDamage((float) (event.getNewDamage() + player.getAttributeValue(ModRegistry.SUMMON_POWER)));
            default -> {
            }
        }
    }

}
