package com.alexthw.sauce.common.block;

import com.alexthw.sauce.api.item.ISchoolFocus;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FocusEnhancedSpellTurret extends BasicSpellTurret {

    public final SpellSchool school;

    public FocusEnhancedSpellTurret(Properties properties, SpellSchool school) {
        super(properties.noOcclusion().forceSolidOn());
        this.school = school;
    }

    @Override
    public void setPlacedBy(Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        Direction orientation = placer == null ? Direction.WEST : Direction.orderedByNearest(placer)[0].getOpposite();

        if (!(world.getBlockEntity(pos) instanceof RotatingTurretTile turretTile)) return;
        switch (orientation) {
            case DOWN:
                turretTile.rotationY = -90F;
                break;
            case UP:
                turretTile.rotationY = 90F;
                break;
            case NORTH:
                turretTile.rotationX = 270F;
                break;
            case SOUTH:
                turretTile.rotationX = 90F;
                break;
            case WEST:
                break;
            case EAST:
                turretTile.rotationX = 180F;
                break;
        }
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FocusEnhancedSpellTurretTile(pos, state).setSchool(school);
    }

    static class FocusTurretSpellResolver extends EntitySpellResolver {

        SpellSchool school;

        public FocusTurretSpellResolver(SpellContext context, SpellSchool tile) {
            super(context);
            this.school = tile;
        }

        @Override
        public boolean hasFocus(ItemStack stack) {
            return hasFocus(stack.getItem());
        }

        @Override
        public boolean hasFocus(Item item) {
            if (item instanceof ISchoolFocus focus) {
                return school == focus.getSchool();
            } else if (item == ItemsRegistry.SHAPERS_FOCUS.get()) {
                return school == SpellSchools.MANIPULATION;
            }
            return super.hasFocus(item);
        }


        @Override
        public SpellResolver getNewResolver(SpellContext context) {
            return new FocusTurretSpellResolver(context, school);
        }

    }

}
