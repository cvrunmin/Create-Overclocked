package io.github.cvrunmin.create_andertech.index;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import io.github.cvrunmin.create_andertech.CreateAndertech;
import net.minecraft.resources.ResourceLocation;

public class CAAllPartialModels {

    public static final PartialModel CRIMSCHIST_BELT_PULLEY = block("crimschist_belt_pulley");
    public static final PartialModel CRIMSCHIST_SHAFT = block("crimschist_shaft");
    public static final PartialModel CRIMSCHIST_SHAFT_HALF = block("crimschist_shaft_half");
    public static final PartialModel SHAFTLESS_CRIMSCHIST_COGWHEEL = block("crimschist_cogwheel_shaftless");
    public static final PartialModel SHAFTLESS_LARGE_CRIMSCHIST_COGWHEEL = block("crimschist_large_cogwheel_shaftless");
    public static final PartialModel CRIMSCHIST_COGWHEEL_SHAFT = block("crimschist_cogwheel_shaft");
    public static final PartialModel CRIMSCHIST_COGWHEEL = block("crimschist_cogwheel");
    public static final PartialModel CHRONENDITE_DRILL_HEAD = block("chronendite_mechanical_drill/head");
    public static final PartialModel CHRONENDITE_CRUSHING_WHEEL = block("chronendite_crushing_wheel/block");
    public static final PartialModel CHRONENDITE_SPEED_CONTROLLER_BRACKET = block("chronendite_rotation_speed_controller/bracket");

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/" + path));
    }

    public static void init(){

    }
}
