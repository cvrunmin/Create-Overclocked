package io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.encased;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.BracketedOCKineticBlockEntityRenderer;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.OCKineticBlockEntity;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.SimpleOCKineticBlockEntity;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EncasedCrimschistCogVisual extends KineticBlockEntityVisual<SimpleOCKineticBlockEntity> {
    private final boolean large;

    protected final RotatingInstance rotatingModel;
    @Nullable
    protected final RotatingInstance rotatingTopShaft;
    @Nullable
    protected final RotatingInstance rotatingBottomShaft;

    public static EncasedCrimschistCogVisual small(VisualizationContext modelManager, SimpleOCKineticBlockEntity blockEntity, float partialTick) {
        return new EncasedCrimschistCogVisual(modelManager, blockEntity, false, partialTick, Models.partial(CAAllPartialModels.SHAFTLESS_CRIMSCHIST_COGWHEEL));
    }

    public static EncasedCrimschistCogVisual large(VisualizationContext modelManager, SimpleOCKineticBlockEntity blockEntity, float partialTick) {
        return new EncasedCrimschistCogVisual(modelManager, blockEntity, true, partialTick, Models.partial(CAAllPartialModels.SHAFTLESS_LARGE_CRIMSCHIST_COGWHEEL));
    }

    public EncasedCrimschistCogVisual(VisualizationContext modelManager, SimpleOCKineticBlockEntity blockEntity, boolean large, float partialTick, Model model) {
        super(modelManager, blockEntity, partialTick);
        this.large = large;

        rotatingModel = instancerProvider().instancer(AllInstanceTypes.ROTATING, model)
                .createInstance();

        rotatingModel.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(rotationAxis())
                .setChanged();

        RotatingInstance rotatingTopShaft = null;
        RotatingInstance rotatingBottomShaft = null;

        Block block = blockState.getBlock();
        if (block instanceof IRotate def) {
            for (Direction d : Iterate.directionsInAxis(rotationAxis())) {
                if (!def.hasShaftTowards(blockEntity.getLevel(), blockEntity.getBlockPos(), blockState, d))
                    continue;
                RotatingInstance instance = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(CAAllPartialModels.CRIMSCHIST_SHAFT_HALF))
                        .createInstance();
                instance.setup(blockEntity)
                        .setPosition(getVisualPosition())
                        .rotateToFace(Direction.SOUTH, d)
                        .setChanged();

                if (large) {
                    instance.setRotationOffset(BracketedOCKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos));
                }

                if (d.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    rotatingTopShaft = instance;
                } else {
                    rotatingBottomShaft = instance;
                }
            }
        }

        this.rotatingTopShaft = rotatingTopShaft;
        this.rotatingBottomShaft = rotatingBottomShaft;
    }

    @Override
    public void update(float partialTick) {
        rotatingModel.setup(blockEntity)
                .setChanged();
        if (rotatingTopShaft != null) rotatingTopShaft.setup(blockEntity)
                .setChanged();
        if (rotatingBottomShaft != null) rotatingBottomShaft.setup(blockEntity)
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
        if (rotatingTopShaft != null) rotatingTopShaft.delete();
        if (rotatingBottomShaft != null) rotatingBottomShaft.delete();
    }
}
