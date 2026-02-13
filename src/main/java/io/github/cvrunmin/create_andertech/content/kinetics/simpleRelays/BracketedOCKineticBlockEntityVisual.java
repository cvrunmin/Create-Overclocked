package io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class BracketedOCKineticBlockEntityVisual {
    public static BlockEntityVisual<BracketedOCKineticBlockEntity> create(VisualizationContext context, BracketedOCKineticBlockEntity blockEntity, float partialTick) {
        if (ICogWheel.isLargeCog(blockEntity.getBlockState())) {
            return new BracketedOCKineticBlockEntityVisual.LargeCogVisual(context, blockEntity, partialTick);
        } else {
            Model model;
            if (AllBlocks.CRIMSCHIST_COGWHEEL.is(blockEntity.getBlockState().getBlock())) {
                model = Models.partial(CAAllPartialModels.CRIMSCHIST_COGWHEEL);
            } else {
                model = Models.partial(CAAllPartialModels.CRIMSCHIST_SHAFT);
            }
            return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, model);
        }
    }

    // Large cogs sometimes have to offset their teeth by 11.25 degrees in order to
    // mesh properly
    public static class LargeCogVisual extends SingleAxisRotatingVisual<BracketedOCKineticBlockEntity> {

        protected final RotatingInstance additionalShaft;

        private LargeCogVisual(VisualizationContext context, BracketedOCKineticBlockEntity blockEntity, float partialTick) {
            super(context, blockEntity, partialTick, Models.partial(CAAllPartialModels.SHAFTLESS_LARGE_CRIMSCHIST_COGWHEEL));

            Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);

            additionalShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(CAAllPartialModels.CRIMSCHIST_COGWHEEL_SHAFT))
                    .createInstance();

            additionalShaft.rotateToFace(axis)
                    .setup(blockEntity)
                    .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos))
                    .setPosition(getVisualPosition())
                    .setChanged();
        }

        @Override
        public void update(float pt) {
            super.update(pt);
            additionalShaft.setup(blockEntity)
                    .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos))
                    .setChanged();
        }

        @Override
        public void updateLight(float partialTick) {
            super.updateLight(partialTick);
            relight(additionalShaft);
        }

        @Override
        protected void _delete() {
            super._delete();
            additionalShaft.delete();
        }

        @Override
        public void collectCrumblingInstances(Consumer<Instance> consumer) {
            super.collectCrumblingInstances(consumer);
            consumer.accept(additionalShaft);
        }
    }
}
