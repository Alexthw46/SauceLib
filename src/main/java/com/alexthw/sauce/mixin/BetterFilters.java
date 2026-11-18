package com.alexthw.sauce.mixin;

import com.alexthw.sauce.api.AlliesSavedData;
import com.hollingsworth.arsnouveau.api.spell.IDamageEffect;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
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

    @Inject(method = "canDamage", at = @At("TAIL"), cancellable = true)
    default void canDamage(LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // small clause for magic pets and summons
        if (shooter instanceof OwnableEntity pet) shooter = pet.getOwner();
        // the check only matters for players server-side
        if (!(cir.getReturnValue() && shooter instanceof Player dealer && dealer.level() instanceof ServerLevel serverLevel))
            return;
        cir.setReturnValue(!AlliesSavedData.isAlly(serverLevel, dealer.getUUID(), entity));
    }

}
