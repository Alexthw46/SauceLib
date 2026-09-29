package com.alexthw.sauce.common.block;

import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ArcanePlinthTile extends ArcanePedestalTile {

    public ArcanePlinthTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ArcanePlinthTile(BlockPos pos, BlockState state) {
        super(ModRegistry.ARCANE_PLINTH_TILE.get(), pos, state);
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return true;
    }

}
