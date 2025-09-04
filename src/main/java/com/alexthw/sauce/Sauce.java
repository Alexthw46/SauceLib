package com.alexthw.sauce;

import com.alexthw.sauce.common.fluid.SourceFluid;
import com.alexthw.sauce.event.AttributeEventHandler;
import com.alexthw.sauce.event.DamageEventHandler;
import com.alexthw.sauce.event.GenericEventHandler;
import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static com.alexthw.sauce.registry.ModRegistry.SOURCE_FLUID_TYPE;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Sauce.MODID)
public class Sauce {
    public static final String MODID = "sauce";

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean ENABLE_LIQUID_SOURCE = !FMLEnvironment.production;
    public static boolean ENABLE_ENTHRALL = !FMLEnvironment.production;
    public static boolean ENABLE_SPELL_CRIT = !FMLEnvironment.production;

    public Sauce(IEventBus modEventBus, ModContainer modContainer) {
        ModRegistry.registerRegistries(modEventBus);
        ArsNouveauRegistry.init();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::registerClientExtensions);
        modContainer.registerConfig(ModConfig.Type.SERVER, SauceConfig.SERVER_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, SauceConfig.COMMON_SPEC);
        NeoForge.EVENT_BUS.register(DamageEventHandler.class);
        NeoForge.EVENT_BUS.register(AttributeEventHandler.class);
        NeoForge.EVENT_BUS.register(GenericEventHandler.class);
        if (FMLEnvironment.dist.isClient() && ENABLE_LIQUID_SOURCE) {
            new SourceFluid.FluidTypeSourceClient(modEventBus);
        }
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    public void registerClientExtensions(RegisterClientExtensionsEvent event) {
        if (!ENABLE_LIQUID_SOURCE) return;
        event.registerFluidType(SourceFluid.extension, ModRegistry.SOURCE_FLUID_TYPE);
    }

    private void setup(final FMLCommonSetupEvent ignoredEvent) {
        ArsNouveauRegistry.postInit();
        if (!ENABLE_LIQUID_SOURCE) return;
        try {
            FluidInteractionRegistry.addInteraction(SOURCE_FLUID_TYPE.get(),
                    new FluidInteractionRegistry.InteractionInformation(
                            (level, currentPos, relativePos, currentState) ->
                                    level.getFluidState(relativePos).getFluidType() == NeoForgeMod.LAVA_TYPE.value() && level.getBlockState(currentPos.below()).is(Blocks.BLUE_ICE),
                            Objects.requireNonNull(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, LibBlockNames.SMOOTH_SOURCESTONE))).defaultBlockState()));
            FluidInteractionRegistry.addInteraction(SOURCE_FLUID_TYPE.get(),
                    new FluidInteractionRegistry.InteractionInformation(
                            (level, currentPos, relativePos, currentState) ->
                                    level.getFluidState(relativePos).getFluidType() == NeoForgeMod.LAVA_TYPE.value(),
                            Objects.requireNonNull(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, LibBlockNames.SOURCESTONE))).defaultBlockState()));
        } catch (NullPointerException npe) {
            System.out.println("Sourcestone not found, skipping interaction.");
        }
    }

}
