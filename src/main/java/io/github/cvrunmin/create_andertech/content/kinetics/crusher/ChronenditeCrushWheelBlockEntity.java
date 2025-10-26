package io.github.cvrunmin.create_andertech.content.kinetics.crusher;

import com.simibubi.create.AllDamageTypes;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays.OCKineticBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ChronenditeCrushWheelBlockEntity extends OCKineticBlockEntity {
    public ChronenditeCrushWheelBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        setLazyTickRate(20);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        registerAwardables(behaviours, AllAdvancements.CRUSHING_WHEEL, AllAdvancements.CRUSHER_MAXED);
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        fixControllers();
    }

    public void fixControllers() {
        if(!(getBlockState().getBlock() instanceof ChronenditeCrushWheelBlock)) return;
        for (Direction d : Iterate.directions)
            ((ChronenditeCrushWheelBlock) getBlockState().getBlock()).updateControllers(getBlockState(), getLevel(), getBlockPos(),
                    d);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        fixControllers();
    }

    // This increases the drops when dropCustomDeathLoot is called, and LootingEnchantFunctionMixin increases the drops
    // defined in the entity loot table
    @SubscribeEvent
    public static void crushingIsFortunate(LootingLevelEvent event) {
        DamageSource damageSource = event.getDamageSource();
        if (damageSource == null || !damageSource.is(AllDamageTypes.CRUSH))
            return;
        event.setLootingLevel(4);
    }

    @SubscribeEvent
    public static void handleCrushedMobDrops(LivingDropsEvent event) {
        DamageSource damageSource = event.getSource();
        if (damageSource == null || !damageSource.is(AllDamageTypes.CRUSH))
            return;
        Vec3 outSpeed = Vec3.ZERO;
        for (ItemEntity outputItem : event.getDrops()) {
            outputItem.setDeltaMovement(outSpeed);
        }
    }
}
