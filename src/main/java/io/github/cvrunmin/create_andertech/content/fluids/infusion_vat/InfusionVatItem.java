package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.foundation.block.IBE;
import io.github.cvrunmin.create_andertech.content.fluids.tank.CrimschistFluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class InfusionVatItem extends BlockItem {
    public InfusionVatItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        MinecraftServer minecraftserver = level.getServer();
        if (minecraftserver == null)
            return false;
        CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            CompoundTag nbt = blockEntityData.copyTag();
            nbt.remove("Luminosity");
            nbt.remove("XLength");
            nbt.remove("ZLength");
            nbt.remove("Height");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
            if (nbt.contains("TankContent")) {
                FluidStack fluid = FluidStack.parseOptional(minecraftserver.registryAccess(), nbt.getCompound("TankContent"));
                if (!fluid.isEmpty()) {
                    fluid.setAmount(Math.min(CrimschistFluidTankBlockEntity.getMyCapacityMultiplier(), fluid.getAmount()));
                    nbt.put("TankContent", fluid.saveOptional(minecraftserver.registryAccess()));
                }
            }
            BlockEntity.addEntityType(nbt, ((IBE<?>) this.getBlock()).getBlockEntityType());
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(nbt));
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }
}
