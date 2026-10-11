package io.github.cvrunmin.create_andertech.content.contraptions.actors.harvester;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class CrimschistHarvesterActorVisual extends ActorVisual {
    protected TransformedInstance harvester;
    private double rotation;
    private double previousRotation;

    public CrimschistHarvesterActorVisual(VisualizationContext visualizationContext, BlockAndTintGetter world, MovementContext context) {
        super(visualizationContext, world, context);
        BlockState state = context.state;
        harvester = instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(CAAllPartialModels.CRIMSCHIST_HARVESTER_BLADE))
                .createInstance();
        harvester.light(localBlockLight(), 0);
        harvester.setChanged();
    }

    @Override
    public void tick() {
        super.tick();
        previousRotation = rotation;

        if (context.contraption.stalled || context.disabled
                || VecHelper.isVecPointingTowards(context.relativeMotion, Direction.UP))
            return;

        double arcLength = context.motion.length();

        double radians = arcLength * 16 / 6.5;

        float deg = AngleHelper.deg(radians);

        deg = (float) (((int) (deg * 3000)) / 3000);

        rotation += deg * 1.25;

        rotation %= 360;
    }

    @Override
    public void beginFrame() {
        harvester.setIdentityTransform()
                .translate(context.localPos)
                .rotateYCenteredDegrees((float) getRotation())
                .setChanged();
    }

    @Override
    protected void _delete() {
        harvester.delete();
    }

    protected double getRotation() {
        return AngleHelper.angleLerp(AnimationTickHolder.getPartialTicks(), previousRotation, rotation);
    }
}
