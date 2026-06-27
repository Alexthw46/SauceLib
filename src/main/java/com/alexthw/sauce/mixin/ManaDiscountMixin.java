package com.alexthw.sauce.mixin;

import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.alexthw.sauce.event.AttributeEventHandler.schoolToDiscountAttribute;

@Mixin(ManaUtil.class)
public class ManaDiscountMixin {

    @Inject(method = "getPlayerDiscounts", at = @At("TAIL"), cancellable = true)
    private static void getPlayerDiscounts(LivingEntity e, Spell spell, ItemStack casterStack, CallbackInfoReturnable<Integer> cir) {
        if (e instanceof Player player && !(player instanceof FakePlayer)) {
            int acc = 0;
            AttributeInstance attribute = player.getAttribute(ModRegistry.MANA_DISCOUNT);
            if (attribute != null) acc += (int) attribute.getValue();
            for (var glyph : spell.recipe()) {
                double percent = 0;
                for (var school : glyph.spellSchools) {
                    Holder<Attribute> discountAttribute = schoolToDiscountAttribute.get(school);
                    if (discountAttribute != null) {
                        AttributeInstance discountInstance = player.getAttribute(discountAttribute);
                        if (discountInstance != null) {
                            percent += discountInstance.getValue();
                        }
                    }
                }
                if (percent != 0) acc += Mth.ceil(glyph.getCastingCost() * Math.min(percent, 1.0));
            }
            cir.setReturnValue(cir.getReturnValue() + acc);
        }
    }

}
