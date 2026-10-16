package io.github.cvrunmin.create_andertech.index;

import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.cvrunmin.create_andertech.foundation.AssetLookupWithParent;
import net.minecraft.resources.ResourceLocation;

public class AllExtraModels {
    public static void genBlockStates(RegistrateBlockstateProvider provider){
        // provider is already in mod's namespace, model folder
        // when a builder is created, it will generate.
        provider.models().getBuilder("block/crimschist_belt_pulley")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/belt_pulley")))
                .texture("0", provider.modLoc("block/crimschist_axis"))
                .texture("1", provider.modLoc("block/crimschist_axis_top"));
        provider.models().getBuilder("block/chronendite_mechanical_drill/head")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_drill/head")))
                .texture("1", provider.modLoc("block/crimschist_axis"))
                .texture("0", provider.modLoc("block/crimschist_axis_top"))
                .texture("10", provider.modLoc("block/chronendite_casing_very_short"));
        provider.models().getBuilder("block/chronendite_rotation_speed_controller/bracket")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/rotation_speed_controller/bracket")))
                .texture("2", provider.modLoc("block/chronendite_casing"))
                .texture("4", provider.modLoc("block/chronendite_block"))
                .texture("5", provider.modLoc("block/chronendite_rotation_speed_controller_bracket"))
                .texture("7", provider.modLoc("block/chronendite_funnel_neutral"))
                .texture("particle", provider.modLoc("block/chronendite_block"));

        provider.models().getBuilder("block/chronendite_mechanical_arm/cog")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/cog")))
                .texture("5", provider.modLoc("block/chronendite_flywheel"));
        provider.models().getBuilder("block/chronendite_mechanical_arm/base")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/base")))
                .texture("3", provider.modLoc("block/chronendite_block"))
                .texture("5", provider.modLoc("block/chronendite_mechanical_arm"));
        provider.models().getBuilder("block/chronendite_mechanical_arm/claw_base")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/claw_base")))
                .texture("5", provider.modLoc("block/chronendite_mechanical_arm"));
        provider.models().getBuilder("block/chronendite_mechanical_arm/claw_base_goggles")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/claw_base_goggles")))
                .texture("5", provider.modLoc("block/chronendite_mechanical_arm"));
        provider.models().getBuilder("block/chronendite_mechanical_arm/lower_body")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/lower_body")))
                .texture("5", provider.modLoc("block/chronendite_mechanical_arm"));
        provider.models().getBuilder("block/chronendite_mechanical_arm/lower_claw_grip")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/lower_claw_grip")))
                .texture("5", provider.modLoc("block/chronendite_mechanical_arm"));
        provider.models().getBuilder("block/chronendite_mechanical_arm/upper_body")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/upper_body")))
                .texture("5", provider.modLoc("block/chronendite_mechanical_arm"));
        provider.models().getBuilder("block/chronendite_mechanical_arm/upper_claw_grip")
                .parent(AssetLookupWithParent.anyParent(ResourceLocation.fromNamespaceAndPath("create", "block/mechanical_arm/upper_claw_grip")))
                .texture("5", provider.modLoc("block/chronendite_mechanical_arm"));
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
