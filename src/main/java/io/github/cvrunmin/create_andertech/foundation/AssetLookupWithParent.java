package io.github.cvrunmin.create_andertech.foundation;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.Map;
import java.util.function.Function;

/**
 * Helper class that direct model location to ** /cloned/*
 * <p>
 * See {@link com.simibubi.create.foundation.data.AssetLookup} for more information
 */
public class AssetLookupWithParent {

    public static ModelFile anyParent(ResourceLocation location){
        return new ModelFile.UncheckedModelFile(location);
    }

    public static String makeResourceLoc(String prefix, String name, String... suffix){
        var sb = new StringBuilder(prefix).append("/").append(name);
        for (String s : suffix) {
            sb.append("_").append(s);
        }
        return sb.toString();
    }

    public static ModelFile specifiedModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String modelPath, String parentModid, String parentPath, Map<String, ResourceLocation> overriddenTextures){
        var mf = prov.models().getBuilder(modelPath).parent(anyParent(ResourceLocation.fromNamespaceAndPath(parentModid, parentPath)));
        for (Map.Entry<String, ResourceLocation> entry : overriddenTextures.entrySet()) {
            mf = mf.texture(entry.getKey(), entry.getValue());
        }
        return mf;
    }

    public static ModelFile partialModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures, String... suffix){
        String location = makeResourceLoc("block", parentName + "/block", suffix);
        String myLocation = makeResourceLoc("block", ctx.getName() + "/block", suffix);
        return specifiedModel(ctx, prov, myLocation, modid, location, overriddenTextures);
    }

    private static ModelFile partialModelComplex(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures, String[] suffix, String[] parentSuffix){
        String location = makeResourceLoc("block", parentName + "/block", parentSuffix);
        String myLocation = makeResourceLoc("block", ctx.getName() + "/block", suffix);
        return specifiedModel(ctx, prov, myLocation, modid, location, overriddenTextures);
    }

    public static ModelFile standardModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures){
        String location = makeResourceLoc("block", parentName);
        return specifiedModel(ctx, prov, ctx.getName(), modid, location, overriddenTextures);
    }

    public static Function<BlockState, ModelFile> forPowered(DataGenContext<?, ?> ctx,
                                                             RegistrateBlockstateProvider prov, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures, Map<String, ResourceLocation> poweredOverriddenTextures) {
        return state -> state.getValue(BlockStateProperties.POWERED) ? partialModel(ctx, prov, modid, parentName, poweredOverriddenTextures, "powered")
                : partialModel(ctx, prov, modid, parentName, overriddenTextures);
    }

    public static Function<BlockState, ModelFile> forPoweredLowDefined(DataGenContext<?, ?> ctx,
                                                             RegistrateBlockstateProvider prov, Map<String, ResourceLocation> overriddenTextures) {
        return state -> state.getValue(BlockStateProperties.POWERED) ? partialModelComplex(ctx, prov, ctx.getId().getNamespace(), ctx.getName(), overriddenTextures, new String[]{"powered"}, new String[]{})
                : AssetLookup.partialBaseModel(ctx, prov);
    }
}
