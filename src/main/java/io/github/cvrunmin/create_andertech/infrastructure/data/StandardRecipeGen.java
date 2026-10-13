package io.github.cvrunmin.create_andertech.infrastructure.data;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class StandardRecipeGen extends RecipeProvider {
    public StandardRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    protected static void nineBlockStorageRecipes(
            RecipeOutput recipeOutput,
            RecipeCategory unpackedCategory,
            ItemLike unpacked,
            RecipeCategory packedCategory,
            ItemLike packed,
            ResourceLocation packedName,
            @Nullable String packedGroup,
            ResourceLocation unpackedName,
            @Nullable String unpackedGroup
    ) {
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9)
                .requires(packed)
                .group(unpackedGroup)
                .unlockedBy(getHasName(packed), has(packed))
                .save(recipeOutput, unpackedName);
        ShapedRecipeBuilder.shaped(packedCategory, packed)
                .define('#', unpacked)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(packedGroup)
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(recipeOutput, packedName);
    }

    protected static void stonecutterResultFromBase(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount)
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, getConversionRecipeName(result, material) + "_stonecutting"));
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        nineBlockStorageRecipes(recipeOutput,
                RecipeCategory.MISC, AllItems.CHRONENDITE_ORB,
                RecipeCategory.BUILDING_BLOCKS, AllBlocks.CHRONENDITE_BLOCK,
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendite_block"), null,
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendite_orb_from_block"), "chronendite_orb"
                );
        nineBlockStorageRecipes(recipeOutput,
                RecipeCategory.MISC, AllItems.CHRONENDITE_SHARD,
                RecipeCategory.MISC, AllItems.CHRONENDITE_ORB,
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendite_orb_from_shards"), "chronendite_orb",
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendite_shard"), null
                );
        nineBlockStorageRecipes(recipeOutput,
                RecipeCategory.MISC, AllItems.CRIMSCHIST_ALLOY,
                RecipeCategory.BUILDING_BLOCKS, AllBlocks.CRIMSCHIST_ALLOY_BLOCK,
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_alloy_block"), null,
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_alloy_from_block"), "crimschist_alloy"
                );
        nineBlockStorageRecipes(recipeOutput,
                RecipeCategory.MISC, AllItems.CRIMSCHIST_NUGGET,
                RecipeCategory.MISC, AllItems.CRIMSCHIST_ALLOY,
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_from_nuggets"), "crimschist_alloy",
                ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_nugget"), null
                );
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AllBlocks.WARPED_NYLIUM_TRANSMUTER, 4)
                .define('#', Ingredient.of(Blocks.SAND, Blocks.RED_SAND))
                .define('X', Items.WARPED_ROOTS)
                .define('Y', AllItems.ENDOPYROTIC_CINDER_PASTE)
                .pattern("X#X")
                .pattern("#Y#")
                .pattern("X#X")
                .unlockedBy(getHasName(AllItems.ENDOPYROTIC_CINDER_PASTE), has(AllItems.ENDOPYROTIC_CINDER_PASTE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, AllBlocks.CRIMSON_NYLIUM_TRANSMUTER, 4)
                .define('#', Ingredient.of(Blocks.SAND, Blocks.RED_SAND))
                .define('X', Items.CRIMSON_ROOTS)
                .define('Y', AllItems.ENDOPYROTIC_CINDER_PASTE)
                .pattern("X#X")
                .pattern("#Y#")
                .pattern("X#X")
                .unlockedBy(getHasName(AllItems.ENDOPYROTIC_CINDER_PASTE), has(AllItems.ENDOPYROTIC_CINDER_PASTE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_SHAFT, 8)
                .define('#', AllItems.CRIMSCHIST_ALLOY)
                .pattern("#")
                .pattern("#")
                .unlockedBy(getHasName(AllItems.CRIMSCHIST_ALLOY), has(AllItems.CRIMSCHIST_ALLOY))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_COGWHEEL)
                .requires(AllBlocks.CRIMSCHIST_SHAFT)
                .requires(ItemTags.PLANKS)
                .unlockedBy(getHasName(AllBlocks.CRIMSCHIST_SHAFT), has(AllBlocks.CRIMSCHIST_SHAFT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_LARGE_COGWHEEL)
                .requires(AllBlocks.CRIMSCHIST_SHAFT)
                .requires(ItemTags.PLANKS)
                .requires(ItemTags.PLANKS)
                .unlockedBy(getHasName(AllBlocks.CRIMSCHIST_SHAFT), has(AllBlocks.CRIMSCHIST_SHAFT))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_LARGE_COGWHEEL)
                .requires(AllBlocks.CRIMSCHIST_COGWHEEL)
                .requires(ItemTags.PLANKS)
                .unlockedBy(getHasName(AllBlocks.CRIMSCHIST_SHAFT), has(AllBlocks.CRIMSCHIST_SHAFT))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_large_cogwheel_from_cogwheel"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AllBlocks.CHRONENDITE_HV_ENCASED_FAN)
                .requires(AllBlocks.CHRONENDITE_ENCASED_FAN)
                .unlockedBy(getHasName(AllBlocks.CHRONENDITE_ENCASED_FAN), has(AllBlocks.CHRONENDITE_ENCASED_FAN))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendite_hv_encased_fan"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AllBlocks.CHRONENDITE_ENCASED_FAN)
                .requires(AllBlocks.CHRONENDITE_HV_ENCASED_FAN)
                .unlockedBy(getHasName(AllBlocks.CHRONENDITE_ENCASED_FAN), has(AllBlocks.CHRONENDITE_ENCASED_FAN))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendite_hv_encased_fan_reverse"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_FLUID_TANK)
                .define('A', AllItems.CRIMSCHIST_SHEET)
                .define('B', AllBlocks.INFUSION_VAT)
                .pattern("A")
                .pattern("B")
                .unlockedBy(getHasName(AllBlocks.INFUSION_VAT), has(AllBlocks.INFUSION_VAT))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_fluid_tank_by_add_top"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_FLUID_TANK)
                .define('A', AllItems.CRIMSCHIST_SHEET)
                .define('B', Blocks.BARREL)
                .pattern("A")
                .pattern("B")
                .pattern("A")
                .unlockedBy(getHasName(AllItems.CRIMSCHIST_SHEET), has(AllItems.CRIMSCHIST_SHEET))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_fluid_tank"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.CHRONENDITE_MECHANICAL_SPOUT)
                .define('A', AllBlocks.CRIMSCHIST_COGWHEEL)
                .define('B', AllBlocks.CHRONENDITE_CASING)
                .define('C', com.simibubi.create.AllBlocks.SPOUT)
                .pattern("A")
                .pattern("B")
                .pattern("C")
                .unlockedBy(getHasName(AllBlocks.CHRONENDITE_CASING), has(AllBlocks.CHRONENDITE_CASING))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendite_mechanical_spout"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_HARVESTER)
                .define('A', AllBlocks.CRIMSCHIST_CASING)
                .define('B', com.simibubi.create.AllItems.IRON_SHEET)
                .define('C', com.simibubi.create.AllItems.ANDESITE_ALLOY)
                .pattern("CBC")
                .pattern("CBC")
                .pattern(" A ")
                .unlockedBy(getHasName(AllBlocks.CRIMSCHIST_CASING), has(AllBlocks.CRIMSCHIST_CASING))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_mechanical_harvester"));
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, AllBlocks.CRIMSCHIST_CASING_SLAB, AllBlocks.CRIMSCHIST_CASING, 2);
    }
}
