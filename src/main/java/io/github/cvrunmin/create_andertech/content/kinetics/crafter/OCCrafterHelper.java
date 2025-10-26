package io.github.cvrunmin.create_andertech.content.kinetics.crafter;

import com.simibubi.create.content.kinetics.crafter.CrafterHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Same content with {@link CrafterHelper}, but target for {@link ChronenditeMechanicalCrafterBlockEntity}
 */
public class OCCrafterHelper {

    public static ChronenditeMechanicalCrafterBlockEntity getCrafter(BlockAndTintGetter reader, BlockPos pos) {
        BlockEntity blockEntity = reader.getBlockEntity(pos);
        if (!(blockEntity instanceof ChronenditeMechanicalCrafterBlockEntity))
            return null;
        return (ChronenditeMechanicalCrafterBlockEntity) blockEntity;
    }

    public static OCConnectedInputHandler.ConnectedInput getInput(BlockAndTintGetter reader, BlockPos pos) {
        ChronenditeMechanicalCrafterBlockEntity crafter = getCrafter(reader, pos);
        return crafter == null ? null : crafter.input;
    }

    public static boolean areCraftersConnected(BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos) {
        var input1 = getInput(reader, pos);
        var input2 = getInput(reader, otherPos);

        if (input1 == null || input2 == null)
            return false;
        if (input1.data.isEmpty() || input2.data.isEmpty())
            return false;
        try {
            if (pos.offset(input1.data.get(0))
                    .equals(otherPos.offset(input2.data.get(0))))
                return true;
        } catch (IndexOutOfBoundsException e) {
            // race condition. data somehow becomes empty between the last 2 if statements
        }

        return false;
    }
}
