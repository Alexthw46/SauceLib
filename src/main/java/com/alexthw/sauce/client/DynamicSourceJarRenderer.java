package com.alexthw.sauce.client;

import com.alexthw.sauce.common.block.DynamicSourceJarTile;
import com.alexthw.sauce.registry.ModRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.alexthw.sauce.util.RenderUtil.addCube;

public class DynamicSourceJarRenderer implements BlockEntityRenderer<DynamicSourceJarTile> {

    public static final Vector3f LIQUID_DIMENSIONS = new Vector3f(11 / 16f, 9.5f / 16f, 1 / 16f);
    private static final BlockState blockState = BlockRegistry.SOURCE_JAR.defaultBlockState();

    public DynamicSourceJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(DynamicSourceJarTile blockEntity) {
        if (blockEntity.getIsFormed()) {
            return new AABB(blockEntity.getMaxPos().getCenter(), blockEntity.getMinPos().getCenter());
        }
        // When not formed, only render the block itself (so it can be seen during construction).
        return new AABB(blockEntity.getBlockPos());
    }

    public static void renderFluid(float percentageFill, int color, int luminosity, ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int light, boolean shading, Vector3f fluidVec) {
        matrixStackIn.pushPose();
        float opacity = 1;
        if (luminosity != 0) light = light & 15728640 | luminosity << 4;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        VertexConsumer builder = bufferIn.getBuffer(RenderType.translucentMovingBlock());
        matrixStackIn.translate(0.5, fluidVec.z(), 0.5);
        addCube(builder, matrixStackIn,
                fluidVec.x(),
                percentageFill * fluidVec.y(),
                sprite, light, color, opacity, true, true, shading, true);
        matrixStackIn.popPose();
    }

    @Override
    public void render(@NotNull DynamicSourceJarTile tile, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        // Translate to the corner of the multiblock
        if (!tile.getIsFormed()) return;
        pPoseStack.pushPose();

        BlockPos controllerPos = tile.getBlockPos();
        BlockPos min = tile.getMinPos();
        BlockPos max = tile.getMaxPos();

        // 1. Calculate dimensions (including the blocks themselves)
        float width = (max.getX() - min.getX() + 1);
        float height = (max.getY() - min.getY() + 0.5F);
        float depth = (max.getZ() - min.getZ() + 1);

        // 2. Translate to the min corner relative to controller
        pPoseStack.translate(min.getX() - controllerPos.getX(),
                min.getY() - controllerPos.getY(),
                min.getZ() - controllerPos.getZ());


        // 3. Render the empty source jar scaled to the multiblock size
        pPoseStack.pushPose();
        // 3.5 Center-point inflation
        // We move by half the total size, scale it, then move it back.
        float inflation = 1.2f;
        pPoseStack.translate(width / 2f, 0, depth / 2f);
        pPoseStack.scale(width * inflation, height, depth * inflation);
        pPoseStack.translate(-0.5f, 0, -0.5f);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();

        // 4. render fluid to show the fill level
        pPoseStack.translate(0, 0.25, 0); // Move up to the base of the jar opening
        pPoseStack.scale(width, height + 0.25f, depth);
        FluidStack fluidHolder = new FluidStack(ModRegistry.SOURCE_FLUID.get(), 1000);
        if (tile.getSource() > 0) {
            renderFluid((float) tile.getSource() / tile.getMaxSource(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getTintColor(fluidHolder),
                    fluidHolder.getFluid().getFluidType().getLightLevel(), IClientFluidTypeExtensions.of(fluidHolder.getFluid()).getStillTexture(),
                    pPoseStack, pBufferSource, pPackedLight, true, LIQUID_DIMENSIONS);
        }

        pPoseStack.popPose();
    }

}
