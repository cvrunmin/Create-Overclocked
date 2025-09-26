package io.github.cvrunmin.create_andertech.compat.jei;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import io.github.cvrunmin.create_andertech.AllBlocks;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class CreateAndertechJEI implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CreateAndertech.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.getJeiHelpers().getRecipeType(Create.asResource("pressing"), PressingRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_PRESS.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("mixing"), BasinRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_MIXER.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("automatic_shapeless"), BasinRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_MIXER.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("automatic_brewing"), BasinRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_MIXER.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("packing"), BasinRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_PRESS.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("automatic_packing"), BasinRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_PRESS.get(), rtype));
    }
}
