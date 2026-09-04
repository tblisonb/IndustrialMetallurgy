package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.tileentity.AutoclaveBlockEntity;
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

public class AutoclaveContainer extends AbstractContainerMenu {

    public final AutoclaveBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntReferenceHolder currentProcessTime;
    public FunctionalIntReferenceHolder currentEnergy;
    private final Inventory playerInventory;

    public AutoclaveContainer(final int id, final Inventory player, final AutoclaveBlockEntity blockEntity) {
        super(ModContainerTypes.AUTOCLAVE.get(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.playerInventory = player;

        ModItemHandler inventory = blockEntity.getInventory();
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, AutoclaveBlockEntity.INPUT_ID, 39, 24));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, AutoclaveBlockEntity.INPUT_ID + 1, 62, 17));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, AutoclaveBlockEntity.INPUT_ID + 2, 39, 51));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, AutoclaveBlockEntity.OUTPUT_ID, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

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

        this.addDataSlot(currentProcessTime = new FunctionalIntReferenceHolder(() -> this.blockEntity.currentProcessTime, value -> this.blockEntity.currentProcessTime = value));
        this.addDataSlot(currentEnergy = new FunctionalIntReferenceHolder(this.blockEntity::getEnergyAmount, this.blockEntity::setEnergyAmount));
    }

    public AutoclaveContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static AutoclaveBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof AutoclaveBlockEntity autoclave) {
            return autoclave;
        }
        throw new IllegalStateException("BlockEntity is not correct " + blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, RegistryHandler.AUTOCLAVE.get());
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

    public int getProcessProgressionScaled() {
        return this.currentProcessTime.get() != 0
                ? this.currentProcessTime.get() * 28 / AutoclaveBlockEntity.MAX_PROCESS_TIME
                : 0;
    }

    public int getCurrentEnergyScaled() {
        return this.currentEnergy.get() != 0
                ? this.currentEnergy.get() * 70 / AutoclaveBlockEntity.MAX_ENERGY
                : 0;
    }

}
