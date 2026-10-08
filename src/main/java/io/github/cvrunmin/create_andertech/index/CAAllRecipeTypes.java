package io.github.cvrunmin.create_andertech.index;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatRecipe;
import io.github.cvrunmin.create_andertech.content.kinetics.mixer.StirringRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class CAAllRecipeTypes {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateAndertech.MODID);
    private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, CreateAndertech.MODID);


    public static final CARecipeTypeInfo INFUSING = new CARecipeTypeInfo("infusing", InfusionVatRecipe::new);
    public static final CARecipeTypeInfo STIRRING = new CARecipeTypeInfo("stirring", StirringRecipe::new);

    public static void register(IEventBus bus){
        SERIALIZER_REGISTER.register(bus);
        TYPE_REGISTER.register(bus);
    }

    public static class CARecipeTypeInfo implements IRecipeTypeInfo {

        private final ResourceLocation id;
        private final Supplier<RecipeSerializer<?>> serializerSupplier;
        private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
        @Nullable
        private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
        private final Supplier<RecipeType<?>> type;

        public CARecipeTypeInfo(String name, Supplier<RecipeSerializer<?>> serializerSupplier){
            id = ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, name);
            this.serializerSupplier = serializerSupplier;
            this.serializerObject = SERIALIZER_REGISTER.register(name, serializerSupplier);
            this.typeObject = TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
            type = this.typeObject;
        }

        public CARecipeTypeInfo(String name, StandardProcessingRecipe.Factory<?> factory){
            this(name, () -> new StandardProcessingRecipe.Serializer<>(factory));
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) serializerObject.get();
        }

        @Override
        public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
            return (RecipeType<R>) type.get();
        }

        public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I input, Level level){
            return level.getRecipeManager().getRecipeFor(getType(), input, level);
        }
    }
}
