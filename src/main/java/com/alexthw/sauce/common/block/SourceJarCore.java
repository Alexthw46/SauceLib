package com.alexthw.sauce.common.block;

import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.alexthw.sauce.common.block.SourceJarFrame.FORMED;
import static com.alexthw.sauce.common.block.SourceJarFrame.notifyNearbyControllers;

public class SourceJarCore extends TickableModBlock {

    public SourceJarCore(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FORMED, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new DynamicSourceJarTile(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORMED);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        if (state.getValue(FORMED)) {
            // When formed, the frame blocks are invisible and only the jar renders.
            return RenderShape.ENTITYBLOCK_ANIMATED;
        }
        return super.getRenderShape(state);
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        // Ensure we only trigger if it's a new block placement,
        // not just a BlockState property updating (like waterlogging).
        if (!state.is(oldState.getBlock())) {
            notifyNearbyControllers(level, pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        // Ensure we only trigger if the block is actually being destroyed.
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof DynamicSourceJarTile tile) {
                tile.setFormed(false);
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
}
