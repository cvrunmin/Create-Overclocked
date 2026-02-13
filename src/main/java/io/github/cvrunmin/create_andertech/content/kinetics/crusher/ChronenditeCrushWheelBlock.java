package io.github.cvrunmin.create_andertech.content.kinetics.crusher;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import io.github.cvrunmin.create_andertech.index.AllBlocks;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock.VALID;

public class ChronenditeCrushWheelBlock extends RotatedPillarKineticBlock implements IBE<ChronenditeCrushWheelBlockEntity> {

    public ChronenditeCrushWheelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CRUSHING_WHEEL_COLLISION_SHAPE;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        for (Direction d : Iterate.directions) {
            if (d.getAxis() == state.getValue(AXIS))
                continue;
            if (AllBlocks.CHRONENDITE_CRUSHING_WHEEL_CONTROLLER.has(worldIn.getBlockState(pos.relative(d))))
                worldIn.removeBlock(pos.relative(d), isMoving);
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public void updateControllers(BlockState state, Level world, BlockPos pos, Direction side) {
        if (side.getAxis() == state.getValue(AXIS))
            return;
        if (world == null)
            return;

        BlockPos controllerPos = pos.relative(side);
        BlockPos otherWheelPos = pos.relative(side, 2);

        boolean controllerExists = AllBlocks.CHRONENDITE_CRUSHING_WHEEL_CONTROLLER.has(world.getBlockState(controllerPos));
        boolean controllerIsValid = controllerExists && world.getBlockState(controllerPos)
                .getValue(VALID);
        Direction controllerOldDirection = controllerExists ? world.getBlockState(controllerPos)
                .getValue(ChronenditeCrushWheelControllerBlock.FACING) : null;

        boolean controllerShouldExist = false;
        boolean controllerShouldBeValid = false;
        Direction controllerNewDirection = Direction.DOWN;

        BlockState otherState = world.getBlockState(otherWheelPos);
        if (AllBlocks.CHRONENDITE_CRUSHING_WHEEL.has(otherState)) {
            controllerShouldExist = true;

            ChronenditeCrushWheelBlockEntity be = getBlockEntity(world, pos);
            ChronenditeCrushWheelBlockEntity otherBE = getBlockEntity(world, otherWheelPos);

            if (be != null && otherBE != null && (be.getSpeed() > 0) != (otherBE.getSpeed() > 0)
                    && be.getSpeed() != 0) {
                Direction.Axis wheelAxis = state.getValue(AXIS);
                Direction.Axis sideAxis = side.getAxis();
                int controllerADO = Math.round(Math.signum(be.getSpeed())) * side.getAxisDirection()
                        .getStep();
                Vec3 controllerDirVec = new Vec3(wheelAxis == Direction.Axis.X ? 1 : 0, wheelAxis == Direction.Axis.Y ? 1 : 0,
                        wheelAxis == Direction.Axis.Z ? 1 : 0).cross(
                        new Vec3(sideAxis == Direction.Axis.X ? 1 : 0, sideAxis == Direction.Axis.Y ? 1 : 0, sideAxis == Direction.Axis.Z ? 1 : 0));

                controllerNewDirection = Direction.getNearest(controllerDirVec.x * controllerADO,
                        controllerDirVec.y * controllerADO, controllerDirVec.z * controllerADO);

                controllerShouldBeValid = true;
            }
            if (otherState.getValue(AXIS) != state.getValue(AXIS))
                controllerShouldExist = false;
        }

        if (!controllerShouldExist) {
            if (controllerExists)
                world.setBlockAndUpdate(controllerPos, Blocks.AIR.defaultBlockState());
            return;
        }

        if (!controllerExists) {
            if (!world.getBlockState(controllerPos)
                    .canBeReplaced())
                return;
            world.setBlockAndUpdate(controllerPos, AllBlocks.CHRONENDITE_CRUSHING_WHEEL_CONTROLLER.getDefaultState()
                    .setValue(VALID, controllerShouldBeValid)
                    .setValue(ChronenditeCrushWheelControllerBlock.FACING, controllerNewDirection));
        } else if (controllerIsValid != controllerShouldBeValid || controllerOldDirection != controllerNewDirection) {
            world.setBlockAndUpdate(controllerPos, world.getBlockState(controllerPos)
                    .setValue(VALID, controllerShouldBeValid)
                    .setValue(ChronenditeCrushWheelControllerBlock.FACING, controllerNewDirection));
        }

        ((ChronenditeCrushWheelControllerBlock) AllBlocks.CHRONENDITE_CRUSHING_WHEEL_CONTROLLER.get())
                .updateSpeed(world.getBlockState(controllerPos), world, controllerPos);

    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn.getY() < pos.getY() + 1.25f || !entityIn.onGround())
            return;

        float speed = getBlockEntityOptional(worldIn, pos).map(ChronenditeCrushWheelBlockEntity::getSpeed)
                .orElse(0f);

        double x = 0;
        double z = 0;

        if (state.getValue(AXIS) == Direction.Axis.X) {
            z = speed / 20f;
            x += (pos.getX() + .5f - entityIn.getX()) * .1f;
        }
        if (state.getValue(AXIS) == Direction.Axis.Z) {
            x = speed / -20f;
            z += (pos.getZ() + .5f - entityIn.getZ()) * .1f;
        }
        entityIn.setDeltaMovement(entityIn.getDeltaMovement()
                .add(x, 0, z));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        for (Direction direction : Iterate.directions) {
            BlockPos neighbourPos = pos.relative(direction);
            BlockState neighbourState = worldIn.getBlockState(neighbourPos);
            Direction.Axis stateAxis = state.getValue(AXIS);
            if (AllBlocks.CHRONENDITE_CRUSHING_WHEEL_CONTROLLER.has(neighbourState) && direction.getAxis() != stateAxis)
                return false;
            if (!AllBlocks.CHRONENDITE_CRUSHING_WHEEL.has(neighbourState))
                continue;
            if (neighbourState.getValue(AXIS) != stateAxis || stateAxis != direction.getAxis())
                return false;
        }

        return true;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public float getParticleTargetRadius() {
        return 1.125f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 1f;
    }

    @Override
    public Class<ChronenditeCrushWheelBlockEntity> getBlockEntityClass() {
        return ChronenditeCrushWheelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChronenditeCrushWheelBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CHRONENDITE_CRUSHING_WHEEL.get();
    }
}
