package com.alexthw.sauce.mixin;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SourceJarTile.class)
public abstract class JarTileMixin extends AbstractSourceMachine {

    public JarTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    public void sauce$getTooltip(List<Component> tooltip, CallbackInfo ci) {
        if (!ArsNouveauAPI.ENABLE_DEBUG_NUMBERS) return;
        tooltip.add(Component.translatable("sauce.tooltip.buffer", this.getSource(), this.getMaxSource()));
        tooltip.add(Component.translatable("sauce.tooltip.transfer_rate", this.getTransferRate()));
    }

}
