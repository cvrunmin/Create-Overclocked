package io.github.cvrunmin.create_andertech.index;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AllTags {
    public static final TagKey<Item> CRIMSCHIST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "crimschist"));
    public static final TagKey<Item> CHRONENDITE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "chronendite"));
}
