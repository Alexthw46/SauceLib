package com.alexthw.sauce.mixin;

import com.alexthw.sauce.util.GlyphEffectUtil;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityOrbitProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(EntityOrbitProjectile.class)
public abstract class EntityOrbitMixin extends EntityProjectileSpell {


    @Inject(method = "<init>*", at = @At("TAIL"))
    public void constructor(Level world, SpellResolver resolver, Entity tracking, CallbackInfo ci) {
        Set<IFilter> filters = GlyphEffectUtil.getFilters(resolver.spell.unsafeList(), 0);
        List<Predicate<LivingEntity>> ignore = new ArrayList<>();
        if (!filters.isEmpty()) {
            ignore.add((entity) -> GlyphEffectUtil.checkIgnoreFilters(entity, filters));
            sauce$setIgnored(ignore);
        }
    }

    @Unique
    List<Predicate<LivingEntity>> sauce$ignore;

    public EntityOrbitMixin(EntityType<? extends EntityProjectileSpell> entityType, Level world) {
        super(entityType, world);
    }


    @Unique
    public void sauce$setIgnored(List<Predicate<LivingEntity>> ignore) {
        this.sauce$ignore = ignore;
    }

    @Unique
    public List<Predicate<LivingEntity>> sauce$getIgnored() {
        return this.sauce$ignore;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        boolean b = super.canHitEntity(entity);
        if (entity instanceof LivingEntity) b = b && sauce$shouldTarget((LivingEntity) entity);
        return b;
    }

    @Unique
    private boolean sauce$shouldTarget(LivingEntity e) {
        if (sauce$ignore == null) return true;
        for (Predicate<LivingEntity> p : sauce$getIgnored()) {
            if (p.test(e)) {
                return false;
            }
        }
        return true;
    }

}
