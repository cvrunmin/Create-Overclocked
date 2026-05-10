package io.github.cvrunmin.create_andertech.index;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AllTags {
    public static final TagKey<Block> UNTOPPED_NYLIUM = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "untopped_nylium"));
    public static final TagKey<Item> CRIMSCHIST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "crimschist"));
    public static final TagKey<Item> CHRONENDITE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "chronendite"));

    public static final TagKey<Block> EXPLOSIVE_TO_TRANSMUTER = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "explosive_to_transmuter"));

    static {

    }

}
