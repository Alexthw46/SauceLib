package com.alexthw.sauce.common.fluid;

import com.alexthw.sauce.Sauce;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.alexthw.sauce.registry.ModRegistry.SOURCE_FLUID;
import static com.alexthw.sauce.registry.ModRegistry.SOURCE_FLUID_BLOCK;
import static com.alexthw.sauce.registry.ModRegistry.SOURCE_FLUID_FLOWING;

public class SourceFluid extends FluidType {
    /**
     * Default constructor.
     */
    public SourceFluid() {
        super(FluidType.Properties.create().supportsBoating(true).canHydrate(true).density(1).temperature(1).viscosity(1));
    }

    public static IClientFluidTypeExtensions extension = (new IClientFluidTypeExtensions() {
        private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath(Sauce.MODID, "block/source_still"),
                FLOW = ResourceLocation.fromNamespaceAndPath(Sauce.MODID, "block/source_flow"),
                VIEW_OVERLAY = ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "textures/block/sourcestone.png");

        @Override
        public @NotNull ResourceLocation getStillTexture() {
            return STILL;
        }

        @Override
        public @NotNull ResourceLocation getFlowingTexture() {
            return FLOW;
        }

        @Override
        public ResourceLocation getRenderOverlayTexture(@NotNull Minecraft mc) {
            return VIEW_OVERLAY;
        }

        @Override
        public int getTintColor() {
            return 0xFFB55AE8;
        }

        @Override
        public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level, int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor) {
            int color = this.getTintColor();
            return new Vector3f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F);
        }

        @Override
        public void modifyFogRender(@NotNull Camera camera, FogRenderer.@NotNull FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, @NotNull FogShape shape) {
            nearDistance = -8F;
            farDistance = 24F;

            if (farDistance > renderDistance) {
                farDistance = renderDistance;
                shape = FogShape.CYLINDER;
            }

            RenderSystem.setShaderFogStart(nearDistance);
            RenderSystem.setShaderFogEnd(farDistance);
            RenderSystem.setShaderFogShape(shape);
        }
    });


    public static class FluidTypeSourceClient {
        public FluidTypeSourceClient(IEventBus modEventBus) {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerBlockColors);
        }

        public void clientSetup(FMLClientSetupEvent ignoredEvent) {
            ItemBlockRenderTypes.setRenderLayer(SOURCE_FLUID.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(SOURCE_FLUID_FLOWING.get(), RenderType.translucent());
        }

        private void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((state, getter, pos, index) ->
            {
                if (getter != null && pos != null) {
                    FluidState fluidState = getter.getFluidState(pos);
                    return IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, getter, pos);
                } else return 0xAF7FFFD4;
            }, SOURCE_FLUID_BLOCK.get());
        }
    }
}
