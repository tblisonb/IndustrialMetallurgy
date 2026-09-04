package com.onlytanner.industrialmetallurgy.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Set;

// A machine's ground-truth inventory, backed by the new net.neoforged.neoforge.transfer
// resource-handler API (the real ResourceHandler<ItemResource> exposed as Capabilities.Item.BLOCK)
// rather than the deprecated ItemStackHandler/IItemHandler this used to extend. The convenience
// methods below (getStackInSlot/insertItem/extractItem/etc.) keep every call site elsewhere in the
// mod syntactically unchanged -- they're built on top of the new insert/extract(Transaction)/set
// primitives, not a second parallel storage mechanism.
public class ModItemHandler extends ItemStacksResourceHandler {

    private final int fixedSize;
    private final Set<Integer> outputSlots;

    public ModItemHandler(int size) {
        this(size, Set.of());
    }

    /**
     * @param outputSlots Which of this handler's slots are actually a finished-product output --
     *                     see {@link #asExternalHandler()}. Empty for a machine with no output
     *                     concept at all (e.g. Battery Box, Thermoelectric Generator).
     */
    public ModItemHandler(int size, Set<Integer> outputSlots) {
        super(size);
        this.fixedSize = size;
        this.outputSlots = outputSlots;
    }

    @Override
    public void deserialize(ValueInput input) {
        super.deserialize(input);
        // StacksResourceHandler#deserialize replaces its backing list with whatever length was
        // saved in the NBT -- if a machine's slot count ever changes after a world has already
        // saved one of its block entities, this silently resizes the handler back to the old size
        // instead of the current constructor's, and every fixed-slot-index accessor then throws.
        // Re-assert the real size afterward, preserving whatever was actually loaded.
        if (this.stacks.size() != this.fixedSize) {
            NonNullList<ItemStack> loaded = this.stacks;
            this.setStacks(NonNullList.withSize(this.fixedSize, ItemStack.EMPTY));
            for (int index = 0; index < loaded.size() && index < this.fixedSize; index++) {
                this.setStackInSlot(index, loaded.get(index));
            }
        }
    }

    public ItemStack getStackInSlot(int index) {
        return this.stacks.get(index);
    }

    public void setStackInSlot(int index, ItemStack stack) {
        if (stack.isEmpty()) {
            this.set(index, ItemResource.EMPTY, 0);
        } else {
            this.set(index, ItemResource.of(stack), stack.getCount());
        }
    }

    public ItemStack insertItem(int index, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemResource resource = ItemResource.of(stack);
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = this.insert(index, resource, stack.getCount(), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return inserted == stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - inserted);
        }
    }

    public ItemStack extractItem(int index, int amount, boolean simulate) {
        ItemResource resource = this.getResource(index);
        if (resource.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = this.extract(index, resource, amount, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return resource.toStack(extracted);
        }
    }

    public void decrStackSize(int index, int count) {
        this.extractItem(index, count, false);
    }

    public boolean isItemValid(int index, ItemStack stack) {
        return this.isValid(index, ItemResource.of(stack));
    }

    public int getSlotLimit(int index) {
        return this.getCapacityAsInt(index, ItemResource.EMPTY);
    }

    /**
     * The view exposed as this block's Capabilities.Item.BLOCK capability -- i.e. everything a
     * Conduit or I/O Port can actually see and touch. Restricted to output slots only extract,
     * everything else only insert, so external automation can feed a machine raw material but
     * can't reach in and drain a slot mid-process the way a bare ModItemHandler would let it.
     * The player's own GUI is unaffected: Container/ResourceHandlerSlot talk to this handler
     * directly, never through this wrapper.
     */
    public ResourceHandler<ItemResource> asExternalHandler() {
        return new ExternalAccessItemHandler(this, this.outputSlots);
    }

}
