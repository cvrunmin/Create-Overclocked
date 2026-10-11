package io.github.cvrunmin.create_andertech.index;

import com.simibubi.create.content.contraptions.bearing.BearingVisual;
import com.simibubi.create.content.kinetics.base.*;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmRenderer;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.engine_room.flywheel.lib.model.Models;
import io.github.cvrunmin.create_andertech.content.contraptions.actors.harvester.CrimschistHarvesterBlockEntity;
import io.github.cvrunmin.create_andertech.content.contraptions.actors.harvester.CrimschistHarvesterRenderer;
import io.github.cvrunmin.create_andertech.content.contraptions.bearing.ChronenditeMechanicalBearingBlockEntity;
import io.github.cvrunmin.create_andertech.content.contraptions.bearing.OCBearingRenderer;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatBlockEntity;
import io.github.cvrunmin.create_andertech.content.fluids.infusion_vat.InfusionVatRenderer;
import io.github.cvrunmin.create_andertech.content.fluids.pump.CrimschistPumpBlockEntity;
import io.github.cvrunmin.create_andertech.content.fluids.pump.CrimschistPumpRenderer;
import io.github.cvrunmin.create_andertech.content.fluids.tank.CrimschistFluidTankBlockEntity;
import io.github.cvrunmin.create_andertech.content.fluids.tank.CrimschistFluidTankRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.CrimschistShaftVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.crafter.ChronenditeMechanicalCrafterBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.crafter.ChronenditeMechanicalCrafterRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.crusher.ChronenditeCrushWheelBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.crusher.ChronenditeCrushWheelControllerBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.deployer.ChronenditeDeployerBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.deployer.ChronenditeDeployerRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.deployer.ChronenditeDeployerVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.drill.ChronenditeDrillBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.drill.ChronenditeDrillRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.ChronenditeEncasedFanBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.ChronenditeEncasedFanRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.ChronenditeHVEncasedFanBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.fan.OCFanVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.gearbox.CrimschistGearboxBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.gearbox.CrimschistGearboxRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.gearbox.CrimschistGearboxVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.mechanicalArm.ChronenditeArmBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.mixer.ChronenditeMechanicalMixerBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.mixer.ChronenditeMechanicalMixerRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.mixer.ChronenditeMixerVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.motor.CreativeOCMotorBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.motor.CreativeOCMotorRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.press.ChronenditeMechanicalPressBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.press.ChronenditeMechanicalPressRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.press.ChronenditePressVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.saw.ChronenditeSawVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.*;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased.EncasedCrimschistCogRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased.EncasedCrimschistCogVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.speedController.ChronenditeSpeedControllerRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.spout.ChronenditeMechanicalSpoutBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.spout.ChronenditeMechanicalSpoutRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistBlackboxDifferentialVisual;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistClutchBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.CrimschistGearshiftBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.OCSplitShaftRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.transmission.OCSplitShaftVisual;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerBlockEntity;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerRenderer;
import io.github.cvrunmin.create_andertech.content.processing.burner.ChronendicBlazeBurnerVisual;

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllBlockEntityTypes {
    public static final BlockEntityEntry<CreativeOCMotorBlockEntity> OC_MOTOR = REGISTRATE
            .blockEntity("oc_motor", CreativeOCMotorBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(CAAllPartialModels.CRIMSCHIST_SHAFT_HALF), false)
            .validBlocks(AllBlocks.CREATIVE_OC_MOTOR)
            .renderer(() -> CreativeOCMotorRenderer::new)
            .register();

    public static final BlockEntityEntry<BracketedOCKineticBlockEntity> BRACKETED_OC_KINETIC = REGISTRATE
            .blockEntity("simple_oc_kinetic", BracketedOCKineticBlockEntity::new)
            .visual(() -> BracketedOCKineticBlockEntityVisual::create, false)
            .validBlocks(AllBlocks.CRIMSCHIST_SHAFT, AllBlocks.CRIMSCHIST_COGWHEEL, AllBlocks.CRIMSCHIST_LARGE_COGWHEEL)
            .renderer(() -> BracketedOCKineticBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<CrimschistGearboxBlockEntity> CRIMSCHIST_GEARBOX = REGISTRATE
            .blockEntity("gearbox", CrimschistGearboxBlockEntity::new)
            .visual(() -> CrimschistGearboxVisual::new, false)
            .validBlocks(AllBlocks.CRIMSCHIST_GEARBOX)
            .renderer(() -> CrimschistGearboxRenderer::new)
            .register();

    public static final BlockEntityEntry<OCKineticBlockEntity> ENCASED_CRIMSCHIST_SHAFT = REGISTRATE
            .blockEntity("encased_crimschist_shaft", OCKineticBlockEntity::new)
            .visual(() -> ((ctx, blockEntity, partialTick) -> new SingleAxisRotatingVisual<>(ctx, blockEntity, partialTick, Models.partial(CAAllPartialModels.CRIMSCHIST_SHAFT))), false)
            .validBlocks(AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_SHAFT, AllBlocks.BRASS_ENCASED_CRIMSCHIST_SHAFT, AllBlocks.ENCASED_CRIMSCHIST_CHAIN_DRIVE,
                    AllBlocks.METAL_GIRDER_ENCASED_CRIMSCHIST_SHAFT)
            .renderer(() -> ShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<SimpleOCKineticBlockEntity> ENCASED_CRIMSCHIST_COGWHEEL = REGISTRATE
            .blockEntity("encased_crimschist_cogwheel", SimpleOCKineticBlockEntity::new)
            .visual(() -> EncasedCrimschistCogVisual::small, false)
            .validBlocks(AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_COGWHEEL, AllBlocks.BRASS_ENCASED_CRIMSCHIST_COGWHEEL)
            .renderer(() -> EncasedCrimschistCogRenderer::small)
            .register();

    public static final BlockEntityEntry<SimpleOCKineticBlockEntity> ENCASED_CRIMSCHIST_LARGE_COGWHEEL = REGISTRATE
            .blockEntity("encased_crimschist_large_cogwheel", SimpleOCKineticBlockEntity::new)
            .visual(() -> EncasedCrimschistCogVisual::large, false)
            .validBlocks(AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_LARGE_COGWHEEL, AllBlocks.BRASS_ENCASED_CRIMSCHIST_LARGE_COGWHEEL)
            .renderer(() -> EncasedCrimschistCogRenderer::large)
            .register();

    public static final BlockEntityEntry<ChronenditeCrushWheelBlockEntity> CHRONENDITE_CRUSHING_WHEEL = REGISTRATE
            .blockEntity("crushing_wheel", ChronenditeCrushWheelBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual.of(CAAllPartialModels.CHRONENDITE_CRUSHING_WHEEL), false)
            .validBlocks(AllBlocks.CHRONENDITE_CRUSHING_WHEEL)
            .renderer(() -> KineticBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeCrushWheelControllerBlockEntity> CHRONENDITE_CRUSHING_WHEEL_CONTROLLER =
            REGISTRATE
                    .blockEntity("crushing_wheel_controller", ChronenditeCrushWheelControllerBlockEntity::new)
                    .validBlocks(AllBlocks.CHRONENDITE_CRUSHING_WHEEL_CONTROLLER)
                    .register();

    public static final BlockEntityEntry<ChronenditeMechanicalPressBlockEntity> CHRONENDITE_MECHANICAL_PRESS = REGISTRATE
            .blockEntity("mechanical_press", ChronenditeMechanicalPressBlockEntity::new)
            .visual(() -> ChronenditePressVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_PRESS)
            .renderer(() -> ChronenditeMechanicalPressRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeMechanicalMixerBlockEntity> CHRONENDITE_MECHANICAL_MIXER = REGISTRATE
            .blockEntity("mechanical_mixer", ChronenditeMechanicalMixerBlockEntity::new)
            .visual(() -> ChronenditeMixerVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_MIXER)
            .renderer(() -> ChronenditeMechanicalMixerRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeDrillBlockEntity> CHRONENDITE_DRILL = REGISTRATE
            .blockEntity("drill", ChronenditeDrillBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(CAAllPartialModels.CHRONENDITE_DRILL_HEAD), false)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_DRILL)
            .renderer(() -> ChronenditeDrillRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeSawBlockEntity> CHRONENDITE_SAW = REGISTRATE
            .blockEntity("saw", ChronenditeSawBlockEntity::new)
            .visual(() -> ChronenditeSawVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_SAW)
            .renderer(() -> ChronenditeSawRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeDeployerBlockEntity> CHRONENDITE_DEPLOYER = REGISTRATE
            .blockEntity("deployer", ChronenditeDeployerBlockEntity::new)
            .visual(() -> ChronenditeDeployerVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_DEPLOYER)
            .renderer(() -> ChronenditeDeployerRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeMechanicalBearingBlockEntity> CHRONENDITE_MECHANICAL_BEARING = REGISTRATE
            .blockEntity("mechanical_bearing", ChronenditeMechanicalBearingBlockEntity::new)
            .visual(() -> BearingVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_BEARING)
            .renderer(() -> OCBearingRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeMechanicalSpoutBlockEntity> CHRONENDITE_MECHANICAL_SPOUT = REGISTRATE
            .blockEntity("mechanical_spout", ChronenditeMechanicalSpoutBlockEntity::new)
            .visual(() -> CrimschistShaftVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_SPOUT)
            .renderer(() -> ChronenditeMechanicalSpoutRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeEncasedFanBlockEntity> CHRONENDITE_ENCASED_FAN = REGISTRATE
            .blockEntity("encased_fan", ChronenditeEncasedFanBlockEntity::new)
            .visual(() -> OCFanVisual::new, false)
            .validBlocks(AllBlocks.CHRONENDITE_ENCASED_FAN)
            .renderer(() -> ChronenditeEncasedFanRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeHVEncasedFanBlockEntity> CHRONENDITE_HV_ENCASED_FAN = REGISTRATE
            .blockEntity("highvolume_encased_fan", ChronenditeHVEncasedFanBlockEntity::new)
            .visual(() -> OCFanVisual::new, false)
            .validBlocks(AllBlocks.CHRONENDITE_HV_ENCASED_FAN)
            .renderer(() -> ChronenditeEncasedFanRenderer::new)
            .register();

    public static final BlockEntityEntry<CrimschistPumpBlockEntity>  CRIMSCHIST_MECHANICAL_PUMP = REGISTRATE
            .blockEntity("mechanical_pump", CrimschistPumpBlockEntity::new)
            .visual(() -> CrimschistShaftVisual::new)
            .validBlocks(AllBlocks.CRIMSCHIST_MECHANICAL_PUMP)
            .renderer(() -> CrimschistPumpRenderer::new)
            .register();

    public static final BlockEntityEntry<CrimschistClutchBlockEntity> CRIMSCHIST_CLUTCH = REGISTRATE
            .blockEntity("clutch", CrimschistClutchBlockEntity::new)
            .visual(() -> OCSplitShaftVisual::new, false)
            .validBlocks(AllBlocks.CRIMSCHIST_CLUTCH)
            .renderer(() -> OCSplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<CrimschistGearshiftBlockEntity> CRIMSCHIST_GEARSHIFT = REGISTRATE
            .blockEntity("gearshift", CrimschistGearshiftBlockEntity::new)
            .visual(() -> OCSplitShaftVisual::new, false)
            .validBlocks(AllBlocks.CRIMSCHIST_GEARSHIFT)
            .renderer(() -> OCSplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<CrimschistBlackboxDifferentialBlockEntity> CRIMSCHIST_BLACKBOX_DIFFERENTIAL = REGISTRATE
            .blockEntity("blackbox_differential", CrimschistBlackboxDifferentialBlockEntity::new)
            .visual(() -> CrimschistBlackboxDifferentialVisual::new)
            .validBlocks(AllBlocks.CRIMSCHIST_BLACKBOX_DIFFERENTIAL)
            .renderer(() -> CrimschistBlackboxDifferentialRenderer::new)
            .register();

    public static final BlockEntityEntry<OCBeltBlockEntity> OC_BELT = REGISTRATE
            .blockEntity("belt", OCBeltBlockEntity::new)
            .visual(() -> OCBeltVisual::new, OCBeltBlockEntity::shouldRenderNormally)
            .validBlocks(AllBlocks.OC_BELT)
            .renderer(() -> OCBeltRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeArmBlockEntity> CHRONENDITE_MECHANICAL_ARM = REGISTRATE
            .blockEntity("mechanical_arm", ChronenditeArmBlockEntity::new)
            .visual(() -> ArmVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_ARM)
            .renderer(() -> ArmRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeMechanicalCrafterBlockEntity> CHRONENDITE_MECHANICAL_CRAFTER = REGISTRATE
            .blockEntity("mechanical_crafter", ChronenditeMechanicalCrafterBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual.of(CAAllPartialModels.SHAFTLESS_CRIMSCHIST_COGWHEEL))
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_CRAFTER)
            .renderer(() -> ChronenditeMechanicalCrafterRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeSpeedControllerBlockEntity> CHRONENDITE_ROTATION_SPEED_CONTROLLER = REGISTRATE
            .blockEntity("rotation_speed_controller", ChronenditeSpeedControllerBlockEntity::new)
            .visual(() -> ((ctx, blockEntity, partialTick) -> new SingleAxisRotatingVisual<>(ctx, blockEntity, partialTick, Models.partial(CAAllPartialModels.CRIMSCHIST_SHAFT))))
            .validBlocks(AllBlocks.CHRONENDITE_ROTATION_SPEED_CONTROLLER)
            .renderer(() -> ChronenditeSpeedControllerRenderer::new)
            .register();

    public static final BlockEntityEntry<CrimschistFluidTankBlockEntity> CRIMSCHIST_FLUID_TANK = REGISTRATE
            .blockEntity("crimschist_fluid_tank", CrimschistFluidTankBlockEntity::new)
            .validBlocks(AllBlocks.CRIMSCHIST_FLUID_TANK)
            .renderer(() -> CrimschistFluidTankRenderer::new)
            .register();

    public static final BlockEntityEntry<InfusionVatBlockEntity> INFUSION_VAT = REGISTRATE
            .blockEntity("crimschist_infusion_vat", InfusionVatBlockEntity::new)
            .validBlocks(AllBlocks.INFUSION_VAT)
            .renderer(() -> InfusionVatRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronendicBlazeBurnerBlockEntity> CHRONENDIC_BLAZE_BURNER = REGISTRATE
            .blockEntity("chronendic_blaze_burner", ChronendicBlazeBurnerBlockEntity::new)
            .visual(() -> ChronendicBlazeBurnerVisual::new, false)
            .validBlock(AllBlocks.CHRONENDIC_BLAZE_BURNER)
            .renderer(() -> ChronendicBlazeBurnerRenderer::new)
            .register();

    public static final BlockEntityEntry<CrimschistHarvesterBlockEntity> CRIMSCHIST_HARVESTER = REGISTRATE
            .blockEntity("crimschist_harvester", CrimschistHarvesterBlockEntity::new)
            .validBlock(AllBlocks.CRIMSCHIST_HARVESTER)
            .renderer(() -> CrimschistHarvesterRenderer::new)
            .register();

    public static void register(){}
}
