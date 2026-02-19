package io.github.cvrunmin.create_andertech.content.fluids.tank;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.fluid.FluidHelper;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Arrays;
import java.util.List;

public class CrimschistFluidTankBlock extends Block implements IWrenchable, IBE<CrimschistFluidTankBlockEntity> {

    public static final BooleanProperty TOP = FluidTankBlock.TOP;
    public static final BooleanProperty BOTTOM = FluidTankBlock.BOTTOM;
    public static final EnumProperty<FluidTankBlock.Shape> SHAPE = FluidTankBlock.SHAPE;
    public static final BooleanProperty LARGE_WINDOW = BooleanProperty.create("large_window");
    public static final DirectionProperty LARGE_WINDOW_DIRECTION = DirectionProperty.create("large_window_direction", Direction.Plane.HORIZONTAL);

    public CrimschistFluidTankBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(SHAPE, FluidTankBlock.Shape.WINDOW).setValue(LARGE_WINDOW, false).setValue(LARGE_WINDOW_DIRECTION, Direction.NORTH));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }


    public static boolean isTank(BlockState state) {
        return state.getBlock() instanceof CrimschistFluidTankBlock;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        withBlockEntityDo(world, pos, CrimschistFluidTankBlockEntity::updateConnectivity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(TOP, BOTTOM, SHAPE, LARGE_WINDOW, LARGE_WINDOW_DIRECTION);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        CrimschistFluidTankBlockEntity tankAt = ConnectivityHandler.partAt(getBlockEntityType(), world, pos);
        if (tankAt == null || !tankAt.hasLevel())
            return 0;
        CrimschistFluidTankBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null || !controllerBE.isWindowOpened())
            return 0;
        return tankAt.getLuminosity();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), CrimschistFluidTankBlockEntity::toggleWindows);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pDirection == Direction.DOWN && pNeighborState.getBlock() != this)
            withBlockEntityDo(pLevel, pCurrentPos, CrimschistFluidTankBlockEntity::updateBoilerTemperature);
        return pState;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean onClient = level.isClientSide;

        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!player.isCreative())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        FluidHelper.FluidExchange exchange = null;
        CrimschistFluidTankBlockEntity be = ConnectivityHandler.partAt(getBlockEntityType(), level, pos);
        if (be == null)
            return ItemInteractionResult.FAIL;

        IFluidHandler tankCapability = level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        if (tankCapability == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        FluidStack prevFluidInTank = tankCapability.getFluidInTank(0)
                .copy();

        if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be))
            exchange = FluidHelper.FluidExchange.ITEM_TO_TANK;
        else if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be))
            exchange = FluidHelper.FluidExchange.TANK_TO_ITEM;

        if (exchange == null) {
            if (GenericItemEmptying.canItemBeEmptied(level, stack)
                    || GenericItemFilling.canItemBeFilled(level, stack))
                return ItemInteractionResult.SUCCESS;
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        SoundEvent soundevent = null;
        BlockState fluidState = null;
        FluidStack fluidInTank = tankCapability.getFluidInTank(0);

        if (exchange == FluidHelper.FluidExchange.ITEM_TO_TANK) {
            Fluid fluid = fluidInTank.getFluid();
            fluidState = fluid.defaultFluidState().createLegacyBlock();
            soundevent = FluidHelper.getEmptySound(fluidInTank);
        }

        if (exchange == FluidHelper.FluidExchange.TANK_TO_ITEM) {
            Fluid fluid = prevFluidInTank.getFluid();
            fluidState = fluid.defaultFluidState().createLegacyBlock();
            soundevent = FluidHelper.getFillSound(prevFluidInTank);
        }

        if (soundevent != null && !onClient) {
            float pitch = Mth
                    .clamp(1 - (1f * fluidInTank.getAmount() / (CrimschistFluidTankBlockEntity.getMyCapacityMultiplier() * 16)), 0, 1);
            pitch /= 1.5f;
            pitch += .5f;
            pitch += (level.random.nextFloat() - .5f) / 4f;
            level.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, pitch);
        }

        if (!FluidStack.isSameFluidSameComponents(fluidInTank, prevFluidInTank)) {
            if (be instanceof CrimschistFluidTankBlockEntity tankBe) {
                CrimschistFluidTankBlockEntity controllerBE = tankBe.getControllerBE();
                if (controllerBE != null) {
                    if (fluidState != null && onClient) {
                        BlockParticleOption blockParticleData = new BlockParticleOption(ParticleTypes.BLOCK, fluidState);
                        float fluidLevel = (float) fluidInTank.getAmount() / tankCapability.getTankCapacity(0);

                        boolean reversed = fluidInTank.getFluid().getFluidType().isLighterThanAir();
                        if (reversed)
                            fluidLevel = 1 - fluidLevel;

                        Vec3 vec = hitResult.getLocation();
                        vec = new Vec3(vec.x, controllerBE.getBlockPos().getY() + fluidLevel * (controllerBE.getHeight() - .5f) + .25f, vec.z);
                        Vec3 motion = player.position().subtract(vec).scale(1 / 20f);
                        vec = vec.add(motion);
                        level.addParticle(blockParticleData, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                        return ItemInteractionResult.SUCCESS;
                    }

                    controllerBE.sendDataImmediately();
                    controllerBE.setChanged();
                }
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof CrimschistFluidTankBlockEntity tankBE))
                return;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(tankBE);
        }
    }

    @Override
    public Class<CrimschistFluidTankBlockEntity> getBlockEntityClass() {
        return CrimschistFluidTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrimschistFluidTankBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CRIMSCHIST_FLUID_TANK.get();
    }


    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return state;
        boolean x = mirror == Mirror.FRONT_BACK;
        var newState = switch (state.getValue(SHAPE)) {
            case WINDOW_NE ->
                    state.setValue(SHAPE, x ? FluidTankBlock.Shape.WINDOW_NW : FluidTankBlock.Shape.WINDOW_SE);
            case WINDOW_NW ->
                    state.setValue(SHAPE, x ? FluidTankBlock.Shape.WINDOW_NE : FluidTankBlock.Shape.WINDOW_SW);
            case WINDOW_SE ->
                    state.setValue(SHAPE, x ? FluidTankBlock.Shape.WINDOW_SW : FluidTankBlock.Shape.WINDOW_NE);
            case WINDOW_SW ->
                    state.setValue(SHAPE, x ? FluidTankBlock.Shape.WINDOW_SE : FluidTankBlock.Shape.WINDOW_NW);
            default -> state;
        };
        newState = newState.setValue(LARGE_WINDOW_DIRECTION, mirror.mirror(state.getValue(LARGE_WINDOW_DIRECTION)));
        return newState;
    }

    private static final List<FluidTankBlock.Shape> DIRECTIONAL_WINDOWS = Arrays.asList(
            FluidTankBlock.Shape.WINDOW_NE, FluidTankBlock.Shape.WINDOW_SE, FluidTankBlock.Shape.WINDOW_SW, FluidTankBlock.Shape.WINDOW_NW
    );

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        var windowShape = state.getValue(SHAPE);
        if(DIRECTIONAL_WINDOWS.contains(windowShape)){
            int index = DIRECTIONAL_WINDOWS.indexOf(windowShape);
            int rotatedIndex = rotation.rotate(index, 4);
            state.setValue(SHAPE, DIRECTIONAL_WINDOWS.get(rotatedIndex));
        }
        state = state.setValue(LARGE_WINDOW_DIRECTION, rotation.rotate(state.getValue(LARGE_WINDOW_DIRECTION)));
        return state;
    }


    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = super.getSoundType(state, world, pos, entity);
        if (entity != null && entity.getPersistentData().contains("SilenceTankSound"))
            return FluidTankBlock.SILENCED_METAL;
        return soundType;
    }


    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return getBlockEntityOptional(worldIn, pos).map(CrimschistFluidTankBlockEntity::getControllerBE)
                .map(be -> ComparatorUtil.fractionToRedstoneLevel(be.getFillState()))
                .orElse(0);
    }

    public static void updateBoilerState(BlockState pState, Level pLevel, BlockPos tankPos) {
        BlockState tankState = pLevel.getBlockState(tankPos);
        if (!(tankState.getBlock() instanceof CrimschistFluidTankBlock tank))
            return;
        CrimschistFluidTankBlockEntity tankBE = tank.getBlockEntity(pLevel, tankPos);
        if (tankBE == null)
            return;
        CrimschistFluidTankBlockEntity controllerBE = tankBE.getControllerBE();
        if (controllerBE == null)
            return;
        controllerBE.updateBoilerState();
    }
}
