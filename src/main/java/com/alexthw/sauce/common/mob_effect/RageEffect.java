package com.alexthw.sauce.common.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class RageEffect extends MobEffect {

    public RageEffect() {
        super(MobEffectCategory.HARMFUL, MobEffects.DAMAGE_BOOST.value().getColor());
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide() && livingEntity instanceof Mob mob) {
            if (mob.getTarget() == null) {
                mob.setTarget(mob.level().getNearestEntity(
                        mob.level().getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(mob), p_148152_ -> true),
                        TargetingConditions.forCombat().range(10).selector((e) -> true),
                        mob,
                        mob.getX(),
                        mob.getEyeY(),
                        mob.getZ()
                ));
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    protected AABB getTargetSearchArea(Mob mob) {
        return mob.getBoundingBox().inflate(10, 4.0, 10);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0 || super.shouldApplyEffectTickThisTick(duration, amplifier);
    }
}
