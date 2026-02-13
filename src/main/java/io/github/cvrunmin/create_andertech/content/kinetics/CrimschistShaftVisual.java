package io.github.cvrunmin.create_andertech.content.kinetics;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import io.github.cvrunmin.create_andertech.index.CAAllPartialModels;

public class CrimschistShaftVisual<T extends KineticBlockEntity> extends SingleAxisRotatingVisual<T> {
    public CrimschistShaftVisual(VisualizationContext context, T blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(CAAllPartialModels.CRIMSCHIST_SHAFT));
    }
}
