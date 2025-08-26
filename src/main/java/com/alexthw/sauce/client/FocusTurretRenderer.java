package com.alexthw.sauce.client;

import com.alexthw.sauce.common.block.FocusEnhancedSpellTurretTile;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FocusTurretRenderer extends GeoBlockRenderer<FocusEnhancedSpellTurretTile> {

    public static GeoModel<FocusEnhancedSpellTurretTile> modelFire = new TurretModel<>("fire");
    public static GeoModel<FocusEnhancedSpellTurretTile> modelWater = new TurretModel<>("water");
    public static GeoModel<FocusEnhancedSpellTurretTile> modelAir = new TurretModel<>("air");
    public static GeoModel<FocusEnhancedSpellTurretTile> modelEarth = new TurretModel<>("earth");
    public static GeoModel<FocusEnhancedSpellTurretTile> modelShaper = new TurretModel<>("manipulation");

    public FocusTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(modelShaper);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, FocusEnhancedSpellTurretTile tile, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        super.actuallyRender(poseStack, tile, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        float rotationX = tile.rotationX;
        float neededRotationX = tile.clientNeededX;
        float rotationY = tile.rotationY;
        float neededRotationY = tile.clientNeededY;
        float step = (0.1f + partialTick);
        if (rotationX != neededRotationX) {
            float diff = neededRotationX - rotationX;
            if (Math.abs(diff) < step) {
                tile.setRotationX(neededRotationX);
            } else {
                tile.setRotationX(rotationX + diff * step);
            }
        }
        if (rotationY != neededRotationY) {
            float diff = neededRotationY - rotationY;
            if (Math.abs(diff) < step) {
                tile.setRotationY(neededRotationY);
            } else {
                tile.setRotationY(rotationY + diff * step);
            }
        }
    }

    //Disable geckolib automatic rotation based on blockstate
    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
    }

    public static GenericItemBlockRenderer getISTER(String element) {
        GeoModel<?> model = switch (element) {
            case "fire" -> modelFire;
            case "water" -> modelWater;
            case "air" -> modelAir;
            case "earth" -> modelEarth;
            default -> modelShaper;
        };
        return new GenericItemBlockRenderer(model);
    }

    @Override
    public ResourceLocation getTextureLocation(FocusEnhancedSpellTurretTile instance) {
        return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "textures/block/" + instance.getSchool().getId() + "_turret.png");
    }

    public static class TurretModel<T extends FocusEnhancedSpellTurretTile> extends GeoModel<T> {

        final String element;

        public TurretModel(String element) {
            this.element = element;
        }

        @Override
        public ResourceLocation getModelResource(T t) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "geo/basic_spell_turret.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(T t) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "textures/block/" + element + "_turret.png");
        }

        @Override
        public ResourceLocation getAnimationResource(T t) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "animations/basic_spell_turret_animations.json");
        }
    }

}