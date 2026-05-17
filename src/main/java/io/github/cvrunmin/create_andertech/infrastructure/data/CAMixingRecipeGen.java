package io.github.cvrunmin.create_andertech.infrastructure.data;

import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.AllFluids;
import io.github.cvrunmin.create_andertech.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CAMixingRecipeGen extends MixingRecipeGen {

    GeneratedRecipe CRIMSCHIST_ALLOY = create("crimschist_alloy", b ->
            b.require(Tags.Items.INGOTS_COPPER)
                    .require(Tags.Items.INGOTS_COPPER)
                    .require(Tags.Items.INGOTS_COPPER)
                    .require(Items.WARPED_WART_BLOCK)
                    .require(Items.WARPED_WART_BLOCK)
                    .output(AllItems.CRIMSCHIST_ALLOY, 2)
                    .requiresHeat(HeatCondition.HEATED));

    GeneratedRecipe CHRONENDITE_SOLUTION = create("chronendite_solution", b ->
            b.require(Items.CHORUS_FRUIT)
                    .require(Items.WARPED_WART_BLOCK)
                    .require(Items.WARPED_WART_BLOCK)
                    .require(Items.ENDER_PEARL)
                    .require(Items.ENDER_PEARL)
                    .require(Items.ENDER_PEARL)
                    .require(Fluids.WATER, 500)
                    .output(AllFluids.CHRONENDITE_SOLUTION.get(), 270)
                    .requiresHeat(HeatCondition.HEATED));

    GeneratedRecipe DISSOLVE_INTO_CHRONENDITE_SOLUTION = create("dissolve_into_chronendite_solution", b ->
            b.require(AllItems.CHRONENDITE_ORB)
                    .require(Fluids.WATER, 90)
                    .output(AllFluids.CHRONENDITE_SOLUTION.get(), 90)
                    .requiresHeat(HeatCondition.HEATED));

    GeneratedRecipe CRYSTALLIZE_CHRONENDITE_SOLUTION_PRIMAL = create("crystallize_chronendite_solution_primal", b ->
            b.require(Items.SLIME_BALL)
                    .require(AllFluids.CHRONENDITE_SOLUTION.get(), 90)
                    .output(AllItems.CHRONENDITE_SHARD.get())
                    .requiresHeat(HeatCondition.HEATED));

    GeneratedRecipe CRYSTALLIZE_CHRONENDITE_SOLUTION_SEEDING = create("crystallize_chronendite_solution_seeding", b ->
            b.require(AllItems.CHRONENDITE_SHARD)
                    .require(AllFluids.CHRONENDITE_SOLUTION.get(), 80)
                    .output(AllItems.CHRONENDITE_ORB.get())
                    .requiresHeat(HeatCondition.HEATED));

    GeneratedRecipe FORGED_CHRONENDITE_ORB = create("forged_chronendite_orb", b ->
            b.require(Items.SLIME_BALL)
                    .require(Items.WARPED_WART_BLOCK)
                    .require(Items.WARPED_WART_BLOCK)
                    .require(Items.ENDER_PEARL)
                    .require(Items.PURPLE_DYE)
                    .require(Fluids.WATER, 500)
                    .output(AllItems.FORGED_CHRONENDITE_ORB, 2)
                    .requiresHeat(HeatCondition.HEATED));

    GeneratedRecipe ENDOPYROTIC_CINDER_PASTE = create("endopyrotic_cinder_paste", b ->
            b.require(Items.GUNPOWDER)
                    .require(com.simibubi.create.AllItems.CINDER_FLOUR)
                    .require(com.simibubi.create.AllItems.CINDER_FLOUR)
                    .require(AllItems.FORGED_CHRONENDITE_ORB).output(AllItems.ENDOPYROTIC_CINDER_PASTE, 2));

    public CAMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAndertech.MODID);
    }
}
