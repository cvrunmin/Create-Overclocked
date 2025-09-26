package io.github.cvrunmin.create_andertech;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import com.simibubi.create.content.contraptions.bearing.BearingVisual;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import io.github.cvrunmin.create_andertech.content.contraptions.bearing.ChronenditeMechanicalBearingBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.drill.ChronenditeDrillBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.drill.ChronenditeDrillRenderer;
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

import static io.github.cvrunmin.create_andertech.CreateAndertech.REGISTRATE;

public class AllBlockEntityTypes {
    public static final BlockEntityEntry<CreativeOCMotorBlockEntity> OC_MOTOR = REGISTRATE
            .blockEntity("oc_motor", CreativeOCMotorBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF), false)
            .validBlocks(AllBlocks.CREATIVE_OC_MOTOR)
            .renderer(() -> CreativeOCMotorRenderer::new)
            .register();

    public static final BlockEntityEntry<BracketedOCKineticBlockEntity> BRACKETED_OC_KINETIC = REGISTRATE
            .blockEntity("simple_oc_kinetic", BracketedOCKineticBlockEntity::new)
            .visual(() -> BracketedOCKineticBlockEntityVisual::create, false)
            .validBlocks(AllBlocks.CRIMSCHIST_SHAFT, AllBlocks.CRIMSCHIST_COGWHEEL, AllBlocks.CRIMSCHIST_LARGE_COGWHEEL)
            .renderer(() -> BracketedOCKineticBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<OCKineticBlockEntity> ENCASED_CRIMSCHIST_SHAFT = REGISTRATE
            .blockEntity("encased_crimschist_shaft", OCKineticBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft, false)
            .validBlocks(AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_SHAFT, AllBlocks.BRASS_ENCASED_CRIMSCHIST_SHAFT, AllBlocks.ENCASED_CRIMSCHIST_CHAIN_DRIVE,
                    AllBlocks.METAL_GIRDER_ENCASED_CRIMSCHIST_SHAFT)
            .renderer(() -> ShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<SimpleOCKineticBlockEntity> ENCASED_CRIMSCHIST_COGWHEEL = REGISTRATE
            .blockEntity("encased_crimschist_cogwheel", SimpleOCKineticBlockEntity::new)
            .visual(() -> EncasedCogVisual::small, false)
            .validBlocks(AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_COGWHEEL, AllBlocks.BRASS_ENCASED_CRIMSCHIST_COGWHEEL)
            .renderer(() -> EncasedCogRenderer::small)
            .register();

    public static final BlockEntityEntry<SimpleOCKineticBlockEntity> ENCASED_CRIMSCHIST_LARGE_COGWHEEL = REGISTRATE
            .blockEntity("encased_crimschist_large_cogwheel", SimpleOCKineticBlockEntity::new)
            .visual(() -> EncasedCogVisual::large, false)
            .validBlocks(AllBlocks.ANDESITE_ENCASED_CRIMSCHIST_LARGE_COGWHEEL, AllBlocks.BRASS_ENCASED_CRIMSCHIST_LARGE_COGWHEEL)
            .renderer(() -> EncasedCogRenderer::large)
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
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.DRILL_HEAD), false)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_DRILL)
            .renderer(() -> ChronenditeDrillRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeSawBlockEntity> CHRONENDITE_SAW = REGISTRATE
            .blockEntity("saw", ChronenditeSawBlockEntity::new)
            .visual(() -> ChronenditeSawVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_SAW)
            .renderer(() -> ChronenditeSawRenderer::new)
            .register();

    public static final BlockEntityEntry<ChronenditeMechanicalBearingBlockEntity> CHRONENDITE_MECHANICAL_BEARING = REGISTRATE
            .blockEntity("mechanical_bearing", ChronenditeMechanicalBearingBlockEntity::new)
            .visual(() -> BearingVisual::new)
            .validBlocks(AllBlocks.CHRONENDITE_MECHANICAL_BEARING)
            .renderer(() -> BearingRenderer::new)
            .register();

    public static void register(){}
}
