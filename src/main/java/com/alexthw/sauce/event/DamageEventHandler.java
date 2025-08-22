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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
    public static void damageTweaking(LivingIncomingDamageEvent event) {
        var dealer = event.getSource().getEntity();
        var target = event.getEntity();


        //fetch the damage reduction from the armor according to the damage source
        HashMap<SpellSchool, Integer> bonusMap = new HashMap<>();
        int bonusReduction = 0;

        for (ItemStack stack : event.getEntity().getArmorSlots()) {
            Item item = stack.getItem();
            if (item instanceof IElementalArmor armor && armor.fillAbsorptions(event.getSource(), bonusMap)) {
                bonusReduction++;
            }
        }

        boolean not_bypassEnchants = !event.getSource().is(DamageTypeTags.BYPASSES_ENCHANTMENTS);

        if (target instanceof Player) {

            Set<SpellSchool> schools = ISchoolFocus.getFociSchools(target);

            if (not_bypassEnchants) {
                //reduce damage from elytra if you have air focus
                if (event.getSource().is(DamageTypes.FLY_INTO_WALL) && schools.contains(ELEMENTAL_AIR)) {
                    event.setAmount(event.getAmount() * .1F);
                }

                //if you have 4 pieces of the fire school, fire is removed. Apply the fire focus buff if you have it, since it wouldn't detect the fire otherwise
                if (bonusMap.getOrDefault(SpellSchools.ELEMENTAL_FIRE, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FIRE)) {
                    target.clearFire();
                    if (schools.contains(SpellSchools.ELEMENTAL_FIRE)) {
                        target.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT, 200, 2));
                    }
                }

                //if you have 4 pieces of the water school, you get extra air when drowning
                if (bonusMap.getOrDefault(SpellSchools.ELEMENTAL_WATER, 0) == 4 && event.getSource().is(DamageTypes.DROWN)) {
                    target.setAirSupply(target.getMaxAirSupply());
                    bonusReduction += 5;
                }
                //if you have 4 pieces of the earth school, you get extra food when you are low
                if (target instanceof Player player && bonusMap.getOrDefault(ELEMENTAL_EARTH, 0) == 4 && target.getEyePosition().y() < 20 && player.getFoodData().getFoodLevel() < 4) {
                    player.getFoodData().setFoodLevel(20);
                }

                //if you have 4 pieces of the air school, you get extra fall damage reduction
                if (bonusMap.getOrDefault(ELEMENTAL_AIR, 0) == 4 && event.getSource().is(DamageTypeTags.IS_FALL)) {
                    bonusReduction += 5;
                }

                if (bonusReduction > 0) {
                    //convert the damage reduction into mana and add the mana regen effect
                    var mana = CapabilityRegistry.getMana(target);
                    if (mana != null) {
                        if (bonusReduction > 3) mana.addMana(event.getOriginalAmount() * 5);
                        event.getEntity().addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 200, bonusReduction / 2));
                    }
                }

            }
        }

        if (bonusReduction > 0 && not_bypassEnchants)
            event.setAmount(event.getAmount() * (1 - bonusReduction / 10F));
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
