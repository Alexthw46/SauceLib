package com.alexthw.sauce.util;

import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class ContingencyEffectInstance extends MobEffectInstance {

    protected ContingencyTrigger trigger;
    SpellResolver spell;
    double amplifier;
    int activations, max_activations;

    public ContingencyEffectInstance(SpellResolver spell, ContingencyTrigger trigger, int duration, double amplifier, int max_activations) {
        super(ModRegistry.CONTINGENCY, duration, 0, false, false);
        this.spell = spell;
        this.trigger = trigger;
        this.amplifier = amplifier;
        this.activations = 0;
        this.max_activations = max_activations; // Set the maximum activations, first not counted
    }

    public ContingencyTrigger getTrigger() {
        return trigger;
    }

    public void triggerSpell(LivingEntity entity) {
        if (activations <= max_activations) {
            activations++; // Increase before resolve, otherwise a contingency set in the spell will be increased instead
            spell.onResolveEffect(entity.level(), new EntityHitResult(entity));
        }
    }

    @Override
    public void onMobHurt(@NotNull LivingEntity livingEntity, @NotNull DamageSource damageSource, float amount) {
        getTrigger().onMobHurt(this, livingEntity, damageSource, amount);
    }

    @Override
    public boolean update(@NotNull MobEffectInstance other) {
        if (!(other instanceof ContingencyEffectInstance contingency)) return false;
        super.update(other); // update basic stuff
        // update Contingency data
        this.trigger = contingency.trigger;
        this.amplifier = contingency.amplifier;
        this.activations = contingency.activations;
        this.max_activations = contingency.max_activations;
        this.spell = contingency.spell;
        return true;
    }

    @Override
    public boolean tick(@NotNull LivingEntity entity, @NotNull Runnable onExpirationRunnable) {
        if (entity.level().isClientSide) return super.tick(entity, onExpirationRunnable);

        boolean ret = getTrigger().onTick(this, entity);

        if (!ret) return false;

        // chain the runnable with an additional method to trigger the spell if the trigger is EXPIRE
        Runnable newExpirationRunnable = () -> {
            if (trigger == EXPIRE) {
                triggerSpell(entity);
                if (activations > max_activations) {
                    onExpirationRunnable.run();
                }
            } else onExpirationRunnable.run();
        };

        // Remove the effect if max activations reached by returning false
        return super.tick(entity, newExpirationRunnable) && activations <= max_activations;
    }

    public static final ContingencyTrigger EXPIRE = new ContingencyTrigger() {
    };

    /*
     * Trigger defined as interface to keep the two callbacks inside effect easy to use and extensible
     */
    public interface ContingencyTrigger {
        /*
         * Called when the entity with the contingency is hurt
         */
        default void onMobHurt(ContingencyEffectInstance instance,
                               LivingEntity entity,
                               DamageSource source,
                               float amount) {
        }

        /*
         * Return false if the effect should be terminated early
         */
        default boolean onTick(ContingencyEffectInstance instance,
                               LivingEntity entity) {
            return true;
        }

    }
}
