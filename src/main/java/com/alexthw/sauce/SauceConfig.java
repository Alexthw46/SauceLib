package com.alexthw.sauce;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
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

        public Startup(ModConfigSpec.Builder builder) {

        }

    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        Sauce.ENABLE_SPELL_CRIT = Sauce.ENABLE_SPELL_CRIT || SauceConfig.Server.ENABLE_SPELL_CRIT.get();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        Sauce.ENABLE_SPELL_CRIT = Sauce.ENABLE_SPELL_CRIT || SauceConfig.Server.ENABLE_SPELL_CRIT.get();
    }

}
