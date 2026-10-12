package io.github.cvrunmin.create_andertech.content.contraptions.actors.harvester;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.farmersdelight.FarmersDelightCompat;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.CropBlockAccessor;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import io.github.cvrunmin.create_andertech.api.CrimschistHarvesterBehavioursAPI;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SpecialPlantable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public class CrimschistHarvesterMovementBehaviour implements MovementBehaviour {
    @Override
    public boolean isActive(MovementContext context) {
        Direction attachedFace = context.state.getValue(CrimschistHarvesterBlock.ATTACHED_FACE);
        return MovementBehaviour.super.isActive(context) && !VecHelper.isVecPointingTowards(context.relativeMotion, attachedFace);
    }

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        Direction attachedFace = context.state.getValue(CrimschistHarvesterBlock.ATTACHED_FACE);
        Direction nearest = Direction.getNearest(context.relativeMotion.normalize());
        if(nearest == attachedFace) return Vec3.ZERO;
        return Vec3.atLowerCornerOf(nearest.getNormal()).scale(0.45);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Level world = context.world;
        if (world.isClientSide)
            return;

        BlockState stateVisited = world.getBlockState(pos);
        if (stateVisited.isAir() || AllTags.AllBlockTags.NON_HARVESTABLE.matches(stateVisited))
            return;
        CrimschistHarvesterBehavioursAPI.tryHarvest(world, pos, stateVisited, CrimschistHarvesterBehavioursAPI.movementBehaviourContext(this, context));
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!VisualizationManager.supportsVisualization(context.world))
            CrimschistHarvesterRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }

    @Override
    public @Nullable ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return new CrimschistHarvesterActorVisual(visualizationContext, simulationWorld, movementContext);
    }
}
