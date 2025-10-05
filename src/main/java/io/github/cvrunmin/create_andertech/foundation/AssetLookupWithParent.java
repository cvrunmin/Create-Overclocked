package io.github.cvrunmin.create_andertech.foundation;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Map;
import java.util.function.Function;

/**
 * Helper class that direct model location to ** /cloned/*
 * <p>
 * See {@link com.simibubi.create.foundation.data.AssetLookup} for more information
 */
public class AssetLookupWithParent {

    public static String makeResourceLoc(String prefix, String name, String... suffix){
        var sb = new StringBuilder(prefix).append("/").append(name);
        for (String s : suffix) {
            sb.append("_").append(s);
        }
        return sb.toString();
    }

    public static ModelFile partialModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures, String... suffix){
        String location = makeResourceLoc("block", parentName + "/block", suffix);
        String myLocation = makeResourceLoc("block", ctx.getName() + "/block", suffix);
        var mf = prov.models().getBuilder(myLocation).parent(new ModelFile.UncheckedModelFile(new ResourceLocation(modid, location)));
        for (Map.Entry<String, ResourceLocation> entry : overriddenTextures.entrySet()) {
            mf = mf.texture(entry.getKey(), entry.getValue());
        }
        return mf;
    }

    public static ModelFile standardModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures){
        String location = makeResourceLoc("block", parentName);
        var mf = prov.models().getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile(new ResourceLocation(modid, location)));
        for (Map.Entry<String, ResourceLocation> entry : overriddenTextures.entrySet()) {
            mf = mf.texture(entry.getKey(), entry.getValue());
        }
        return mf;
    }

    public static Function<BlockState, ModelFile> forPowered(DataGenContext<?, ?> ctx,
                                                             RegistrateBlockstateProvider prov, String modid, String parentName, Map<String, ResourceLocation> overriddenTextures) {
        return state -> state.getValue(BlockStateProperties.POWERED) ? partialModel(ctx, prov, modid, parentName, overriddenTextures, "powered")
                : partialModel(ctx, prov, modid, parentName, overriddenTextures);
    }
}
