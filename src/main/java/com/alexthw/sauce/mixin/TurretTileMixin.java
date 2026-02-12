package com.alexthw.sauce.mixin;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BasicSpellTurretTile.class)
public abstract class TurretTileMixin {

    @Shadow
    public abstract int getManaCost();

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    public void sauce$getTooltip(List<Component> tooltip, CallbackInfo ci) {
        if (!ArsNouveauAPI.ENABLE_DEBUG_NUMBERS) return;
        tooltip.add(Component.translatable("sauce.tooltip.cost", this.getManaCost()));
    }
}
