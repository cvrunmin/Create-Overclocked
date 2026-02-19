package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InfusionVatBlock extends Block implements IWrenchable, IBE<InfusionVatBlockEntity> {
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;

    public InfusionVatBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(EAST, true)
                .setValue(WEST, true)
                .setValue(NORTH, true)
                .setValue(SOUTH, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM, EAST, WEST, SOUTH, NORTH);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (movedByPiston)
            return;
        withBlockEntityDo(level, pos, InfusionVatBlockEntity::updateConnectivity);
    }


    static final VoxelShape CLIP = Block.box(0, 0.1, 0, 16, 16, 16);

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
                                        CollisionContext pContext) {
        if (pContext == CollisionContext.empty())
            return Block.box(0, 0.1, 0, 16, 16, 16);
//            return CLIP;
        return pState.getShape(pLevel, pPos);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.block();
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = level.getBlockEntity(pos);
            if(!(be instanceof InfusionVatBlockEntity)) return;
            level.removeBlockEntity(pos);
            InfusionVatConnectivityHandler.splitMultiblock(((InfusionVatBlockEntity) be));
        }
    }

    @Override
    public Class<InfusionVatBlockEntity> getBlockEntityClass() {
        return InfusionVatBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends InfusionVatBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.INFUSION_VAT.get();
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if(mirror == Mirror.NONE) return state;
        boolean east = state.getValue(EAST);
        boolean west = state.getValue(WEST);
        boolean north = state.getValue(NORTH);
        boolean south = state.getValue(SOUTH);
        switch (mirror) {
            case FRONT_BACK -> state = state.setValue(EAST, west).setValue(WEST, east);
            case LEFT_RIGHT -> state = state.setValue(NORTH, south).setValue(SOUTH, north);
        }
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++) {
            state = rotateOnce(state);
        }
        return state;
    }

    private BlockState rotateOnce(BlockState state){
        boolean east = state.getValue(EAST);
        boolean west = state.getValue(WEST);
        boolean north = state.getValue(NORTH);
        boolean south = state.getValue(SOUTH);
        return state.setValue(SOUTH, east).setValue(WEST, south).setValue(NORTH, west).setValue(EAST, north);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = super.getSoundType(state, world, pos, entity);
        if (entity != null && entity.getPersistentData().contains("SilenceTankSound"))
            return FluidTankBlock.SILENCED_METAL;
        return soundType;
    }
}
