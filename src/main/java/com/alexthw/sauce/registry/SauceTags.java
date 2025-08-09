package com.alexthw.sauce.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static com.alexthw.sauce.Sauce.prefix;

public class SauceTags {

    public static TagKey<DamageType> FIRE_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("fire_damage"));
    public static TagKey<DamageType> WATER_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("water_damage"));
    public static TagKey<DamageType> EARTH_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("earth_damage"));
    public static TagKey<DamageType> AIR_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("air_damage"));


}
