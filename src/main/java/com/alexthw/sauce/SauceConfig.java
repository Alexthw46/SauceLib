package com.alexthw.sauce;


import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber()
public class SauceConfig {
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;
    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;
    public static final Startup STARTUP;
    public static final ModConfigSpec STARTUP_SPEC;

    static {

        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        final Pair<Server, ModConfigSpec> specClientPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specClientPair.getRight();
        SERVER = specClientPair.getLeft();

        final Pair<Startup, ModConfigSpec> specStartupPair = new ModConfigSpec.Builder().configure(Startup::new);
        STARTUP_SPEC = specStartupPair.getRight();
        STARTUP = specStartupPair.getLeft();
    }

    public static class Common {

        public Common(ModConfigSpec.Builder builder) {

        }
    }

    public static class Server {
        public static ModConfigSpec.BooleanValue ENABLE_SPELL_CRIT;

        public Server(ModConfigSpec.Builder builder) {
            builder.comment("Enable Spell Critical Hits").push("spell_crit");
            ENABLE_SPELL_CRIT = builder
                    .comment("Enable Spell Critical Hits, if another mod doesn't enable it already.")
                    .define("enable_spell_crit", true);
            builder.pop();
        }

    }

    public static class Startup {

        public static ModConfigSpec.BooleanValue SHOW_SOURCE_FLUID;
        public static ModConfigSpec.BooleanValue SHOW_DEBUG_NUMBERS;

        public Startup(ModConfigSpec.Builder builder) {
            builder.comment("Source Fluid").push("source_fluid");
            SHOW_SOURCE_FLUID = builder
                    .comment("Show the liquid source bucket and fluid in JEI and Creative Tabs, valid only if another mod doesn't enable it already.")
                    .define("show_source_fluid", false);
            builder.pop();
            builder.comment("Debug Numbers").push("debug_numbers");
            SHOW_DEBUG_NUMBERS = builder
                    .comment("Show numeric values overlays for source and mana.")
                    .define("show_debug_numbers", false);
            builder.pop();
        }

    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        Sauce.ENABLE_SPELL_CRIT = Sauce.ENABLE_SPELL_CRIT || SauceConfig.Server.ENABLE_SPELL_CRIT.get();
        Sauce.SHOW_LIQUID_SOURCE = Sauce.SHOW_LIQUID_SOURCE || SauceConfig.Startup.SHOW_SOURCE_FLUID.get();
        ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = ArsNouveauAPI.ENABLE_DEBUG_NUMBERS || SauceConfig.Startup.SHOW_DEBUG_NUMBERS.get();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        Sauce.ENABLE_SPELL_CRIT = Sauce.ENABLE_SPELL_CRIT || SauceConfig.Server.ENABLE_SPELL_CRIT.get();
        Sauce.SHOW_LIQUID_SOURCE = Sauce.SHOW_LIQUID_SOURCE || SauceConfig.Startup.SHOW_SOURCE_FLUID.get();
        ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = ArsNouveauAPI.ENABLE_DEBUG_NUMBERS || SauceConfig.Startup.SHOW_DEBUG_NUMBERS.get();
    }

}
