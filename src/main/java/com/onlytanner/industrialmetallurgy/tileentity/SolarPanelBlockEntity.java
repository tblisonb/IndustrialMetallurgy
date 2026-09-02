package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

// Purely passive -- no fuel slot, no recipe, nothing to insert. Generates FE whenever it has an
// unobstructed view of the sky during the day, same reduced-output-in-bad-weather behavior real
// panels have, then pushes straight out to neighbors exactly like ThermoelectricGeneratorBlockEntity
// does. A much smaller buffer and lower per-tick rate than that machine on purpose: this is meant
// to trickle power out continuously, not stockpile it, and the real tradeoff for "free" (no fuel
// cost) generation is a lower, weather/daylight-gated rate.
public class SolarPanelBlockEntity extends BlockEntity {

    public static final int MAX_ENERGY = 50_000;
    public static final int ENERGY_GENERATED_PER_TICK = 20;
    public static final int MAX_ENERGY_PROVIDED = 20;

    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            SolarPanelBlockEntity.this.setChanged();
        }
    };

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.SOLAR_PANEL.get(), pos, state);
    }

    public void tick() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (canGenerate(serverLevel)) {
            int generated = currentGeneration(serverLevel);
            int energy = this.energyHandler.getAmountAsInt();
            if (energy < MAX_ENERGY) {
                this.energyHandler.set(Math.min(MAX_ENERGY, energy + generated));
            }
        }

        if (this.energyHandler.getAmountAsInt() > 0) {
            providePowerToNeighbors(serverLevel);
        }
    }

    public boolean canGenerate(ServerLevel level) {
        // getSkyDarken() is 0 at full daylight, rising to 11 by night -- tracks actual brightness
        // (through dawn/dusk) rather than a blunt "past sunrise" time check.
        return level.getSkyDarken() == 0 && level.canSeeSky(this.getBlockPos().above());
    }

    public int currentGeneration(ServerLevel level) {
        if (level.isThundering()) {
            return ENERGY_GENERATED_PER_TICK / 4;
        }
        if (level.isRaining()) {
            return ENERGY_GENERATED_PER_TICK / 2;
        }
        return ENERGY_GENERATED_PER_TICK;
    }

    private void providePowerToNeighbors(ServerLevel level) {
        List<EnergyHandler> neighbors = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            EnergyHandler neighbor = Capabilities.Energy.BLOCK.getCapability(level, this.getBlockPos().relative(direction), null, null, direction.getOpposite());
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }
        if (neighbors.isEmpty()) {
            return;
        }
        int share = MAX_ENERGY_PROVIDED / neighbors.size();
        for (EnergyHandler neighbor : neighbors) {
            int toSend = Math.min(share, this.energyHandler.getAmountAsInt());
            if (toSend <= 0) {
                continue;
            }
            try (Transaction transaction = Transaction.openRoot()) {
                int inserted = neighbor.insert(toSend, transaction);
                if (inserted > 0) {
                    this.energyHandler.extract(inserted, transaction);
                    transaction.commit();
                }
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energyHandler.deserialize(input.childOrEmpty("Energy"));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.energyHandler.serialize(output.child("Energy"));
    }

    public final EnergyHandler getEnergyHandler() {
        return this.energyHandler;
    }

    public int getEnergyAmount() {
        return this.energyHandler.getAmountAsInt();
    }

}
