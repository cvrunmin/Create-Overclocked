package io.github.cvrunmin.create_andertech.content.kinetics.fan;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class AdvancedFanProcessingHelper {
    public static boolean applyProcessing(ItemEntity entity, FanProcessingType type, int speedMultiplier) {
        if (decrementProcessingTime(entity, type, speedMultiplier) != 0)
            return false;
        List<ItemStack> stacks = type.process(entity.getItem(), entity.level());
        if (stacks == null)
            return false;
        if (stacks.isEmpty()) {
            entity.discard();
            return false;
        }
        entity.setItem(stacks.remove(0));
        for (ItemStack additional : stacks) {
            ItemEntity entityIn = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), additional);
            entityIn.setDeltaMovement(entity.getDeltaMovement());
            entity.level().addFreshEntity(entityIn);
        }
        return true;
    }

    public static TransportedItemStackHandlerBehaviour.TransportedResult applyProcessing(TransportedItemStack transported, Level world, FanProcessingType type, int speedMultiplier) {
        speedMultiplier = Math.max(1, speedMultiplier);
        TransportedItemStackHandlerBehaviour.TransportedResult ignore = TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
        if (transported.processedBy != type) {
            transported.processedBy = type;
            int timeModifierForStackSize = ((transported.stack.getCount() - 1) / 16) + 1;
            int processingTime =
                    (int) (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1;
            transported.processingTime = processingTime;
            if (!type.canProcess(transported.stack, world))
                transported.processingTime = -1;
            return ignore;
        }
        if (transported.processingTime == -1)
            return ignore;
        int oldProcessingTime = transported.processingTime;
        if(oldProcessingTime == 0){
            transported.processingTime = -1;
        }else{
            transported.processingTime = Math.max(transported.processingTime - speedMultiplier, 0);
        }
        if (oldProcessingTime > 0)
            return ignore;

        List<ItemStack> stacks = type.process(transported.stack, world);
        if (stacks == null)
            return ignore;

        List<TransportedItemStack> transportedStacks = new ArrayList<>();
        for (ItemStack additional : stacks) {
            TransportedItemStack newTransported = transported.getSimilar();
            newTransported.stack = additional.copy();
            transportedStacks.add(newTransported);
        }
        return TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(transportedStacks);
    }

    private static int decrementProcessingTime(ItemEntity entity, FanProcessingType type, int speedMultiplier) {
        speedMultiplier = Math.max(1, speedMultiplier);
        CompoundTag nbt = entity.getPersistentData();

        if (!nbt.contains("CreateData"))
            nbt.put("CreateData", new CompoundTag());
        CompoundTag createData = nbt.getCompound("CreateData");

        if (!createData.contains("Processing"))
            createData.put("Processing", new CompoundTag());
        CompoundTag processing = createData.getCompound("Processing");

        if (!processing.contains("Type") || AllFanProcessingTypes.parseLegacy(processing.getString("Type")) != type) {
            ResourceLocation key = CreateBuiltInRegistries.FAN_PROCESSING_TYPE.getKey(type);
            if (key == null)
                throw new IllegalArgumentException("Could not get id for FanProcessingType " + type + "!");

            processing.putString("Type", key.toString());
            int timeModifierForStackSize = ((entity.getItem()
                    .getCount() - 1) / 16) + 1;
            int processingTime =
                    (int) (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1;
            processing.putInt("Time", processingTime);
        }
        int value;
        if(processing.contains("Time") && processing.getInt("Time") == 0){
            value = -1;
        }else{
            value = Math.max(0, processing.getInt("Time") - speedMultiplier);
        }
        processing.putInt("Time", value);
        return value;
    }
}
