package com.alexthw.sauce.mixin;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RelaySplitterTile.class)
public abstract class SplitterRelayTileMixin extends AbstractSourceMachine {

    public SplitterRelayTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    public void ars_elemental$getTooltip(List<Component> tooltip, CallbackInfo ci) {
        if (!ArsNouveauAPI.ENABLE_DEBUG_NUMBERS) return;
        tooltip.add(Component.translatable("sauce.tooltip.buffer", this.getSource(), this.getMaxSource()));
        tooltip.add(Component.translatable("sauce.tooltip.transfer_rate", this.getTransferRate()));
    }

}
