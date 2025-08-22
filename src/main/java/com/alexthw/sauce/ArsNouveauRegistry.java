package com.alexthw.sauce;

import com.alexthw.sauce.api.spell_style.GravityWellMotion;
import com.alexthw.sauce.common.glyphs.TestEffect;
import com.alexthw.sauce.mixin.SpellSchoolAccessor;
import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.SimpleParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.timelines.LingerTimeline;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry.PARTICLE_CONFIG;

public class ArsNouveauRegistry {

    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>(); //this will come handy for datagen
    public static final DocAssets.BlitInfo ANIMA_ICON = new DocAssets.BlitInfo(ArsNouveau.prefix("textures/gui/documentation/doc_icon_anima.png"), 10, 10);

    public static void registerGlyphs(){
        register(TestEffect.INSTANCE);
    }
    public static void registerSounds(){
        SpellSoundRegistry.registerSpellSound(ModRegistry.EXAMPLE_SPELL_SOUND);
    }
    public static void register(AbstractSpellPart spellPart){
        GlyphRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

    public static final DeferredHolder<IParticleMotionType<?>, IParticleMotionType<GravityWellMotion>> GRAVITY_FIELD_TYPE = PARTICLE_CONFIG.register("gravity_field", () -> new SimpleParticleMotionType<>(GravityWellMotion.CODEC, GravityWellMotion.STREAM, GravityWellMotion::new));

    public static void postInit() {
        registerSounds();
        ((SpellSchoolAccessor) SpellSchools.NECROMANCY).setDocIcon(ANIMA_ICON);

        LingerTimeline.TRAIL_OPTIONS.add(GRAVITY_FIELD_TYPE.get());

    }
}
