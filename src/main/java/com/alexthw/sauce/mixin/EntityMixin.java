package com.alexthw.sauce.mixin;

import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.common.entity.EnthrallUtil;
import com.alexthw.sauce.registry.ModRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("TAIL"), cancellable = true)
    public void sauce$isAlliedTo(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!Sauce.ENABLE_ENTHRALL) return;
        if (((Entity) (Object) this) instanceof LivingEntity living && pEntity instanceof LivingEntity target) {
            if (living.hasEffect(ModRegistry.RAGE)) {
                cir.setReturnValue(false);
            } else if (EnthrallUtil.isEnthralled(living)) {
                cir.setReturnValue(EnthrallUtil.isEnthralledBy(living, target) || EnthrallUtil.sameMaster(living, target));
            }
        }
    }

}
