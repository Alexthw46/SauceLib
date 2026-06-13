package com.alexthw.sauce.mixin;

import com.hollingsworth.arsnouveau.api.spell.IDamageEffect;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IDamageEffect.class)
public interface BetterFilters {

    @Inject(method = "attemptDamage", at = @At("HEAD"), cancellable = true)
    default void attemptDamage(Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, Entity entity, DamageSource source, float baseDamage, CallbackInfoReturnable<Boolean> cir) {
        if (spellContext != null && spellContext.getCurrentIndex() > 1 && spellContext.getSpell().unsafeList().get(spellContext.getCurrentIndex() - 2) instanceof IFilter filter) {
            if (!filter.shouldResolveOnEntity(new EntityHitResult(entity), world, stats, spellContext, resolver)) {
                cir.setReturnValue(false);
            }
        }
    }

}
