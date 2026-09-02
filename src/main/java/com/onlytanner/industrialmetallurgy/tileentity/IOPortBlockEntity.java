package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;

// A capability proxy, not a machine -- it holds no energy of its own. It scans its own 6
// neighbors for the first real EnergyHandler that isn't another port or a Conduit (the "host"
// machine it's attached to) and re-exposes that capability on itself, filtered by Mode. A Conduit
// connecting to a port sees exactly the same EnergyHandler interface it'd see talking straight to
// a machine -- the port is purely what adds directional control on top of that.
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

}
