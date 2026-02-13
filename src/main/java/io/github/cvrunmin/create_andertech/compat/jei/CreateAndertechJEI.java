package io.github.cvrunmin.create_andertech.compat.jei;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.category.BlockCuttingCategory;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;

@JeiPlugin
public class CreateAndertechJEI implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "jei_plugin");
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
        registration.getJeiHelpers().getRecipeType(Create.asResource("fan_washing"), SplashingRecipe.class)
                .ifPresent(rtype -> {
                    registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_ENCASED_FAN.get(), rtype);
                    registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_HV_ENCASED_FAN.get(), rtype);
                });
        registration.getJeiHelpers().getRecipeType(Create.asResource("fan_smoking"), SmokingRecipe.class)
                .ifPresent(rtype -> {
                    registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_ENCASED_FAN.get(), rtype);
                    registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_HV_ENCASED_FAN.get(), rtype);
                });
        registration.getJeiHelpers().getRecipeType(Create.asResource("fan_blasting"), AbstractCookingRecipe.class)
                .ifPresent(rtype -> {
                    registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_ENCASED_FAN.get(), rtype);
                    registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_HV_ENCASED_FAN.get(), rtype);
                });
        registration.getJeiHelpers().getRecipeType(Create.asResource("fan_haunting"), HauntingRecipe.class)
                .ifPresent(rtype -> {
                    registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_ENCASED_FAN.get(), rtype);
                    registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_HV_ENCASED_FAN.get(), rtype);
                });
        registration.getJeiHelpers().getRecipeType(Create.asResource("sawing"), CuttingRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_SAW.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("block_cutting"), BlockCuttingCategory.CondensedBlockCuttingRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_SAW.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("deploying"), DeployerApplicationRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_DEPLOYER.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("automatic_shaped"), CraftingRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_CRAFTER.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("mechanical_crafting"), CraftingRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_MECHANICAL_CRAFTER.get(), rtype));
        registration.getJeiHelpers().getRecipeType(Create.asResource("crushing"), CrushingRecipe.class)
                .ifPresent(rtype -> registration.addRecipeCatalyst(AllBlocks.CHRONENDITE_CRUSHING_WHEEL.get(), rtype));
    }
}
