package com.alexthw.sauce.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PosCarryMEI extends MobEffectInstance {

    public BlockPos getOrigin() {
        return origin;
    }

    public Set<BlockPos> getAllOrigins() {
        return origins;
    }

    @Deprecated
    BlockPos origin;

    Set<BlockPos> origins = new HashSet<>();

    public PosCarryMEI(Holder<MobEffect> pEffect, int duration, int amp, boolean ambient, boolean show, BlockPos origin) {
        super(pEffect, duration, amp, ambient, show);
        this.origin = origin;
        this.origins.add(origin);
    }

    //update the origin when updating the effect
    @Override
    public boolean update(@NotNull MobEffectInstance pOther) {

        if (pOther instanceof PosCarryMEI other) {
            this.origin = other.getOrigin();
            this.origins.addAll(other.getAllOrigins());
        }

        return super.update(pOther);
    }

}
