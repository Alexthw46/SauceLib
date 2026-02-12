package com.alexthw.sauce.mixin;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ImbuementTile.class)
public abstract class ImbuementChamberTileMixin extends AbstractSourceMachine {
    @Shadow
    @Nullable
    public abstract RecipeHolder<? extends IImbuementRecipe> getRecipeNow();

    @Shadow
    public ItemStack stack;

    @Shadow
    int craftTicks;

    public ImbuementChamberTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    public void sauce$getTooltip(List<Component> tooltip, CallbackInfo ci) {
        if (!ArsNouveauAPI.ENABLE_DEBUG_NUMBERS) return;
        tooltip.add(Component.translatable("sauce.tooltip.buffer", this.getSource(), this.getMaxSource()));
        if (craftTicks > 0) {
            tooltip.add(Component.translatable("Conversion starting in " + craftTicks / 20 + " seconds."));
            return;
        }
        var holder = getRecipeNow();
        var recipe = holder == null ? null : holder.value();
        if (recipe != null && !recipe.getResultItem(this.level.registryAccess()).isEmpty() && stack != null && !stack.isEmpty()) {
            int cost = recipe.getSourceCost((ImbuementTile) (Object) this);
            tooltip.add(Component.translatable("sauce.tooltip.required", cost));
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/hollingsworth/arsnouveau/common/block/tile/ImbuementTile;craftTicks:I",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void onCraftTicksChanged(CallbackInfo ci) {
        ImbuementTile self = (ImbuementTile) (Object) this;
        self.updateBlock();
    }

}
