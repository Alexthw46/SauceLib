package com.alexthw.sauce.util;

import com.alexthw.sauce.api.item.components.CharmData;
import net.minecraft.world.item.ItemStack;

public class CharmUtil {
    public static boolean isEnabled(ItemStack charm) {
        return getCharges(charm) > 0;
    }

    public static int getCharges(ItemStack stack) {
        return CharmData.getOrDefault(stack).charges();
    }

    public static void useCharges(ItemStack stack, int charges) {
        CharmData.getOrDefault(stack).use(charges).write(stack);
    }

    public static void setCharges(ItemStack stack, int charges) {
        CharmData.getOrDefault(stack).set(charges).write(stack);
    }

}
