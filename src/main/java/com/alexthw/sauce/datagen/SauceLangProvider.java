package com.alexthw.sauce.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class SauceLangProvider extends LanguageProvider {

    public SauceLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("item.sauce.source_fluid_bucket", "Liquefied Source Bucket");
        add("fluid_type.sauce.source_fluid", "Liquefied Source");
        add("block.sauce.source_fluid_block", "Liquified Source");
    }
}
