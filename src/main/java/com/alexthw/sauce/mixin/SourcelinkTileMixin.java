package com.alexthw.sauce.mixin;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.tile.SourcelinkTile;
import com.hollingsworth.nuggets.client.overlay.IWorldTooltipProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(SourcelinkTile.class)
public abstract class SourcelinkTileMixin extends AbstractSourceMachine implements IWorldTooltipProvider {
    public SourcelinkTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Unique
    public void getTooltip(List<Component> tooltip) {
        if (!ArsNouveauAPI.ENABLE_DEBUG_NUMBERS) return;
        tooltip.add(Component.translatable("sauce.tooltip.buffer", this.getSource(), this.getMaxSource()));
        tooltip.add(Component.translatable("sauce.tooltip.transfer_rate", this.getTransferRate()));
    }

}
