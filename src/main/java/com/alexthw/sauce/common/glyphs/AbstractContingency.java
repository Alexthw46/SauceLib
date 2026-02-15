package com.alexthw.sauce.common.glyphs;

import com.alexthw.sauce.util.ContingencyEffectInstance;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public abstract class AbstractContingency extends AbstractEffect implements IPotionEffect {

    public AbstractContingency(String effectName, String description) {
        super(effectName, description);
    }

    public AbstractContingency(ResourceLocation effectName, String description) {
        super(effectName, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity livingEntity) {
            // create delayed resolver
            SpellContext newContext = spellContext.makeChildContext();
            SpellResolver newResolver = resolver.getNewResolver(newContext);
            spellContext.setCanceled(true);
            applyContingency(spellStats, livingEntity, newResolver);
        }
    }

    public void applyContingency(SpellStats spellStats, LivingEntity livingEntity, SpellResolver newResolver) {
        int ticks = getBaseDuration() * 20 + getExtendTimeDuration() * spellStats.getDurationInTicks();
        livingEntity.addEffect(new ContingencyEffectInstance(newResolver, getTrigger(), ticks, spellStats.getAmpMultiplier(), spellStats.getBuffCount(AugmentSplit.INSTANCE)));
    }

    @Override
    public Integer getTypeIndex() {
        return 9;
    }

    public abstract ContingencyEffectInstance.ContingencyTrigger getTrigger();

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    protected int getDefaultManaCost() {
        return 100;
    }

    @Override
    protected void addAugmentCostOverrides(Map<ResourceLocation, Integer> defaults) {
        super.addAugmentCostOverrides(defaults);
        defaults.put(AugmentSplit.INSTANCE.getRegistryName(), 100);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentSplit.INSTANCE);
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return Set.of(SpellSchools.ABJURATION);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 100 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 100 : EXTEND_TIME.get();
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentSplit.INSTANCE, "Increases the number of possible activations before the contingency ends by one.");
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 100);
        addExtendTimeConfig(builder, 50);
    }

}
