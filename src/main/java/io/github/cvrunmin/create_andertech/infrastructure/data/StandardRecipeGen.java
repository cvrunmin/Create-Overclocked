package io.github.cvrunmin.create_andertech.infrastructure.data;

import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.concurrent.CompletableFuture;

public class StandardRecipeGen extends RecipeProvider {
    public StandardRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        nineBlockStorageRecipesWithCustomPacking(recipeOutput, RecipeCategory.MISC, AllItems.CHRONENDITE_SHARD, RecipeCategory.MISC, AllItems.CHRONENDITE_ORB, "chronendite_orb_from_shards", "chronendite_orb");
        nineBlockStorageRecipesWithCustomPacking(recipeOutput, RecipeCategory.MISC, AllItems.CRIMSCHIST_NUGGET, RecipeCategory.MISC, AllItems.CRIMSCHIST_ALLOY, "crimschist_alloy_from_nuggest", "crimschist_alloy");
    }
}
