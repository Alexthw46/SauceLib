package com.alexthw.sauce.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PosCarryMEI extends MobEffectInstance {

    public BlockPos getOrigin() {
        return origin;
    }

    public List<BlockPos> getAllOrigins() {
        return origins;
    }

    @Deprecated
    BlockPos origin;

    List<BlockPos> origins = new ArrayList<>();

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
