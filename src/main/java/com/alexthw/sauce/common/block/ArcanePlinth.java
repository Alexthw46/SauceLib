package com.alexthw.sauce.common.block;

import com.hollingsworth.arsnouveau.common.block.ArcanePedestal;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ArcanePlinth extends ArcanePedestal {

    public ArcanePlinth() {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcanePlinthTile(pos, state);
    }

    public static final VoxelShape UP = makeShape();
    public static final VoxelShape DOWN = VoxelShapeUtils.rotate(UP, Direction.UP);
    public static final VoxelShape EAST = VoxelShapeUtils.rotate(UP, Direction.WEST);
    public static final VoxelShape WEST = VoxelShapeUtils.rotate(UP, Direction.EAST);
    public static final VoxelShape NORTH = VoxelShapeUtils.rotate(UP, Direction.SOUTH);
    public static final VoxelShape SOUTH = VoxelShapeUtils.rotate(UP, Direction.NORTH);

    static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0.6875, 0.125, 0.875, 0.8125, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, -0.00625, 0.125, 0.875, 0.18125, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.5, 0.1875, 0.8125, 0.6875, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.225, 0.1625, 0.225, 0.775, 0.525, 0.775), BooleanOp.OR);
        return shape;
    }

    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return switch (facing) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack pStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!world.isClientSide && world.getBlockEntity(pos) instanceof ArcanePedestalTile tile) {
            ItemStack pedestalStack = tile.getStack();
            ItemStack handStack = player.getItemInHand(handIn);

            if (!pedestalStack.isEmpty() && handStack.isEmpty()) {
                world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), pedestalStack.copy()));
                tile.setStack(ItemStack.EMPTY);
            } else if (!handStack.isEmpty()) {
                if (!pedestalStack.isEmpty()) {
                    world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), pedestalStack.copy()));
                }
                tile.setStack(handStack.copy());
                player.setItemInHand(handIn, ItemStack.EMPTY);
            }

            world.sendBlockUpdated(pos, state, state, 2);
        }

        return ItemInteractionResult.SUCCESS;
    }
}
