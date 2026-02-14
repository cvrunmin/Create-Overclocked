package io.github.cvrunmin.create_andertech.infrastructure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.cvrunmin.create_andertech.index.AllDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ItemTagIngredientWithCount implements ICustomIngredient {
    public static final MapCodec<ItemTagIngredientWithCount> CODEC = RecordCodecBuilder.mapCodec((builder) ->
            builder.group(TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(ItemTagIngredientWithCount::getTag),
                    ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemTagIngredientWithCount::getCount))
                    .apply(builder, ItemTagIngredientWithCount::new));
    private final TagKey<Item> tag;
    private final int count;

    protected ItemStack[] itemStacks;

    public ItemTagIngredientWithCount(TagKey<Item> tag, int count){
        this.tag = tag;
        this.count = count;
    }

    protected void resolveStacks(){
        if(itemStacks == null){
            List<ItemStack> list = new ArrayList<>();

            for(Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
                list.add(new ItemStack(holder, count));
            }

            if (list.isEmpty()) {
                ItemStack itemStack = new ItemStack(Blocks.BARRIER, count);
                itemStack.set(DataComponents.CUSTOM_NAME, Component.literal("Empty Tag: " + this.tag.location()));
                list.add(itemStack);
            }

            itemStacks = list.toArray(ItemStack[]::new);
        }
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if(itemStack == null) return false;
        resolveStacks();
        for (ItemStack stack : itemStacks) {
            if(stack.is(itemStack.getItem()) && itemStack.getCount() >= stack.getCount()) return true;
        }
        return false;
    }

    @Override
    public Stream<ItemStack> getItems() {
        resolveStacks();
        return Stream.of(itemStacks);
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    public TagKey<Item> getTag() {
        return tag;
    }

    public int getCount() {
        return count;
    }

    @Override
    public IngredientType<?> getType() {
        return AllDataComponents.ITEM_TAG_WITH_COUNT.get();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (!(o instanceof ItemTagIngredientWithCount that)) return false;
        return count == that.count && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, count);
    }
}
