package io.github.cvrunmin.create_andertech.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import io.github.cvrunmin.create_andertech.index.AllBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CrimschistCogWheelBlock extends CogWheelBlock {
//    boolean isLarge;

    public CrimschistCogWheelBlock(boolean large, Properties properties) {
        super(large, properties);
//        isLarge = large;
    }

    public static CrimschistCogWheelBlock small(Properties properties){
        return new CrimschistCogWheelBlock(false, properties);
    }

    public static CrimschistCogWheelBlock large(Properties properties){
        return new CrimschistCogWheelBlock(true, properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.BRACKETED_OC_KINETIC.get();
    }

}
