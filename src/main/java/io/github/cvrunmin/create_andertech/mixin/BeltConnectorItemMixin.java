package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import io.github.cvrunmin.create_andertech.AllBlocks;
import io.github.cvrunmin.create_andertech.content.kinetics.belt.OCBeltConnectorHelper;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.CrimschistShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltConnectorItem.class)
public class BeltConnectorItemMixin {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", shift = At.Shift.AFTER), slice = @Slice(
                from = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtUtils;writeBlockPos(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/nbt/CompoundTag;"),
                to = @At("TAIL")))
    private void createandertech$injectSaveSpeedingFlagAsNeeded(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local(name = "tag") LocalRef<CompoundTag> tag){
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(CrimschistShaftBlock.isShaft(world.getBlockState(pos))){
            var realTag = tag.get();
            realTag.putBoolean("IsSpeeding", true);
            tag.set(realTag);
        }
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;remove(Ljava/lang/String;)V"))
    private void createandertech$removeSpeedingFlagWhenNeeded(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local(name = "tag") LocalRef<CompoundTag> tag){
        var realTag = tag.get();
        if(realTag.contains("IsSpeeding")){
            realTag.remove("IsSpeeding");
            tag.set(realTag);
        }
    }

    @WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/belt/item/BeltConnectorItem;canConnect(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean createandertech$validateSpeedingIfNeeded(Level world, BlockPos firstPulley, BlockPos pos, Operation<Boolean> original, @Local(name = "tag") CompoundTag tag){
        if(tag.contains("IsSpeeding") && tag.getBoolean("IsSpeeding")) return OCBeltConnectorHelper.canConnectAsSpeedingBelt(world, firstPulley, pos);
        return OCBeltConnectorHelper.isSameTypeOfShaft(world, firstPulley, pos) && original.call(world, firstPulley, pos);
    }

    @Inject(method = "createBelts", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;getDefaultState()Lnet/minecraft/world/level/block/state/BlockState;"))
    private static void createandertech$replaceWithSpeedingBeltState(Level world, BlockPos start, BlockPos end, CallbackInfo ci, @Local(name = "beltBlock") LocalRef<BlockState> beltBlock, @Share("speeding")LocalBooleanRef speeding){
        if(CrimschistShaftBlock.isShaft(world.getBlockState(start))){
            beltBlock.set(AllBlocks.OC_BELT.getDefaultState());
            speeding.set(true);
        }
    }

    @Inject(method = "createBelts", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ShaftBlock;isShaft(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static void createandertech$checkCrimschistShaftInstead(Level world, BlockPos start, BlockPos end, CallbackInfo cinfo, @Local(name = "shaftState") BlockState shaftState, @Local(name = "pulley") LocalBooleanRef pulley, @Share("speeding") LocalBooleanRef speeding){
        if(speeding.get()){
            pulley.set(CrimschistShaftBlock.isShaft(shaftState));
        }
    }

    @ModifyExpressionValue(method = "createBelts", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean createandertech$checkExistingBelts(boolean original, Level world, BlockPos start, BlockPos end, @Local(name = "pos") BlockPos pos){
        return original || AllBlocks.OC_BELT.has(world.getBlockState(pos));
    }

    @ModifyExpressionValue(method = "validateAxis", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/simpleRelays/ShaftBlock;isShaft(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean injectValidateAxis(boolean original, Level world, BlockPos pos){
        return original || CrimschistShaftBlock.isShaft(world.getBlockState(pos));
    }
}
