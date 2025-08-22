package com.alexthw.sauce.common.entity;

import com.alexthw.sauce.Sauce;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnthrallUtil {
    // Uses Eidolon style enthrallment with persistent data tag instead of a mob effect like Ars Elemental
    public static final String THRALL_KEY = Sauce.MODID + ":thrall";

    public static @Nullable LivingEntity handleEnthralledTargeting(@Nullable LivingEntity lastHurt, @Nullable LivingEntity lastHurtBy, LivingEntity thrall) {
        if (lastHurtBy != null && lastHurtBy != thrall && !thrall.isAlliedTo(lastHurtBy)) return lastHurtBy;
        if (lastHurt != null && lastHurt != thrall && !thrall.isAlliedTo(lastHurt)) return lastHurt;
        if (thrall.getLastHurtByMob() != null && !thrall.isAlliedTo(thrall.getLastHurtByMob()))
            return thrall.getLastHurtByMob();
        return null;
    }

    public static boolean isEnthralled(LivingEntity entity) {
        return entity.getPersistentData().contains(THRALL_KEY);
    }

    public static boolean isEnthralledBy(LivingEntity entity, LivingEntity owner) {
        return entity != null && owner != null && isEnthralled(entity) && entity.getPersistentData().getUUID(THRALL_KEY).equals(owner.getUUID());
    }

    public static boolean sameMaster(@NotNull LivingEntity entity, @NotNull LivingEntity source) {
        if (!isEnthralled(entity) || !isEnthralled(source)) return false;
        return entity.getPersistentData().getUUID(THRALL_KEY).equals(source.getPersistentData().getUUID(THRALL_KEY));
    }

    public static void permanentEnthrall(LivingEntity caster, LivingEntity thrall) {
        thrall.getPersistentData().putUUID(THRALL_KEY, caster.getUUID());
        if (thrall instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
    }

}
