package com.alexthw.sauce.event;


import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.common.entity.FollowOwnerGoal;
import com.alexthw.sauce.common.entity.ThrallTargetGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

import java.util.UUID;

import static com.alexthw.sauce.common.entity.EnthrallUtil.*;

public class GenericEventHandler {

    @SubscribeEvent
    public static void onEnthrallTarget(LivingChangeTargetEvent event) {
        if (!isEnthralled(event.getEntity())) return;
        UUID master = event.getEntity().getPersistentData().getUUID(THRALL_KEY);
        LivingEntity newTarget = null;
        if (isEnthralledBy(event.getEntity(), event.getNewAboutToBeSetTarget())) {
            LivingEntity lastHurt = event.getNewAboutToBeSetTarget().getLastHurtMob();
            LivingEntity lastHurtBy = event.getNewAboutToBeSetTarget().getLastHurtByMob();
            newTarget = handleEnthralledTargeting(lastHurt, lastHurtBy, event.getEntity());
        } else if (event.getEntity().level() instanceof ServerLevel server && server.getEntity(master) instanceof LivingEntity living) {
            LivingEntity lastHurt = living.getLastHurtMob();
            LivingEntity lastHurtBy = living.getLastHurtByMob();
            newTarget = handleEnthralledTargeting(lastHurt, lastHurtBy, event.getEntity());
        }

        if (newTarget == null) {
            // If the player has no last hurt mob, set the target to null.
            event.setNewAboutToBeSetTarget(null);
            if (event.getEntity() instanceof NeutralMob angry) angry.setRemainingPersistentAngerTime(0);

        } else if (event.getEntity() instanceof NeutralMob angry)
            angry.setPersistentAngerTarget(newTarget.getUUID());
    }

    @SubscribeEvent
    public static void registerCustomAI(EntityJoinLevelEvent event) {
        if (!Sauce.ENABLE_ENTHRALL) return;
        if (event.getEntity() instanceof LivingEntity && !event.getLevel().isClientSide) {
            if (event.getEntity() instanceof PathfinderMob mob && (mob.getNavigation() instanceof GroundPathNavigation || mob.getNavigation() instanceof FlyingPathNavigation)) {
                try {
                    mob.goalSelector.addGoal(2, new FollowOwnerGoal(mob, 1.5F, 3.0F, 1.2F));
                    mob.targetSelector.addGoal(1, new ThrallTargetGoal(mob));
                } catch (IllegalArgumentException ignored) {

                }
            }
        }
    }
}