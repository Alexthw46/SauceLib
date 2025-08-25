package com.alexthw.sauce.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

public record ConfigCondition(String configPath) implements ICondition {
    public static final MapCodec<ConfigCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(ConfigCondition::configPath)
    ).apply(instance, ConfigCondition::new));

    @Override
    public boolean test(IContext context) {
        // For now, always return true. This can be enhanced later to actually check config values
        return true;
    }

    @Override
    public MapCodec<ConfigCondition> codec() {
        return CODEC;
    }
}
