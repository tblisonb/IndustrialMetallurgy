package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.util.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;

// A capability proxy, not a machine -- it holds no energy or items of its own. It scans its own 6
// neighbors for the first real EnergyHandler/IItemHandlerModifiable that isn't another port or a
// Conduit (the "host" machine it's attached to) and re-exposes that capability on itself, filtered
// by one shared Mode for both resource types. A Conduit connecting to a port sees exactly the same
// interface it'd see talking straight to a machine -- the port is purely what adds directional
// control on top of that.
public class IOPortBlockEntity extends BlockEntity {

    public enum Mode {
        INPUT, OUTPUT, BOTH;

        public Mode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        public Component label() {
            return Component.translatable("message.industrialmetallurgy.io_port_mode_" + this.name().toLowerCase());
        }
    }

    private Mode mode = Mode.BOTH;
    private final EnergyHandler energyDelegate = new PortEnergyHandler();
    private final IItemHandlerModifiable itemDelegate = new PortItemHandler();

    public IOPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.IO_PORT.get(), pos, state);
    }

    public Mode getMode() {
        return this.mode;
    }

    public void cycleMode() {
        this.mode = this.mode.next();
        this.setChanged();
    }

    @Nullable
    private EnergyHandler findHostEnergyHandler() {
        if (this.level == null) {
            return null;
        }
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = this.getBlockPos().relative(direction);
            BlockEntity neighbor = this.level.getBlockEntity(neighborPos);
            if (neighbor instanceof IOPortBlockEntity || neighbor instanceof ConduitBlockEntity) {
                continue;
            }
            EnergyHandler handler = Capabilities.Energy.BLOCK.getCapability(this.level, neighborPos, null, null, direction.getOpposite());
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    public EnergyHandler getEnergyDelegate() {
        return this.energyDelegate;
    }

    @Nullable
    private IItemHandlerModifiable findHostItemHandler() {
        if (this.level == null) {
            return null;
        }
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = this.getBlockPos().relative(direction);
            BlockEntity neighbor = this.level.getBlockEntity(neighborPos);
            if (neighbor instanceof IOPortBlockEntity || neighbor instanceof ConduitBlockEntity) {
                continue;
            }
            IItemHandlerModifiable handler = ModCapabilities.ITEM_HANDLER.getCapability(this.level, neighborPos, null, null, direction.getOpposite());
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    public IItemHandlerModifiable getItemDelegate() {
        return this.itemDelegate;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.mode = Mode.values()[input.getIntOr("Mode", Mode.BOTH.ordinal())];
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Mode", this.mode.ordinal());
    }

    private class PortEnergyHandler implements EnergyHandler {

        @Override
        public long getAmountAsLong() {
            EnergyHandler host = findHostEnergyHandler();
            return host != null ? host.getAmountAsLong() : 0;
        }

        @Override
        public long getCapacityAsLong() {
            EnergyHandler host = findHostEnergyHandler();
            return host != null ? host.getCapacityAsLong() : 0;
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            if (mode == Mode.OUTPUT) {
                return 0;
            }
            EnergyHandler host = findHostEnergyHandler();
            return host != null ? host.insert(amount, transaction) : 0;
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            if (mode == Mode.INPUT) {
                return 0;
            }
            EnergyHandler host = findHostEnergyHandler();
            return host != null ? host.extract(amount, transaction) : 0;
        }

    }

    private class PortItemHandler implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            IItemHandlerModifiable host = findHostItemHandler();
            return host != null ? host.getSlots() : 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            IItemHandlerModifiable host = findHostItemHandler();
            return host != null ? host.getStackInSlot(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (mode == Mode.OUTPUT) {
                return stack;
            }
            IItemHandlerModifiable host = findHostItemHandler();
            return host != null ? host.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (mode == Mode.INPUT) {
                return ItemStack.EMPTY;
            }
            IItemHandlerModifiable host = findHostItemHandler();
            return host != null ? host.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            IItemHandlerModifiable host = findHostItemHandler();
            return host != null ? host.getSlotLimit(slot) : 0;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            IItemHandlerModifiable host = findHostItemHandler();
            return host != null && host.isItemValid(slot, stack);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            IItemHandlerModifiable host = findHostItemHandler();
            if (host != null) {
                host.setStackInSlot(slot, stack);
            }
        }

    }

}
