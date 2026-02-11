package com.alexthw.sauce.util;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;

import static com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry.getEntry;

public class DocsUtil {

    public static DocEntry getBaseEntryGlyph(AbstractSpellPart instance) {
        return getEntry(instance.getRegistryName());
    }

    public static DocEntry getBaseEntryItem(ItemRegistryWrapper<?> delegate) {
        return getBaseEntry(delegate.get().getDescriptionId());
    }

    public static DocEntry getBaseEntryBlock(BlockRegistryWrapper<?> delegate) {
        return getBaseEntry(delegate.get().getDescriptionId());
    }

    public static DocEntry getBaseEntry(String entry) {
        return getEntry(ArsNouveau.prefix(entry));
    }

}
