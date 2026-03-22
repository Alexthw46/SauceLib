package com.alexthw.sauce.common.block;

import com.hollingsworth.arsnouveau.common.block.ModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SourceJarFrame extends ModBlock {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public SourceJarFrame(Properties properties) {
        super(properties);
        registerDefaultState(this.defaultBlockState().setValue(FORMED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORMED);
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
            notifyNearbyControllers(level, pos);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    /**
     * Scans the surrounding area for Controllers and tells them to validate.
     * Since the max multiblock size is 9x9x9, the controller can be at most 9 blocks away.
     */
    public static void notifyNearbyControllers(Level level, BlockPos pos) {
        if (level.isClientSide) return;

        int radius = 9;
        BlockPos start = pos.offset(-radius, -radius, -radius);
        BlockPos end = pos.offset(radius, radius, radius);

        // BlockPos.betweenClosed is highly optimized for iterating over volumes
        for (BlockPos checkPos : BlockPos.betweenClosed(start, end)) {
            BlockEntity be = level.getBlockEntity(checkPos);

            if (be instanceof DynamicSourceJarTile controller) {
                controller.validateMultiblock();
                // Note: We deliberately do NOT `break;` here.
                // If a player places a frame between two different battery
                // structures, we want to try update both of them.
            }
        }
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        if (state.getValue(FORMED)) {
            // When formed, the frame blocks are invisible and only the jar renders.
            return RenderShape.INVISIBLE;
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
}
