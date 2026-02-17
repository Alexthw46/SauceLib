package com.alexthw.sauce.common.mob_effect;

import com.hollingsworth.arsnouveau.common.potions.PublicEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ContingencyEffect extends PublicEffect {

    public ContingencyEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
    }

    @Override
    public void fillEffectCures(@NotNull Set<EffectCure> cures, @NotNull MobEffectInstance effectInstance) {
    }
}
