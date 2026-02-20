package io.github.cvrunmin.create_andertech.content.fluids.infusion_vat;

import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class InfusionVatInventory extends SmartInventory {
    private InfusionVatBlockEntity vat;

    public InfusionVatInventory(int slots, InfusionVatBlockEntity be) {
        super(slots, be, 64, true);
        this.vat = be;
    }

    public List<ItemStack> resizeCapacity(int newSlots){
        if(newSlots < 1) return List.of();
        if(getSlots() == newSlots) return List.of();
        Queue<ItemStack> oldStacks = new ArrayDeque<>();
        for (int i = 0; i < inv.getSlots(); i++) {
            var stack = inv.getStackInSlot(i);
            if(stack.isEmpty()) continue;
            oldStacks.add(stack);
        }
        // assume this to be true as super constructor will try to cast inv into this
        ((SyncedStackHandler) inv).setSize(newSlots);
        for (int i = 0; i < inv.getSlots(); i++) {
            var stack = oldStacks.poll();
            if(stack == null) break;
            inv.setStackInSlot(i, stack);
        }
        return new ArrayList<>(oldStacks);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extractItem = super.extractItem(slot, amount, simulate);
        if (!simulate && !extractItem.isEmpty())
            vat.notifyChangeOfContents();
        return extractItem;
    }

    public ArrayList<ItemStack> getContents(){
        var contents = new ArrayList<ItemStack>(getSlots());
        for (int i = 0; i < getSlots(); i++) {
            var stack = getStackInSlot(i);
            if(!stack.isEmpty()) contents.add(stack);
        }
        return contents;
    }
}
