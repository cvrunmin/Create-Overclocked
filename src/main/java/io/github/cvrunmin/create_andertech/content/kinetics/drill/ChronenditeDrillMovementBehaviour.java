package io.github.cvrunmin.create_andertech.content.kinetics.drill;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import javax.annotation.Nullable;

public class ChronenditeDrillMovementBehaviour extends DrillMovementBehaviour {

    @Nullable
    @Override
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return new ChronenditeDrillActorVisual(visualizationContext, simulationWorld, movementContext);
    }
}
