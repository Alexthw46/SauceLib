package com.alexthw.sauce.datagen;

import com.alexthw.sauce.Sauce;
import com.hollingsworth.arsnouveau.setup.registry.BannerRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.alexthw.sauce.Sauce.prefix;

public class SauceBootstrapProviders extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
//            .add(Registries.CONFIGURED_FEATURE, SauceWorldgen::bootstrapConfiguredFeatures)
//            .add(Registries.PLACED_FEATURE, SauceWorldgen::bootstrapPlacedFeatures)
//            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, SauceWorldgen::generateBiomeModifiers)
//            .add(Registries.BIOME, SauceWorldgen::registerBiomes)
            .add(Registries.BANNER_PATTERN, SauceBootstrapProviders::bootstrapPatterns);

    public SauceBootstrapProviders(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Sauce.MODID));
    }

    public static void bootstrapPatterns(BootstrapContext<BannerPattern> bannerPatternBootstrapContext) {
        BannerRegistry.register(bannerPatternBootstrapContext, ResourceKey.create(Registries.BANNER_PATTERN, prefix("anima")));
    }

}
