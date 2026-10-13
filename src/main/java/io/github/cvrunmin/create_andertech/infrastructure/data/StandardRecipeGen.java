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
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class StandardRecipeGen extends RecipeProvider {
    public StandardRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        nineBlockStorageRecipesWithCustomPacking(recipeOutput, RecipeCategory.MISC, AllItems.CHRONENDITE_SHARD, RecipeCategory.MISC, AllItems.CHRONENDITE_ORB, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "chronendite_orb_from_shards").toString(), "chronendite_orb");
        nineBlockStorageRecipesWithCustomPacking(recipeOutput, RecipeCategory.MISC, AllItems.CRIMSCHIST_NUGGET, RecipeCategory.MISC, AllItems.CRIMSCHIST_ALLOY, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_alloy_from_nuggest").toString(), "crimschist_alloy");
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_FLUID_TANK)
                .define('A', AllItems.CRIMSCHIST_SHEET)
                .define('B', AllBlocks.INFUSION_VAT)
                .pattern("A")
                .pattern("B")
                .unlockedBy(getHasName(AllBlocks.INFUSION_VAT), has(AllBlocks.INFUSION_VAT))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_fluid_tank_by_add_top"));
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
                .define('B', Items.IRON_INGOT)
                .define('C', com.simibubi.create.AllItems.ANDESITE_ALLOY)
                .pattern("CBC")
                .pattern("CBC")
                .pattern(" A ")
                .unlockedBy(getHasName(AllBlocks.CRIMSCHIST_CASING), has(AllBlocks.CRIMSCHIST_CASING))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_mechanical_harvester"));
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, AllBlocks.CRIMSCHIST_CASING_SLAB, AllBlocks.CRIMSCHIST_CASING, 2);
    }
}
