package com.alexthw.sauce.datagen;

import com.alexthw.sauce.Sauce;
import com.hollingsworth.arsnouveau.common.block.StrippableLog;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;
import java.util.function.Predicate;

import static com.alexthw.sauce.Sauce.prefix;
import static com.alexthw.sauce.registry.ModRegistry.ITEMS;

public class SauceModelProvider {

    public static class ItemModels extends ItemModelProvider {


        private static final ResourceLocation GENERATED = ResourceLocation.withDefaultNamespace("item/generated");

        private static final ResourceLocation HANDHELD = ResourceLocation.withDefaultNamespace("item/handheld");
        private static final ResourceLocation SPAWN_EGG = ResourceLocation.withDefaultNamespace("item/template_spawn_egg");

        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator.getPackOutput(), Sauce.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            Set<DeferredHolder<Item, ? extends Item>> items = new HashSet<>(ITEMS.getEntries());
            takeAll(items, i -> i.get() instanceof BlockItem).forEach(this::blockItem);

        }

        private void blockItem(DeferredHolder<Item, ? extends Item> i) {
            String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
            String root = "block/";
            if (i.get() instanceof BlockItem bi && (bi.getBlock() instanceof RotatedPillarBlock || bi.getBlock() instanceof LeavesBlock || bi.getBlock() instanceof StrippableLog))
                root = "block/archwood/";
            getBuilder(name).parent(new ModelFile.UncheckedModelFile(prefix(root + name)));
        }

        private void blockGeneratedItem(DeferredHolder<Item, ? extends Item> i) {
            String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
            withExistingParent(name, GENERATED).texture("layer0", prefix("block/" + name));
        }
    }

    public static <T> Collection<T> takeAll(Collection<T> src, Predicate<T> predicate) {
        List<T> ret = new ArrayList<>();

        Iterator<T> iter = src.iterator();
        while (iter.hasNext()) {
            T item = iter.next();
            if (predicate.test(item)) {
                iter.remove();
                ret.add(item);
            }
        }

        if (ret.isEmpty()) {
            return Collections.emptyList();
        }
        return ret;
    }
}