package io.github.cvrunmin.create_andertech.content.kinetics.transmission;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.BracketedOCKineticBlockEntityRenderer;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Ring and both shaft ends spin at their own port's speed - for COG == IN the ring's speed is not the block
 * entity's own speed, so each part is fed its port value explicitly.
 */
public class CrimschistBlackboxDifferentialVisual extends KineticBlockEntityVisual<CrimschistBlackboxDifferentialBlockEntity> {

    protected final RotatingInstance rotatingModel;
    @Nullable
    protected final RotatingInstance rotatingTopShaft;
    @Nullable
    protected final RotatingInstance rotatingBottomShaft;

    public CrimschistBlackboxDifferentialVisual(VisualizationContext context, CrimschistBlackboxDifferentialBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        rotatingModel = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(CAAllPartialModels.SHAFTLESS_LARGE_CRIMSCHIST_COGWHEEL))
                .createInstance();
        rotatingModel.setup(blockEntity, rotationAxis(), blockEntity.portSpeed(CrimschistBlackboxDifferentialBlock.Port.COG))
                .setPosition(getVisualPosition())
                .rotateToFace(rotationAxis())
                .setChanged();

        RotatingInstance topShaft = null;
        RotatingInstance bottomShaft = null;

        Block block = blockState.getBlock();
        if (block instanceof IRotate def) {
            for (Direction direction : Iterate.directionsInAxis(rotationAxis())) {
                if (!def.hasShaftTowards(blockEntity.getLevel(), blockEntity.getBlockPos(), blockState, direction))
                    continue;

                RotatingInstance instance = instancerProvider()
                        .instancer(AllInstanceTypes.ROTATING, Models.partial(CAAllPartialModels.CRIMSCHIST_SHAFT_HALF))
                        .createInstance();
                instance.setup(blockEntity, rotationAxis(),
                                blockEntity.portSpeed(CrimschistBlackboxDifferentialBlock.portForFace(blockState, direction)))
                        .setPosition(getVisualPosition())
                        .rotateToFace(Direction.SOUTH, direction)
                        .setRotationOffset(BracketedOCKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos))
                        .setChanged();

                if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE)
                    topShaft = instance;
                else
                    bottomShaft = instance;
            }
        }

        rotatingTopShaft = topShaft;
        rotatingBottomShaft = bottomShaft;
    }

    @Override
    public void update(float partialTick) {
        rotatingModel.setup(blockEntity, rotationAxis(), blockEntity.portSpeed(CrimschistBlackboxDifferentialBlock.Port.COG))
                .setChanged();
        if (rotatingTopShaft != null)
            rotatingTopShaft.setup(blockEntity, rotationAxis(), blockEntity.portSpeed(CrimschistBlackboxDifferentialBlock.Port.POS))
                    .setChanged();
        if (rotatingBottomShaft != null)
            rotatingBottomShaft.setup(blockEntity, rotationAxis(), blockEntity.portSpeed(CrimschistBlackboxDifferentialBlock.Port.NEG))
                    .setChanged();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(rotatingModel);
        consumer.accept(rotatingTopShaft);
        consumer.accept(rotatingBottomShaft);
    }

    @Override
    public void updateLight(float partialTick) {
        relight(rotatingModel, rotatingTopShaft, rotatingBottomShaft);
    }

    @Override
    protected void _delete() {
        rotatingModel.delete();
        if (rotatingTopShaft != null)
            rotatingTopShaft.delete();
        if (rotatingBottomShaft != null)
            rotatingBottomShaft.delete();
    }
}
