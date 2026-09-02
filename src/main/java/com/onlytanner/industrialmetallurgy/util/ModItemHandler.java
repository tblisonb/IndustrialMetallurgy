package com.onlytanner.industrialmetallurgy.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ModItemHandler extends ItemStackHandler {

    private final int fixedSize;

    public ModItemHandler(int size, ItemStack... initialStacks) {
        super(size);
        this.fixedSize = size;

        for (int index = 0; index < initialStacks.length; index++) {
            this.stacks.set(index, initialStacks[index]);
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        super.deserialize(input);
        // ItemStackHandler#deserialize resizes itself to whatever "Size" was saved in the NBT
        // (via #setSize, which replaces the whole backing list) -- if a machine's slot count ever
        // changes after a world has already saved one of its block entities, this silently
        // shrinks/grows the handler back to the old size instead of the current constructor's,
        // and every fixed-slot-index accessor (getStackInSlot(5), etc.) then throws. Re-assert
        // the real size afterward, preserving whatever was actually loaded.
        if (this.getSlots() != this.fixedSize) {
            NonNullList<ItemStack> loaded = this.toNonNullList();
            this.setSize(this.fixedSize);
            for (int index = 0; index < loaded.size() && index < this.fixedSize; index++) {
                this.setStackInSlot(index, loaded.get(index));
            }
        }
    }

    public void clear() {
        for (int index = 0; index < this.getSlots(); index++) {
            this.stacks.set(index, ItemStack.EMPTY);
            this.onContentsChanged(index);
        }
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (stack.isEmpty() || (stack.getItem() == Items.AIR)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getStackInSlot(index);
        stack.shrink(count);
        this.onContentsChanged(index);
        return stack;
    }

    public void removeStackFromSlot(int index) {
        this.stacks.set(index, ItemStack.EMPTY);
        this.onContentsChanged(index);
    }

    public NonNullList<ItemStack> toNonNullList() {
        NonNullList<ItemStack> items = NonNullList.create();
        for (ItemStack stack : this.stacks) {
            items.add(stack);
        }
        return items;
    }

    public void setNonNullList(NonNullList<ItemStack> items) {
        if (items.isEmpty())
            return;
        if (items.size() != this.getSlots())
            throw new IndexOutOfBoundsException("NonNullList must be same size as ItemStackHandler!");
        for (int index = 0; index < items.size(); index++) {
            this.stacks.set(index, items.get(index));
        }
    }

    @Override
    public String toString() {
        return this.stacks.toString();
    }

}
