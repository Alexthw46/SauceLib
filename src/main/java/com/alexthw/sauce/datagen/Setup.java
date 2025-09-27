package com.alexthw.sauce.datagen;

import com.alexthw.sauce.Sauce;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Sauce.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Setup {
    public static CompletableFuture<HolderLookup.Provider> provider;
    public static PackOutput output;

    //use runData configuration to generate stuff, event.includeServer() for data, event.includeClient() for assets
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        provider = event.getLookupProvider();
        output = gen.getPackOutput();

//        gen.addProvider(event.includeServer(), new ArsProviders.ImbuementProvider(gen));
//        gen.addProvider(event.includeServer(), new ArsProviders.GlyphProvider(gen));
//        gen.addProvider(event.includeServer(), new ArsProviders.EnchantingAppProvider(gen));
        gen.addProvider(event.includeClient(), new SauceLangProvider(output, Sauce.MODID, "en_us"));
        gen.addProvider(event.includeServer(), new SauceTagProviders.DamageType(output, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new SauceTagProviders.BannerTags(output, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new SauceBootstrapProviders(output, provider));
    }

}
