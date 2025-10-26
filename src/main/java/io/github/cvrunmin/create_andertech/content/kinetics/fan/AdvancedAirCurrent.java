package io.github.cvrunmin.create_andertech.content.kinetics.fan;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import io.github.cvrunmin.create_andertech.mixin.AirCurrentInvoker;
import io.github.cvrunmin.create_andertech.mixin.ServerGamePacketListenerAccessor;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;

public class AdvancedAirCurrent extends AirCurrent {
    private ChronenditeEncasedFanBlockEntity be;
    public AdvancedAirCurrent(ChronenditeEncasedFanBlockEntity source) {
        super(source);
        this.be = source;
    }

    @Override
    protected void tickAffectedEntities(Level world) {
        for (Iterator<Entity> iterator = caughtEntities.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            if (!entity.isAlive() || !entity.getBoundingBox()
                    .intersects(bounds) || isPlayerCreativeFlying(entity)) {
                iterator.remove();
                continue;
            }

            Vec3i flow = (pushing ? direction : direction.getOpposite()).getNormal();
            float speed = Math.abs(be.getFlowDistanceCalculatingSpeed());
            float sneakModifier = entity.isShiftKeyDown() ? 4096f : 512f;
            double entityDistance = VecHelper.alignedDistanceToFace(entity.position(), source.getAirCurrentPos(), direction);
            // entityDistanceOld should be removed eventually. Remember that entityDistanceOld cannot be 0 while entityDistance can,
            // so division by 0 must be avoided.
            double entityDistanceOld = entity.position().distanceTo(VecHelper.getCenterOf(source.getAirCurrentPos()));
            float acceleration = (float) (speed / sneakModifier / (entityDistanceOld / maxDistance));
            Vec3 previousMotion = entity.getDeltaMovement();
            float maxAcceleration = 5;

            double xIn = Mth.clamp(flow.getX() * acceleration - previousMotion.x, -maxAcceleration, maxAcceleration);
            double yIn = Mth.clamp(flow.getY() * acceleration - previousMotion.y, -maxAcceleration, maxAcceleration);
            double zIn = Mth.clamp(flow.getZ() * acceleration - previousMotion.z, -maxAcceleration, maxAcceleration);

            entity.setDeltaMovement(previousMotion.add(new Vec3(xIn, yIn, zIn).scale(1 / 8f)));
            entity.fallDistance = 0;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> AirCurrentInvoker.invokeEnableClientPlayerSound(entity, Mth.clamp(speed / 128f * .4f, 0.01f, .4f)));

            if (entity instanceof ServerPlayer)
                ((ServerGamePacketListenerAccessor)((ServerPlayer) entity).connection).setAboveGroundTickCount(0);

            FanProcessingType processingType = getTypeAt((float) entityDistance);

            if (processingType == null)
                continue;

            if (entity instanceof ItemEntity itemEntity) {
                if (world != null && world.isClientSide) {
                    processingType.spawnProcessingParticles(world, entity.position());
                    continue;
                }
                if (FanProcessing.canProcess(itemEntity, processingType))
                    if (AdvancedFanProcessingHelper.applyProcessing(itemEntity, processingType, be.getFanProcessingSpeedMultiplier())
                            && source instanceof ChronenditeEncasedFanBlockEntity fan)
                        fan.award(AllAdvancements.FAN_PROCESSING);
                continue;
            }

            if (world != null)
                processingType.affectEntity(entity, world);
        }
    }

    @Override
    public void tickAffectedHandlers() {
        for (Pair<TransportedItemStackHandlerBehaviour, FanProcessingType> pair : affectedItemHandlers) {
            TransportedItemStackHandlerBehaviour handler = pair.getKey();
            Level world = handler.getWorld();
            FanProcessingType processingType = pair.getRight();
            if (processingType == null)
                continue;

            handler.handleProcessingOnAllItems(transported -> {
                if (world.isClientSide) {
                    processingType.spawnProcessingParticles(world, handler.getWorldPositionOf(transported));
                    return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
                }
                TransportedItemStackHandlerBehaviour.TransportedResult applyProcessing = AdvancedFanProcessingHelper.applyProcessing(transported, world, processingType, be.getFanProcessingSpeedMultiplier());
                if (!applyProcessing.doesNothing() && source instanceof ChronenditeEncasedFanBlockEntity fan)
                    fan.award(AllAdvancements.FAN_PROCESSING);
                return applyProcessing;
            });
        }
    }
}
