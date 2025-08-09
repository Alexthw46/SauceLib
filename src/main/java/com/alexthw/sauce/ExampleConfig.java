package com.alexthw.sauce;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ExampleConfig {
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;
    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static {

        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

        final Pair<Server, ModConfigSpec> specClientPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specClientPair.getRight();
        SERVER = specClientPair.getLeft();

    }

    public static class Common {

        public Common(ModConfigSpec.Builder builder) {

        }
    }

    public static class Server {

        public Server(ModConfigSpec.Builder builder) {
        }

    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
    }
}
