package io.github.cvrunmin.create_andertech.index;

import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.cvrunmin.create_andertech.foundation.AssetLookupWithParent;
import net.minecraft.resources.ResourceLocation;

public class AllExtraModels {
    public static void genBlockStates(RegistrateBlockstateProvider provider){
        // provider is already in mod's namespace, model folder
        // when a builder is created, it will generate.
//        provider.models().getBuilder("block/crimschist_gantry_shaft/block_end")
//                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/gantry_shaft/block_end")))
//                .texture("0", provider.modLoc("block/crimschist_axis"))
//                .texture("1", provider.modLoc("block/crimschist_axis_top"))
//                .texture("3", provider.modLoc("block/crimschist_cogwheel"));
//        provider.models().getBuilder("block/crimschist_gantry_shaft/block_single")
//                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/gantry_shaft/block_single")))
//                .texture("0", provider.modLoc("block/crimschist_axis"))
//                .texture("1", provider.modLoc("block/crimschist_axis_top"))
//                .texture("3", provider.modLoc("block/crimschist_cogwheel"));
//        provider.models().getBuilder("block/crimschist_gantry_shaft/block_start")
//                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/gantry_shaft/block_start")))
//                .texture("0", provider.modLoc("block/crimschist_axis"))
//                .texture("1", provider.modLoc("block/crimschist_axis_top"))
//                .texture("3", provider.modLoc("block/crimschist_cogwheel"));
    }
}
