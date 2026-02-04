package io.github.cvrunmin.create_andertech.mixin;

import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeGridHandler.GroupedItems.class)
public interface GroupedItemsAccessor {
    @Accessor
    Map<Pair<Integer, Integer>, ItemStack> getGrid();
    @Accessor
    int getMinX();
    @Accessor
    int getMinY();
    @Accessor
    int getMaxX();
    @Accessor
    int getMaxY();
    @Accessor
    int getWidth();
    @Accessor
    int getHeight();
}
