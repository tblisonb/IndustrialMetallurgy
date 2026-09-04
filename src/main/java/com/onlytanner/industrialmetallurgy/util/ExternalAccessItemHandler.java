package com.onlytanner.industrialmetallurgy.util;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Set;

/**
 * Wraps a machine's {@link ModItemHandler} for external access only -- see
 * {@link ModItemHandler#asExternalHandler()}. Output slots are extract-only from the outside;
 * every other slot is insert-only. Without this, a Conduit or I/O Port sees the exact same raw
 * handler the machine's own recipe logic uses internally, with no concept of "this slot is meant
 * to be filled by automation, not drained by it" -- meaning a port set to Output could just as
 * easily pull straight out of a machine's own input/raw-material slot as its actual output.
 */
public class ExternalAccessItemHandler implements ResourceHandler<ItemResource> {

    private final ResourceHandler<ItemResource> delegate;
    private final Set<Integer> outputSlots;

    public ExternalAccessItemHandler(ResourceHandler<ItemResource> delegate, Set<Integer> outputSlots) {
        this.delegate = delegate;
        this.outputSlots = outputSlots;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public ItemResource getResource(int index) {
        return this.delegate.getResource(index);
    }

    @Override
    public long getAmountAsLong(int index) {
        return this.delegate.getAmountAsLong(index);
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return this.delegate.getCapacityAsLong(index, resource);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return this.delegate.isValid(index, resource);
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (this.outputSlots.contains(index)) {
            return 0;
        }
        return this.delegate.insert(index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (!this.outputSlots.contains(index)) {
            return 0;
        }
        return this.delegate.extract(index, resource, amount, transaction);
    }

}
