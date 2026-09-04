package io.github.cvrunmin.create_andertech.compat.jei;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.BlockCuttingCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import io.github.cvrunmin.create_andertech.compat.jei.category.InfusionVatCategory;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatRecipe;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.index.CAAllRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CreateAndertechJEI implements IModPlugin {
    private final List<CreateRecipeCategory<?>> createCategories = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "jei_plugin");
    }

    private void loadCreateCategories(){
        createCategories.clear();
        var infusing = new CreateRecipeCategory.Builder<>(InfusionVatRecipe.class)
                .addTypedRecipes(CAAllRecipeTypes.INFUSING)
                .itemIcon(AllBlocks.INFUSION_VAT.asItem())
                .emptyBackground(177, 70)
                .catalyst(AllBlocks.INFUSION_VAT::get)
                .build(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "infusing"), InfusionVatCategory::new);
        createCategories.add(infusing);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCreateCategories();
        registration.addRecipeCategories(createCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        createCategories.forEach(cat -> cat.registerCatalysts(registration));

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

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        createCategories.forEach(cat -> cat.registerRecipes(registration));
        registration.getJeiHelpers().getRecipeType(Create.asResource("mystery_conversion"), ConversionRecipe.class)
                .ifPresent(rtype -> registration.addRecipes(rtype, List.of(
                        ConversionRecipe.create(AllItems.EMPTY_BLAZE_BURNER.asStack(), AllBlocks.CHRONENDIC_BLAZE_BURNER.asStack()).value(),
                        ConversionRecipe.create(com.simibubi.create.AllBlocks.BLAZE_BURNER.asStack(), AllBlocks.CHRONENDIC_BLAZE_BURNER.asStack()).value()
                )));
    }
}
