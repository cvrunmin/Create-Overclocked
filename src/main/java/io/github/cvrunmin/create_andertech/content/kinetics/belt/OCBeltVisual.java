package io.github.cvrunmin.create_andertech.content.kinetics.belt;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.BeltVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.minecraft.core.Direction;

import java.util.Optional;

public class OCBeltVisual extends BeltVisual implements BeltVisualCustomPulley{
    public OCBeltVisual(VisualizationContext context, BeltBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    private Model getPulleyModel() {
        Direction dir = getOrientation();

        return Models.partial(CAAllPartialModels.CRIMSCHIST_BELT_PULLEY, dir.getAxis(), (axis11, modelTransform1) -> {
            var msr = TransformStack.of(modelTransform1);
            msr.center();
            if (axis11 == Direction.Axis.X) msr.rotateYDegrees(90);
            if (axis11 == Direction.Axis.Y) msr.rotateXDegrees(90);
            msr.rotateXDegrees(90);
            msr.uncenter();
        });
    }

    private Direction getOrientation() {
        Direction dir = blockState.getValue(BeltBlock.HORIZONTAL_FACING)
                .getClockWise();

        if (blockState.getValue(BeltBlock.SLOPE) == BeltSlope.SIDEWAYS)
            dir = Direction.UP;

        return dir;
    }

    @Override
    public Optional<Model> getCustomPulleyModel() {
        return Optional.of(getPulleyModel());
    }
}
