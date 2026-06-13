package com.alexthw.sauce.mixin;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ConcurrentHashMap;

import static com.alexthw.sauce.ArsNouveauRegistry.aliases;

@Mixin(GlyphRegistry.class)
public class AliasSpellMap {

    @Shadow
    @Final
    private static ConcurrentHashMap<ResourceLocation, AbstractSpellPart> spellpartMap;

    @Inject(method = "getSpellPart", at = @At("TAIL"), cancellable = true)
    private static void getSpellPart(ResourceLocation id, CallbackInfoReturnable<AbstractSpellPart> cir) {
        // If the RL was not found, try to use the alias instead
        if (cir.getReturnValue() == null && aliases.containsKey(id))
            cir.setReturnValue(spellpartMap.get(aliases.get(id)));
    }
}
