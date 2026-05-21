package com.alexthw.sauce.common.block;

import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DynamicSourceJarTileValve extends SourceJarTile {
    public DynamicSourceJarTileValve(BlockPos pos, BlockState state) {
        super(ModRegistry.BIG_SOURCE_JAR_VALVE.get(), pos, state);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return ModRegistry.BIG_SOURCE_JAR_VALVE.get();
    }

    BlockPos corePos;

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.getMaxSource() > 0) {
            tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (getSource() * 100) / this.getMaxSource()));
            if (!ArsNouveauAPI.ENABLE_DEBUG_NUMBERS) return;
            tooltip.add(Component.translatable("sauce.tooltip.buffer", this.getSource(), this.getMaxSource()));
            tooltip.add(Component.translatable("sauce.tooltip.transfer_rate", this.getTransferRate()));
        }
    }

    @Override
    protected @NotNull SourceStorage createDefaultStorage() {
        return new SourceStorage(0, 0);
    }

    @Override
    public @NotNull SourceStorage getSourceStorage() {
        if (corePos != null && level != null)
            if (level.getBlockEntity(corePos) instanceof DynamicSourceJarTile dynamicSourceJarTile) {
                return dynamicSourceJarTile.getSourceStorage();
            } else {
                // corePos became invalid
                corePos = null;
            }
        return super.getSourceStorage();
    }

    public int setSource(int source) {
        if (corePos != null && level != null)
            if (level.getBlockEntity(corePos) instanceof DynamicSourceJarTile dynamicSourceJarTile) {
                dynamicSourceJarTile.getSourceStorage().setSource(Math.clamp(source, 0, this.getMaxSource()));
                dynamicSourceJarTile.updateBlock();
                return dynamicSourceJarTile.getSourceStorage().getSource();
            } else {
                // corePos became invalid
                corePos = null;
            }

        return super.setSource(source);
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (corePos != null)
            tag.putLong("corePos", corePos.asLong());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        if (tag.contains("corePos")) corePos = BlockPos.of(tag.getLong("corePos"));
    }


}
