package com.alexthw.sauce.mixin;

import com.alexthw.sauce.common.block.ArcanePlinthTile;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantingApparatusTile.class)
public class ApparatusPedestalPatch {

    @Redirect(
            method = "clearItems()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/common/block/tile/ArcanePedestalTile;setStack(Lnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void sauce$redirectSetStack(ArcanePedestalTile instance, ItemStack remainder) {
        // Prevents the enchanting apparatus from clearing the current itemstack if it is an instance of ArcanePlinthTile, allowing the rest of the items to remain on the pedestal.
        if (instance instanceof ArcanePlinthTile && instance.getStack().getCount() > 1) {
            instance.removeItem(0, 1);
            if (!remainder.isEmpty() && instance.getLevel() != null) {
                // drop the itemstack into the world if it is not empty
                instance.getLevel().addFreshEntity(new ItemEntity(instance.getLevel(), instance.getBlockPos().getX() + 0.5, instance.getBlockPos().getY() + 1.0, instance.getBlockPos().getZ() + 0.5, remainder));
            }
        } else {
            // If the instance is not an ArcanePlinthTile or the stack count is 1 or less, proceed with the original behavior
            instance.setStack(remainder);
        }
    }

}
