package com.alexthw.sauce.datagen;

import com.alexthw.sauce.ArsNouveauRegistry;
import com.alexthw.sauce.Sauce;
import com.alexthw.sauce.registry.SauceTags;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.alexthw.sauce.Sauce.MODID;
import static com.hollingsworth.arsnouveau.common.datagen.BannerTagsProvider.bannerTag;

public class SauceTagProviders {

    public static class DamageType extends DamageTypeTagsProvider {

        public DamageType(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(SauceTags.FIRE_DAMAGE).add(
                            DamageTypes.DRAGON_BREATH,
                            DamageTypes.EXPLOSION,
                            DamageTypes.PLAYER_EXPLOSION,
                            DamageTypes.FIREWORKS)
                    .addTag(DamageTypeTags.IS_FIRE);

            tag(SauceTags.WATER_DAMAGE).add(
                            DamageTypes.TRIDENT,
                            DamageTypes.MAGIC)
                    .addTag(DamageTypeTags.IS_FREEZING)
                    .addTag(DamageTypeTags.IS_DROWNING);

            tag(SauceTags.EARTH_DAMAGE).add(DamageTypes.FALLING_BLOCK,
                            DamageTypes.FALLING_STALACTITE,
                            DamageTypes.STALAGMITE,
                            DamageTypes.CACTUS,
                            DamageTypes.FALLING_ANVIL,
                            DamageTypes.STING,
                            DamageTypes.SWEET_BERRY_BUSH)
                    .addTag(Tags.DamageTypes.IS_POISON)
                    .addOptional(DamageTypesRegistry.CRUSH.location())
                    .addOptional(DamageTypesRegistry.SOURCE_BERRY_BUSH.location());

            tag(SauceTags.AIR_DAMAGE).add(DamageTypes.FALL,
                            DamageTypes.FLY_INTO_WALL,
                            DamageTypes.SONIC_BOOM)
                    .addTag(DamageTypeTags.IS_LIGHTNING)
                    .addOptional(DamageTypesRegistry.WINDSHEAR.location());
        }
    }


    public static class BannerTags extends BannerPatternTagsProvider {
        public BannerTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, Sauce.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(bannerTag).addOptional(ArsNouveauRegistry.ANIMA_ICON.location());
        }
    }

}
