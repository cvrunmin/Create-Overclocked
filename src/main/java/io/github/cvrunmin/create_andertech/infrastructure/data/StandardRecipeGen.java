package io.github.cvrunmin.create_andertech.infrastructure.data;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
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
        nineBlockStorageRecipesWithCustomPacking(recipeOutput, RecipeCategory.MISC, AllItems.CHRONENDITE_SHARD, RecipeCategory.MISC, AllItems.CHRONENDITE_ORB, "chronendite_orb_from_shards", "chronendite_orb");
        nineBlockStorageRecipesWithCustomPacking(recipeOutput, RecipeCategory.MISC, AllItems.CRIMSCHIST_NUGGET, RecipeCategory.MISC, AllItems.CRIMSCHIST_ALLOY, "crimschist_alloy_from_nuggest", "crimschist_alloy");
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.CRIMSCHIST_FLUID_TANK)
                .define('A', AllItems.CRIMSCHIST_SHEET)
                .define('B', AllBlocks.INFUSION_VAT)
                .pattern("A")
                .pattern("B")
                .unlockedBy(getHasName(AllBlocks.INFUSION_VAT), has(AllBlocks.INFUSION_VAT))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "crimschist_fluid_tank_by_add_top"));
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, AllBlocks.CRIMSCHIST_CASING_SLAB, AllBlocks.CRIMSCHIST_CASING, 2);
    }
}
