package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.tileentity.ThermoelectricGeneratorBlockEntity;
import com.onlytanner.industrialmetallurgy.util.FunctionalIntReferenceHolder;
import com.onlytanner.industrialmetallurgy.util.ModItemHandler;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class ThermoelectricGeneratorContainer extends AbstractContainerMenu {

    public final ThermoelectricGeneratorBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntReferenceHolder burnTimeRemaining;
    public FunctionalIntReferenceHolder currentEnergy;
    public FunctionalIntReferenceHolder currentMaxBurnTime;

    public ThermoelectricGeneratorContainer(final int id, final Inventory player, final ThermoelectricGeneratorBlockEntity blockEntity) {
        super(ModContainerTypes.THERMOELECTRIC_GENERATOR.get(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        ModItemHandler inventory = blockEntity.getInventory();
        this.addSlot(new FuelSlot(inventory, ThermoelectricGeneratorBlockEntity.FUEL_ID, 80, 35));
        this.addSlot(new FuelSlot(inventory, 1, 8, 8));
        this.addSlot(new FuelSlot(inventory, 2, 8, 26));
        this.addSlot(new FuelSlot(inventory, 3, 8, 44));
        this.addSlot(new FuelSlot(inventory, 4, 8, 62));
        this.addSlot(new CouplingSlot(inventory, ThermoelectricGeneratorBlockEntity.COUPLING_ID, 132, 35));

        // Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(player, 9 + j + i * 9, 8 + (18 * j), 84 + (18 * i)));
            }
        }
        // Player Hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(player, i, 8 + (18 * i), 142));
        }

        this.addDataSlot(burnTimeRemaining = new FunctionalIntReferenceHolder(() -> this.blockEntity.burnTimeRemaining, value -> this.blockEntity.burnTimeRemaining = value));
        this.addDataSlot(currentEnergy = new FunctionalIntReferenceHolder(this.blockEntity::getEnergyAmount, this.blockEntity::setEnergyAmount));
        this.addDataSlot(currentMaxBurnTime = new FunctionalIntReferenceHolder(() -> this.blockEntity.currentMaxBurnTime, value -> this.blockEntity.currentMaxBurnTime = value));
    }

    public ThermoelectricGeneratorContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static ThermoelectricGeneratorBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof ThermoelectricGeneratorBlockEntity generator) {
            return generator;
        }
        throw new IllegalStateException("BlockEntity is not correct " + blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, RegistryHandler.THERMOELECTRIC_GENERATOR.get());
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            final int machineSlots = this.slots.size() - player.getInventory().getContainerSize();
            if (index < machineSlots) {
                if (!this.moveItemStackTo(slotStack, machineSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            // Reverse iteration order: specialized slots (fuel, bottle, solder wire, etc.) are
            // always registered after general-purpose slots, so trying them first here means a
            // shift-clicked item lands in its dedicated slot instead of getting stuck in the
            // first unrestricted slot it happens to pass.
            } else if (!this.moveItemStackTo(slotStack, 0, machineSlots, true)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
        }
        return returnStack;
    }

    public int getCurrentEnergyScaled() {
        return this.currentEnergy.get() != 0
                ? this.currentEnergy.get() * 70 / ThermoelectricGeneratorBlockEntity.MAX_ENERGY
                : 0;
    }

    public int getBurnTimeScaled() {
        return this.burnTimeRemaining.get() != 0 && this.currentMaxBurnTime.get() != 0
                ? this.burnTimeRemaining.get() * 14 / this.currentMaxBurnTime.get()
                : 0;
    }

    private class FuelSlot extends ResourceHandlerSlot {

        public FuelSlot(ModItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, itemHandler::set, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return (blockEntity.getLevel() != null && blockEntity.getLevel().fuelValues().burnDuration(stack) > 0)
                    || stack.getItem().equals(RegistryHandler.COAL_COKE.get());
        }

    }

    private static class CouplingSlot extends ResourceHandlerSlot {

        public CouplingSlot(ModItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, itemHandler::set, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem().equals(RegistryHandler.THERMOELECTRIC_COUPLING.get());
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

    }

}
