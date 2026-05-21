package com.alexthw.sauce.common.block;

import com.hollingsworth.arsnouveau.common.block.ITickableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SourceJarValve extends SourceJarFrame implements ITickableBlock {

    public SourceJarValve(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new DynamicSourceJarTileValve(blockPos, blockState);
    }

}
