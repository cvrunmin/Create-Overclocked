package io.github.cvrunmin.create_andertech.content.kinetics.belt;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.*;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import com.simibubi.create.foundation.item.ItemHelper;
import io.github.cvrunmin.create_andertech.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.AllBlocks;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.*;

public class OCBeltBlock extends HorizontalKineticBlock
        implements IBE<OCBeltBlockEntity>, SpecialBlockItemRequirement, TransformableBlock, ProperWaterloggedBlock {
//    public static final Property<BeltSlope> SLOPE = EnumProperty.create("slope", BeltSlope.class);
//    public static final Property<BeltPart> PART = EnumProperty.create("part", BeltPart.class);
//    public static final BooleanProperty CASING = BooleanProperty.create("casing");
    public static final Property<BeltSlope> SLOPE = BeltBlock.SLOPE;
    public static final Property<BeltPart> PART = BeltBlock.PART;
    public static final BooleanProperty CASING = BeltBlock.CASING;

    public OCBeltBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SLOPE, BeltSlope.HORIZONTAL)
                .setValue(PART, BeltPart.START)
                .setValue(CASING, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return super.areStatesKineticallyEquivalent(oldState, newState)
                && oldState.getValue(PART) == newState.getValue(PART);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if (face.getAxis() != getRotationAxis(state))
            return false;
        return getBlockEntityOptional(world, pos).map(OCBeltBlockEntity::hasPulley)
                .orElse(false);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        if (state.getValue(SLOPE) == BeltSlope.SIDEWAYS)
            return Direction.Axis.Y;
        return state.getValue(HORIZONTAL_FACING)
                .getClockWise()
                .getAxis();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader world, BlockPos pos,
                                       Player player) {
        return AllItems.BELT_CONNECTOR.asStack();
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof OCBeltBlockEntity && ((OCBeltBlockEntity) blockEntity).hasPulley())
            drops.addAll(AllBlocks.CRIMSCHIST_SHAFT.getDefaultState()
                    .getDrops(builder));
        return drops;
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel worldIn, BlockPos pos, ItemStack p_220062_4_, boolean b) {
        BeltBlockEntity controllerBE = BeltHelper.getControllerBE(worldIn, pos);
        if (controllerBE != null)
            controllerBE.getInventory()
                    .ejectAll();
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return false;
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        BlockPos entityPosition = entityIn.blockPosition();
        BlockPos beltPos = null;

        if (AllBlocks.OC_BELT.has(worldIn.getBlockState(entityPosition)))
            beltPos = entityPosition;
        else if (AllBlocks.OC_BELT.has(worldIn.getBlockState(entityPosition.below())))
            beltPos = entityPosition.below();
        if (beltPos == null)
            return;
        if (!(worldIn instanceof Level))
            return;

        entityInside(worldIn.getBlockState(beltPos), (Level) worldIn, beltPos, entityIn);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (!canTransportObjects(state))
            return;
        if (entityIn instanceof Player player) {
            if (player.isShiftKeyDown() && !AllItems.CARDBOARD_BOOTS.isIn(player.getItemBySlot(EquipmentSlot.FEET)))
                return;
            if (player.getAbilities().flying)
                return;
        }

        if (DivingBootsItem.isWornBy(entityIn))
            return;

        BeltBlockEntity belt = BeltHelper.getSegmentBE(worldIn, pos);
        if (belt == null)
            return;
        if(!(belt instanceof OCBeltBlockEntity)){
            return;
        }
        ItemStack asItem = ItemHelper.fromItemEntity(entityIn);
        if (!asItem.isEmpty()) {
            if (worldIn.isClientSide)
                return;
            if (entityIn.getDeltaMovement().y > 0)
                return;
            Vec3 targetLocation = VecHelper.getCenterOf(pos)
                    .add(0, 5 / 16f, 0);
            if (!PackageEntity.centerPackage(entityIn, targetLocation))
                return;
            if (BeltTunnelInteractionHandler.getTunnelOnPosition(worldIn, pos) != null)
                return;
            withBlockEntityDo(worldIn, pos, be -> {
                IItemHandler handler = worldIn.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, be, null);
                if (handler == null)
                    return;
                ItemStack remainder = handler.insertItem(0, asItem, false);
                if (remainder.isEmpty())
                    entityIn.discard();
                else if (entityIn instanceof ItemEntity itemEntity && remainder.getCount() != itemEntity.getItem().getCount())
                    itemEntity.setItem(remainder);
            });
            return;
        }

        BeltBlockEntity controller = BeltHelper.getControllerBE(worldIn, pos);
        if (controller == null || controller.passengers == null)
            return;
        if(!(controller instanceof OCBeltBlockEntity)) return;
        if (controller.passengers.containsKey(entityIn)) {
            BeltMovementHandler.TransportedEntityInfo info = controller.passengers.get(entityIn);
            if (info.getTicksSinceLastCollision() != 0 || pos.equals(entityIn.blockPosition()))
                info.refresh(pos, state);
        } else {
            controller.passengers.put(entityIn, new BeltMovementHandler.TransportedEntityInfo(pos, state));
            entityIn.setOnGround(true);
        }
    }

    public static boolean canTransportObjects(BlockState state) {
        if (!AllBlocks.OC_BELT.has(state))
            return false;
        BeltSlope slope = state.getValue(SLOPE);
        return slope != BeltSlope.VERTICAL && slope != BeltSlope.SIDEWAYS;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn,
                                           BlockHitResult hit) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        boolean isWrench = AllItems.WRENCH.isIn(stack);
        boolean isConnector = AllItems.BELT_CONNECTOR.isIn(stack);
        boolean isShaft = AllBlocks.CRIMSCHIST_SHAFT.isIn(stack);
        boolean isDye = stack.is(Tags.Items.DYES);
        boolean hasWater = GenericItemEmptying.emptyItem(world, stack, true)
                .getFirst()
                .getFluid()
                .isSame(Fluids.WATER);
        boolean isHand = stack.isEmpty() && handIn == InteractionHand.MAIN_HAND;

        if (isDye || hasWater)
            return onBlockEntityUseItemOn(world, pos,
                    be -> be.applyColor(DyeColor.getColor(stack)) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);

        if (isConnector)
            return OCBeltSlicer.useConnector(state, world, pos, player, handIn, hit, new BeltSlicer.Feedback());
        if (isWrench)
            return OCBeltSlicer.useWrench(state, world, pos, player, handIn, hit, new BeltSlicer.Feedback());

        BeltBlockEntity belt = BeltHelper.getSegmentBE(world, pos);
        if (belt == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(!(belt instanceof OCBeltBlockEntity))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (PackageItem.isPackage(stack)) {
            ItemStack toInsert = stack.copy();
            IItemHandler handler = world.getCapability(Capabilities.ItemHandler.BLOCK, belt.getBlockPos(), null);
            if (handler == null)
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            ItemStack remainder = handler.insertItem(0, toInsert, false);
            if (remainder.isEmpty()) {
                stack.shrink(1);
                return ItemInteractionResult.SUCCESS;
            }
        }

        if (isHand) {
            BeltBlockEntity controllerBelt = belt.getControllerBE();
            if (controllerBelt == null)
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            //noinspection ConstantValue
            if(!(controllerBelt instanceof OCBeltBlockEntity))
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (world.isClientSide)
                return ItemInteractionResult.SUCCESS;
            MutableBoolean success = new MutableBoolean(false);
            controllerBelt.getInventory()
                    .applyToEachWithin(belt.index + .5f, .55f, (transportedItemStack) -> {
                        player.getInventory()
                                .placeItemBackInInventory(transportedItemStack.stack);
                        success.setTrue();
                        return TransportedItemStackHandlerBehaviour.TransportedResult.removeItem();
                    });
            if (success.isTrue())
                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                        1f + world.random.nextFloat());
        }

        if (isShaft) {
            if (state.getValue(PART) != BeltPart.MIDDLE)
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (world.isClientSide)
                return ItemInteractionResult.SUCCESS;
            if (!player.isCreative())
                stack.shrink(1);
            KineticBlockEntity.switchToBlockState(world, pos, state.setValue(PART, BeltPart.PULLEY));
            return ItemInteractionResult.SUCCESS;
        }

        if (com.simibubi.create.AllBlocks.BRASS_CASING.isIn(stack)) {
            withBlockEntityDo(world, pos, be -> be.setCasingType(BeltBlockEntity.CasingType.BRASS));
            updateCoverProperty(world, pos, world.getBlockState(pos));

            SoundType soundType = com.simibubi.create.AllBlocks.BRASS_CASING.getDefaultState()
                    .getSoundType(world, pos, player);
            world.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

            return ItemInteractionResult.SUCCESS;
        }

        if (com.simibubi.create.AllBlocks.ANDESITE_CASING.isIn(stack)) {
            withBlockEntityDo(world, pos, be -> be.setCasingType(BeltBlockEntity.CasingType.ANDESITE));
            updateCoverProperty(world, pos, world.getBlockState(pos));

            SoundType soundType = com.simibubi.create.AllBlocks.ANDESITE_CASING.getDefaultState()
                    .getSoundType(world, pos, player);
            world.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if (state.getValue(CASING)) {
            if (world.isClientSide)
                return InteractionResult.SUCCESS;
            withBlockEntityDo(world, pos, be -> be.setCasingType(BeltBlockEntity.CasingType.NONE));
            return InteractionResult.SUCCESS;
        }

        if (state.getValue(PART) == BeltPart.PULLEY) {
            if (world.isClientSide)
                return InteractionResult.SUCCESS;
            KineticBlockEntity.switchToBlockState(world, pos, state.setValue(PART, BeltPart.MIDDLE));
            if (player != null && !player.isCreative())
                player.getInventory()
                        .placeItemBackInInventory(AllBlocks.CRIMSCHIST_SHAFT.asStack());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SLOPE, PART, CASING, WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        return PathType.RAIL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return OCBeltShapes.getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getBlock() != this)
            return Shapes.empty();

        VoxelShape shape = getShape(state, worldIn, pos, context);
        if (!(context instanceof EntityCollisionContext))
            return shape;

        return getBlockEntityOptional(worldIn, pos).map(be -> {
                    Entity entity = ((EntityCollisionContext) context).getEntity();
                    if (entity == null)
                        return shape;

                    BeltBlockEntity controller = be.getControllerBE();
                    if (controller == null)
                        return shape;
                    if (controller.passengers == null || !controller.passengers.containsKey(entity))
                        return OCBeltShapes.getCollisionShape(state);
                    return shape;

                })
                .orElse(shape);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(CASING) ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public static void initBelt(Level world, BlockPos pos) {
        if (world.isClientSide)
            return;
        if (world instanceof ServerLevel && ((ServerLevel) world).getChunkSource()
                .getGenerator() instanceof DebugLevelSource)
            return;

        BlockState state = world.getBlockState(pos);
        if (!AllBlocks.OC_BELT.has(state))
            return;
        // Find controller
        int limit = 1000;
        BlockPos currentPos = pos;
        while (limit-- > 0) {
            BlockState currentState = world.getBlockState(currentPos);
            if (!AllBlocks.OC_BELT.has(currentState)) {
                world.destroyBlock(pos, true);
                return;
            }
            BlockPos nextSegmentPosition = nextSegmentPosition(currentState, currentPos, false);
            if (nextSegmentPosition == null)
                break;
            if (!world.isLoaded(nextSegmentPosition))
                return;
            currentPos = nextSegmentPosition;
        }

        // Init belts
        int index = 0;
        List<BlockPos> beltChain = getBeltChain(world, currentPos);
        if (beltChain.size() < 2) {
            world.destroyBlock(currentPos, true);
            return;
        }

        for (BlockPos beltPos : beltChain) {
            BlockEntity blockEntity = world.getBlockEntity(beltPos);
            BlockState currentState = world.getBlockState(beltPos);

            if (blockEntity instanceof OCBeltBlockEntity be && AllBlocks.OC_BELT.has(currentState)) {
                be.setController(currentPos);
                be.beltLength = beltChain.size();
                be.index = index;
                be.attachKinetics();
                be.setChanged();
                be.sendData();

                if (be.isController() && !canTransportObjects(currentState))
                    be.getInventory()
                            .ejectAll();
            } else {
                world.destroyBlock(currentPos, true);
                return;
            }
            index++;
        }

    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);

        if (world.isClientSide)
            return;
        if (state.getBlock() == newState.getBlock())
            return;
        if (isMoving)
            return;

        // Destroy chain
        for (boolean forward : Iterate.trueAndFalse) {
            BlockPos currentPos = nextSegmentPosition(state, pos, forward);
            if (currentPos == null)
                continue;
            BlockState currentState = world.getBlockState(currentPos);
            if (!AllBlocks.OC_BELT.has(currentState))
                continue;

            boolean hasPulley = false;
            BlockEntity blockEntity = world.getBlockEntity(currentPos);
            if (blockEntity instanceof OCBeltBlockEntity belt) {
                if (belt.isController())
                    belt.getInventory()
                            .ejectAll();

                hasPulley = belt.hasPulley();
            }

            world.removeBlockEntity(currentPos);
            BlockState shaftState = AllBlocks.CRIMSCHIST_SHAFT.getDefaultState()
                    .setValue(BlockStateProperties.AXIS, getRotationAxis(currentState));
            world.setBlock(currentPos, ProperWaterloggedBlock.withWater(world,
                    hasPulley ? shaftState : Blocks.AIR.defaultBlockState(), currentPos), 3);
            world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, currentPos, Block.getId(currentState));
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction side, BlockState p_196271_3_, LevelAccessor world,
                                  BlockPos pos, BlockPos p_196271_6_) {
        updateWater(world, state, pos);
        if (side.getAxis()
                .isHorizontal())
            updateTunnelConnections(world, pos.above());
        if (side == Direction.UP)
            updateCoverProperty(world, pos, state);
        return state;
    }

    public void updateCoverProperty(LevelAccessor world, BlockPos pos, BlockState state) {
        if (world.isClientSide())
            return;
        if (state.getValue(CASING) && state.getValue(SLOPE) == BeltSlope.HORIZONTAL)
            withBlockEntityDo(world, pos, bbe -> bbe.setCovered(isBlockCoveringBelt(world, pos.above())));
    }

    public static boolean isBlockCoveringBelt(LevelAccessor world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        VoxelShape collisionShape = blockState.getCollisionShape(world, pos);
        if (collisionShape.isEmpty())
            return false;
        AABB bounds = collisionShape.bounds();
        if (bounds.getXsize() < .5f || bounds.getZsize() < .5f)
            return false;
        if (bounds.minY > 0)
            return false;
        if (com.simibubi.create.AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(blockState))
            return false;
        if (AllBlocks.CHRONENDITE_CRUSHING_WHEEL_CONTROLLER.has(blockState))
            return false;
        if (FunnelBlock.isFunnel(blockState) && FunnelBlock.getFunnelFacing(blockState) != Direction.UP)
            return false;
        if (blockState.getBlock() instanceof BeltTunnelBlock)
            return false;
        return true;
    }

    private void updateTunnelConnections(LevelAccessor world, BlockPos pos) {
        Block tunnelBlock = world.getBlockState(pos)
                .getBlock();
        if (tunnelBlock instanceof BeltTunnelBlock)
            ((BeltTunnelBlock) tunnelBlock).updateTunnel(world, pos);
    }

    public static List<BlockPos> getBeltChain(LevelAccessor world, BlockPos controllerPos) {
        List<BlockPos> positions = new LinkedList<>();

        BlockState blockState = world.getBlockState(controllerPos);
        if (!AllBlocks.OC_BELT.has(blockState))
            return positions;

        int limit = 1000;
        BlockPos current = controllerPos;
        while (limit-- > 0 && current != null) {
            BlockState state = world.getBlockState(current);
            if (!AllBlocks.OC_BELT.has(state))
                break;
            positions.add(current);
            current = nextSegmentPosition(state, current, true);
        }

        return positions;
    }

    public static BlockPos nextSegmentPosition(BlockState state, BlockPos pos, boolean forward) {
        Direction direction = state.getValue(HORIZONTAL_FACING);
        BeltSlope slope = state.getValue(SLOPE);
        BeltPart part = state.getValue(PART);

        int offset = forward ? 1 : -1;

        if (part == BeltPart.END && forward || part == BeltPart.START && !forward)
            return null;
        if (slope == BeltSlope.VERTICAL)
            return pos.above(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? offset : -offset);
        pos = pos.relative(direction, offset);
        if (slope != BeltSlope.HORIZONTAL && slope != BeltSlope.SIDEWAYS)
            return pos.above(slope == BeltSlope.UPWARD ? offset : -offset);
        return pos;
    }

    @Override
    public Class<OCBeltBlockEntity> getBlockEntityClass() {
        return OCBeltBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OCBeltBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.OC_BELT.get();
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        List<ItemStack> required = new ArrayList<>();
        if (state.getValue(PART) != BeltPart.MIDDLE)
            required.add(AllBlocks.CRIMSCHIST_SHAFT.asStack());
        if (state.getValue(PART) == BeltPart.START)
            required.add(AllItems.BELT_CONNECTOR.asStack());
        if (required.isEmpty())
            return ItemRequirement.NONE;
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, required);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockState rotate = super.rotate(state, rot);

        if (state.getValue(SLOPE) != BeltSlope.VERTICAL)
            return rotate;
        if (state.getValue(HORIZONTAL_FACING)
                .getAxisDirection() != rotate.getValue(HORIZONTAL_FACING)
                .getAxisDirection()) {
            if (state.getValue(PART) == BeltPart.START)
                return rotate.setValue(PART, BeltPart.END);
            if (state.getValue(PART) == BeltPart.END)
                return rotate.setValue(PART, BeltPart.START);
        }

        return rotate;
    }

    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = mirror(state, transform.mirror);
        }

        if (transform.rotationAxis == Direction.Axis.Y) {
            return rotate(state, transform.rotation);
        }
        return transformInner(state, transform);
    }

    protected BlockState transformInner(BlockState state, StructureTransform transform) {
        boolean halfTurn = transform.rotation == Rotation.CLOCKWISE_180;

        Direction initialDirection = state.getValue(HORIZONTAL_FACING);
        boolean diagonal =
                state.getValue(SLOPE) == BeltSlope.DOWNWARD || state.getValue(SLOPE) == BeltSlope.UPWARD;

        if (!diagonal) {
            for (int i = 0; i < transform.rotation.ordinal(); i++) {
                Direction direction = state.getValue(HORIZONTAL_FACING);
                BeltSlope slope = state.getValue(SLOPE);
                boolean vertical = slope == BeltSlope.VERTICAL;
                boolean horizontal = slope == BeltSlope.HORIZONTAL;
                boolean sideways = slope == BeltSlope.SIDEWAYS;

                Direction newDirection = direction.getOpposite();
                BeltSlope newSlope = BeltSlope.VERTICAL;

                if (vertical) {
                    if (direction.getAxis() == transform.rotationAxis) {
                        newDirection = direction.getCounterClockWise();
                        newSlope = BeltSlope.SIDEWAYS;
                    } else {
                        newSlope = BeltSlope.HORIZONTAL;
                        newDirection = direction;
                        if (direction.getAxis() == Direction.Axis.Z)
                            newDirection = direction.getOpposite();
                    }
                }

                if (sideways) {
                    newDirection = direction;
                    if (direction.getAxis() == transform.rotationAxis)
                        newSlope = BeltSlope.HORIZONTAL;
                    else
                        newDirection = direction.getCounterClockWise();
                }

                if (horizontal) {
                    newDirection = direction;
                    if (direction.getAxis() == transform.rotationAxis)
                        newSlope = BeltSlope.SIDEWAYS;
                    else if (direction.getAxis() != Direction.Axis.Z)
                        newDirection = direction.getOpposite();
                }

                state = state.setValue(HORIZONTAL_FACING, newDirection);
                state = state.setValue(SLOPE, newSlope);
            }

        } else if (initialDirection.getAxis() != transform.rotationAxis) {
            for (int i = 0; i < transform.rotation.ordinal(); i++) {
                Direction direction = state.getValue(HORIZONTAL_FACING);
                Direction newDirection = direction.getOpposite();
                BeltSlope slope = state.getValue(SLOPE);
                boolean upward = slope == BeltSlope.UPWARD;
                boolean downward = slope == BeltSlope.DOWNWARD;

                // Rotate diagonal
                if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ^ downward ^ direction.getAxis() == Direction.Axis.Z) {
                    state = state.setValue(SLOPE, upward ? BeltSlope.DOWNWARD : BeltSlope.UPWARD);
                } else {
                    state = state.setValue(HORIZONTAL_FACING, newDirection);
                }
            }

        } else if (halfTurn) {
            Direction direction = state.getValue(HORIZONTAL_FACING);
            Direction newDirection = direction.getOpposite();
            BeltSlope slope = state.getValue(SLOPE);
            boolean vertical = slope == BeltSlope.VERTICAL;

            if (diagonal) {
                state = state.setValue(SLOPE, slope == BeltSlope.UPWARD ? BeltSlope.DOWNWARD
                        : slope == BeltSlope.DOWNWARD ? BeltSlope.UPWARD : slope);
            } else if (vertical) {
                state = state.setValue(HORIZONTAL_FACING, newDirection);
            }
        }

        return state;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return fluidState(pState);
    }

    public static class RenderProperties extends ReducedDestroyEffects implements MultiPosDestructionHandler {
        @Override
        public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof OCBeltBlockEntity belt) {
                return new HashSet<>(OCBeltBlock.getBeltChain(level, belt.getController()));
            }
            return null;
        }
    }

}
