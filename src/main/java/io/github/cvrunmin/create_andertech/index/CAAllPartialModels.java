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
    public static final PartialModel CRIMSCHIST_HARVESTER_BLADE = block("crimschist_mechanical_harvester/blade");


    public static final PartialModel ARM_COG = block("chronendite_mechanical_arm/cog"), ARM_BASE = block("chronendite_mechanical_arm/base"),
    ARM_LOWER_BODY = block("chronendite_mechanical_arm/lower_body"), ARM_UPPER_BODY = block("chronendite_mechanical_arm/upper_body"),
    ARM_CLAW_BASE = block("chronendite_mechanical_arm/claw_base"),
    ARM_CLAW_BASE_GOGGLES = block("chronendite_mechanical_arm/claw_base_goggles"),
    ARM_CLAW_GRIP_UPPER = block("chronendite_mechanical_arm/upper_claw_grip"),
    ARM_CLAW_GRIP_LOWER = block("chronendite_mechanical_arm/lower_claw_grip");

    public static final PartialModel
            BLAZE_INERT = block("chronendic_blaze_burner/blaze/inert"), BLAZE_SUPER_ACTIVE = block("chronendic_blaze_burner/blaze/super_active"),
            BLAZE_IDLE = block("chronendic_blaze_burner/blaze/idle"), BLAZE_ACTIVE = block("chronendic_blaze_burner/blaze/active"),
            BLAZE_SUPER = block("chronendic_blaze_burner/blaze/super"),
            BLAZE_BURNER_RODS = block("chronendic_blaze_burner/rods_small"), BLAZE_BURNER_RODS_2 = block("chronendic_blaze_burner/rods_large"),
            BLAZE_BURNER_SUPER_RODS = block("chronendic_blaze_burner/superheated_rods_small"),
            BLAZE_BURNER_SUPER_RODS_2 = block("chronendic_blaze_burner/superheated_rods_large");

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateAndertech.MODID, "block/" + path));
    }

    public static void init(){

    }
}
