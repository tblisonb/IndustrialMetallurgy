package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.blocks.IOPortBlock;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;

// A capability proxy, not a machine -- it holds no energy or items of its own. It scans its own 6
// neighbors for the first real EnergyHandler/ResourceHandler<ItemResource> that isn't another port
// or a Conduit (the "host" machine it's attached to) and re-exposes that capability on itself,
// filtered by one shared Mode for both resource types. A Conduit connecting to a port sees exactly
// the same interface it'd see talking straight to a machine -- the port is purely what adds
// directional control on top of that.
public class IOPortBlockEntity extends BlockEntity {

    // Implements StringRepresentable so it can double as the block's own IOPortBlock.MODE
    // blockstate property -- the mode isn't just save data, it drives which of the three
    // io_port_<mode> textures renders (see blockstates/io_port.json).
    public enum Mode implements StringRepresentable {
        INPUT, OUTPUT, BOTH;

        public Mode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        public Component label() {
            return Component.translatable("message.industrialmetallurgy.io_port_mode_" + this.name().toLowerCase());
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    private Mode mode = Mode.BOTH;
    private final EnergyHandler energyDelegate = new PortEnergyHandler();
    private final ResourceHandler<ItemResource> itemDelegate = new PortItemHandler();

    public IOPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.IO_PORT.get(), pos, state);
    }

    public Mode getMode() {
        return this.mode;
    }

    public void cycleMode() {
        this.mode = this.mode.next();
        this.setChanged();
        if (this.level != null) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(IOPortBlock.MODE, this.mode));
        }
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
    private ResourceHandler<ItemResource> findHostItemHandler() {
        if (this.level == null) {
            return null;
        }
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = this.getBlockPos().relative(direction);
            BlockEntity neighbor = this.level.getBlockEntity(neighborPos);
            if (neighbor instanceof IOPortBlockEntity || neighbor instanceof ConduitBlockEntity) {
                continue;
            }
            ResourceHandler<ItemResource> handler = Capabilities.Item.BLOCK.getCapability(this.level, neighborPos, null, null, direction.getOpposite());
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    public ResourceHandler<ItemResource> getItemDelegate() {
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

    private class PortItemHandler implements ResourceHandler<ItemResource> {

        @Override
        public int size() {
            ResourceHandler<ItemResource> host = findHostItemHandler();
            return host != null ? host.size() : 0;
        }

        @Override
        public ItemResource getResource(int index) {
            ResourceHandler<ItemResource> host = findHostItemHandler();
            return host != null ? host.getResource(index) : ItemResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index) {
            ResourceHandler<ItemResource> host = findHostItemHandler();
            return host != null ? host.getAmountAsLong(index) : 0;
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            ResourceHandler<ItemResource> host = findHostItemHandler();
            return host != null ? host.getCapacityAsLong(index, resource) : 0;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            ResourceHandler<ItemResource> host = findHostItemHandler();
            return host != null && host.isValid(index, resource);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (mode == Mode.OUTPUT) {
                return 0;
            }
            ResourceHandler<ItemResource> host = findHostItemHandler();
            return host != null ? host.insert(index, resource, amount, transaction) : 0;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (mode == Mode.INPUT) {
                return 0;
            }
            ResourceHandler<ItemResource> host = findHostItemHandler();
            return host != null ? host.extract(index, resource, amount, transaction) : 0;
        }

    }

}
