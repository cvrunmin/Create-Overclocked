package io.github.cvrunmin.create_andertech.content.kinetics.mixer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import io.github.cvrunmin.create_andertech.compat.jei.category.SequencedAssemblyStirringSubCategory;
import io.github.cvrunmin.create_andertech.foundation.MyLang;
import io.github.cvrunmin.create_andertech.index.CAAllRecipeTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class StirringRecipe extends BasinRecipe implements IAssemblyRecipe {
    public StirringRecipe(ProcessingRecipeParams params) {
        super(CAAllRecipeTypes.STIRRING, params);
    }

    @Override
    public Component getDescriptionForAssembly() {
        return MyLang.builder().translate("recipe.assembly.stirring").component();
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(AllBlocks.MECHANICAL_MIXER);
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {

    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> SequencedAssemblyStirringSubCategory::new;
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }
}
