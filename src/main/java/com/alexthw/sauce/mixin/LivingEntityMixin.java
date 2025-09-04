package com.alexthw.sauce.mixin;

import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.common.entity.EnthrallUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static com.alexthw.sauce.common.entity.EnthrallUtil.THRALL_KEY;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("TAIL"), cancellable = true)
    public void sauce$canAttack(LivingEntity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (!Sauce.ENABLE_ENTHRALL) return;
        if (((Entity) (Object) this) instanceof LivingEntity living && pTarget != null) {
            if (EnthrallUtil.isEnthralled(living)) {
                if (living.isAlliedTo(pTarget)) cir.setReturnValue(false);
                UUID master = living.getPersistentData().getUUID(THRALL_KEY);
                if (living.level().getPlayerByUUID(master) instanceof ServerPlayer player) {
                    LivingEntity lastHurt = player.getLastHurtMob();
                    LivingEntity lastHurtBy = player.getLastHurtByMob();
                    // if the target is not one of the player's last hurt mobs, don't attack
                    if (lastHurt != pTarget && lastHurtBy != pTarget) cir.setReturnValue(false);
                }
            }
        }
    }

}
