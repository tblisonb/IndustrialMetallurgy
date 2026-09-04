package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.items.BatteryPackItem;
import com.onlytanner.industrialmetallurgy.tileentity.BatteryBoxBlockEntity;
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

public class BatteryBoxContainer extends AbstractContainerMenu {

    public final BatteryBoxBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    // The real number of this container's own slots -- NOT derivable from
    // player.getInventory().getContainerSize(), which in this MC version also counts the
    // offhand/body-armor/saddle equipment slots and is nowhere near 36, silently making every
    // quickMoveStack's machineSlots computation negative if it's used for that subtraction.
    private final int machineSlotCount;
    public FunctionalIntReferenceHolder burnTimeRemaining;
    public FunctionalIntReferenceHolder currentEnergy;
    public FunctionalIntReferenceHolder currentMaxBurnTime;

    public BatteryBoxContainer(final int id, final Inventory player, final BatteryBoxBlockEntity blockEntity) {
        super(ModContainerTypes.BATTERY_BOX.get(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        ModItemHandler inventory = blockEntity.getInventory();
        this.addSlot(new BatterySlot(inventory, BatteryBoxBlockEntity.FUEL_ID, 80, 35));
        this.addSlot(new BatterySlot(inventory, 1, 8, 8));
        this.addSlot(new BatterySlot(inventory, 2, 8, 26));
        this.addSlot(new BatterySlot(inventory, 3, 8, 44));
        this.addSlot(new BatterySlot(inventory, 4, 8, 62));
        this.addSlot(new ChargeSlot(inventory, BatteryBoxBlockEntity.CHARGE_SLOT_ID, 134, 35));

        this.machineSlotCount = this.slots.size();

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

    public BatteryBoxContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static BatteryBoxBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof BatteryBoxBlockEntity batteryBox) {
            return batteryBox;
        }
        throw new IllegalStateException("BlockEntity is not correct " + blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, RegistryHandler.BATTERY_BOX.get());
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            final int machineSlots = this.machineSlotCount;
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
                ? this.currentEnergy.get() * 70 / BatteryBoxBlockEntity.MAX_ENERGY
                : 0;
    }

    public int getBurnTimeScaled() {
        return this.burnTimeRemaining.get() != 0 && this.currentMaxBurnTime.get() != 0
                ? this.burnTimeRemaining.get() * 14 / this.currentMaxBurnTime.get()
                : 0;
    }

    private static class BatterySlot extends ResourceHandlerSlot {

        public BatterySlot(ModItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, itemHandler::set, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return BatteryBoxBlockEntity.isBatteryItem(stack);
        }

    }

    private static class ChargeSlot extends ResourceHandlerSlot {

        public ChargeSlot(ModItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, itemHandler::set, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof BatteryPackItem;
        }

    }

}
