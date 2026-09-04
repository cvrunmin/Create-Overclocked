package io.github.cvrunmin.create_andertech.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import io.github.cvrunmin.create_andertech.index.AllEntityTypes;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BlazeBurnerBlockItem.class)
public abstract class BlazeBurnerBlockItemMixin {
    @Shadow
    public abstract boolean hasCapturedBlaze();

    @Shadow
    protected abstract void spawnCaptureEffects(Level world, Vec3 vec);

    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    private void interactWithChronendicBlaze(ItemStack heldItem, Player player, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        if(!hasCapturedBlaze() && AllEntityTypes.CHRONENDIC_BLAZE.is(entity)){
            Level world = player.level();
            spawnCaptureEffects(world, entity.position());
            if (world.isClientSide) {
                cir.setReturnValue(InteractionResult.FAIL);
            }else{
                var newStack = AllBlocks.CHRONENDIC_BLAZE_BURNER.asStack();
                if(!player.isCreative()){
                    heldItem.shrink(1);
                }
                if(heldItem.isEmpty()){
                    player.setItemInHand(hand, newStack);
                }else{
                    player.getInventory().placeItemBackInInventory(newStack);
                }
                entity.discard();
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }

    @Definition(id = "by", method = "Lnet/minecraft/world/entity/EntityType;by(Lnet/minecraft/nbt/CompoundTag;)Ljava/util/Optional;")
    @Expression("? = by(?)")
    @Inject(method = "useOn", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER), cancellable = true)
    private void interactWithChronendicBlazeSpawner(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir,
                                                    @Local(name = "optionalEntity") Optional<EntityType<?>> optionalEntity){
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = world.getBlockEntity(pos);
        Player player = context.getPlayer();
        if(optionalEntity.isPresent() && AllEntityTypes.CHRONENDIC_BLAZE.get() == optionalEntity.get()){
            spawnCaptureEffects(world, VecHelper.getCenterOf(pos));
            if (world.isClientSide || player == null) {
                cir.setReturnValue(InteractionResult.SUCCESS);
            }else{
                var newStack = AllBlocks.CHRONENDIC_BLAZE_BURNER.asStack();
                ItemStack heldItem = context.getItemInHand();
                if(!player.isCreative()){
                    heldItem.shrink(1);
                }
                if(heldItem.isEmpty()){
                    InteractionHand hand = context.getHand();
                    player.setItemInHand(hand, newStack);
                }else{
                    player.getInventory().placeItemBackInInventory(newStack);
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
