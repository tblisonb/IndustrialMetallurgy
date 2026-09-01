package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.BatteryBoxBlock;
import com.onlytanner.industrialmetallurgy.containers.BatteryBoxContainer;
import com.onlytanner.industrialmetallurgy.init.ModDataComponents;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.items.BatteryPackItem;
import com.onlytanner.industrialmetallurgy.util.ModItemHandler;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.util.Map;

// Mirrors ThermoelectricGeneratorBlockEntity's burn-and-push tick loop, but "fuel" values come
// from ENERGY_VALUES (a battery's real charge) instead of vanilla's fuel-burn table.
public class BatteryBoxBlockEntity extends BlockEntity implements MenuProvider {

    public static final int FUEL_ID = 0;
    public static final int NUM_EXTRA_FUEL_SLOTS = 4;
    public static final int CHARGE_SLOT_ID = 5;
    public static final int MAX_ENERGY = 500000;
    public static final int MAX_ENERGY_PROVIDED = 80;
    public static final int ENERGY_GENERATED_PER_TICK = 80;
    public static final int PACK_CHARGE_RATE = 80;

    private static final Map<Item, Integer> ENERGY_VALUES = Map.of(
            RegistryHandler.DRY_CELL.get(), 8000,
            RegistryHandler.DRY_CELL_BANK.get(), 32000,
            RegistryHandler.BATTERY_CELL.get(), 20000,
            RegistryHandler.BATTERY_BANK.get(), 80000,
            RegistryHandler.LITHIUM_BATTERY_CELL.get(), 50000,
            RegistryHandler.LITHIUM_BATTERY_BANK.get(), 200000
    );

    private Component customName;
    public int burnTimeRemaining;
    public int currentMaxBurnTime;
    private final ModItemHandler inventory = new ModItemHandler(6);
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            BatteryBoxBlockEntity.this.setChanged();
        }
    };

    public BatteryBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.BATTERY_BOX.get(), pos, state);
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < NUM_EXTRA_FUEL_SLOTS; i++) {
                ItemStack extra = this.inventory.getStackInSlot(1 + i);
                ItemStack fuel = this.inventory.getStackInSlot(FUEL_ID);
                if (extra.getCount() > 0 && fuel.getCount() == 0) {
                    this.inventory.setStackInSlot(FUEL_ID, extra.copy());
                    extra.setCount(0);
                    break;
                } else if (extra.getCount() > 0 && fuel.getCount() > 0 && fuel.getCount() < 64 && extra.getItem().equals(fuel.getItem())) {
                    fuel.setCount(fuel.getCount() + 1);
                    this.inventory.decrStackSize(1 + i, 1);
                    break;
                }
            }

            int energy = this.energyHandler.getAmountAsInt();
            if (hasBattery() && burnTimeRemaining == 0 && energy < MAX_ENERGY) {
                consumeBattery();
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BatteryBoxBlock.LIT, true));
                dirty = true;
            } else if (burnTimeRemaining > 0 && energy < (MAX_ENERGY - ENERGY_GENERATED_PER_TICK)) {
                this.burnTimeRemaining--;
                this.energyHandler.set(energy + ENERGY_GENERATED_PER_TICK);
            } else if (burnTimeRemaining > 0 && energy == MAX_ENERGY) {
                this.burnTimeRemaining--;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BatteryBoxBlock.LIT, false));
                dirty = true;
            }

            chargeBatteryPack();

            if (this.energyHandler.getAmountAsInt() > 0) {
                providePowerToNeighbors(serverLevel);
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    // Tops up a Battery Pack sitting in the charge slot directly from this box's own buffer,
    // in place -- the pack is never consumed here, unlike the disposable batteries in the fuel
    // slot above.
    private void chargeBatteryPack() {
        ItemStack stack = this.inventory.getStackInSlot(CHARGE_SLOT_ID);
        if (!(stack.getItem() instanceof BatteryPackItem)) {
            return;
        }
        int capacity = BatteryPackItem.capacityOf(stack.getItem());
        int stored = stack.getOrDefault(ModDataComponents.STORED_ENERGY.get(), 0);
        int available = this.energyHandler.getAmountAsInt();
        int toTransfer = Math.min(PACK_CHARGE_RATE, Math.min(capacity - stored, available));
        if (toTransfer <= 0) {
            return;
        }
        stack.set(ModDataComponents.STORED_ENERGY.get(), stored + toTransfer);
        this.energyHandler.set(available - toTransfer);
    }

    private void providePowerToNeighbors(ServerLevel level) {
        java.util.List<EnergyHandler> neighbors = new java.util.ArrayList<>();
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

    public boolean hasBattery() {
        return isBatteryItem(this.inventory.getStackInSlot(FUEL_ID));
    }

    public static boolean isBatteryItem(ItemStack stack) {
        return !stack.isEmpty() && ENERGY_VALUES.containsKey(stack.getItem());
    }

    public void consumeBattery() {
        ItemStack batteryStack = this.inventory.getStackInSlot(FUEL_ID);
        int totalEnergy = ENERGY_VALUES.getOrDefault(batteryStack.getItem(), 0);
        int burnTime = totalEnergy / ENERGY_GENERATED_PER_TICK;
        this.burnTimeRemaining = burnTime;
        this.currentMaxBurnTime = burnTime;
        this.inventory.decrStackSize(FUEL_ID, 1);
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable("container." + IndustrialMetallurgy.MODID + ".battery_box");
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.customName = input.read("CustomName", ComponentSerialization.CODEC).orElse(null);
        this.inventory.deserialize(input.childOrEmpty("Inventory"));
        this.burnTimeRemaining = input.getIntOr("BurnTimeRemaining", 0);
        this.currentMaxBurnTime = input.getIntOr("CurrentMaxBurnTime", 0);
        this.energyHandler.deserialize(input.childOrEmpty("Energy"));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.customName != null) {
            output.store("CustomName", ComponentSerialization.CODEC, this.customName);
        }
        this.inventory.serialize(output.child("Inventory"));
        output.putInt("BurnTimeRemaining", this.burnTimeRemaining);
        output.putInt("CurrentMaxBurnTime", this.currentMaxBurnTime);
        this.energyHandler.serialize(output.child("Energy"));
    }

    public final IItemHandlerModifiable getInventory() {
        return this.inventory;
    }

    public final EnergyHandler getEnergyHandler() {
        return this.energyHandler;
    }

    public int getEnergyAmount() {
        return this.energyHandler.getAmountAsInt();
    }

    public void setEnergyAmount(int amount) {
        this.energyHandler.set(Math.max(0, amount));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BatteryBoxContainer(containerId, playerInventory, this);
    }

}
