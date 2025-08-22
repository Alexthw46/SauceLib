package com.alexthw.sauce.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TooltipUtils {

    public static String shiftInfoKey(String namespace, String path) {
        return namespace + '.' + path + ".shift_info";
    }

    public static Component getShiftInfoTooltip(String translationKey) {
        Component shift = Component.literal("SHIFT").withStyle(ChatFormatting.AQUA);
        return Component.translatable(translationKey, shift).withStyle(ChatFormatting.GRAY);
    }

    public static void addOnShift(List<Component> tooltip, Runnable lambda, String translationKey) {
        if (Screen.hasShiftDown()) {
            lambda.run();
        } else {
            tooltip.add(getShiftInfoTooltip(translationKey));
        }
    }

}