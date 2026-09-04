package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.tileentity.AdvancedForgeBlockEntity;
import com.onlytanner.industrialmetallurgy.util.FunctionalIntReferenceHolder;
import com.onlytanner.industrialmetallurgy.util.ModItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

/** Shared by the electric forge tiers (3-4); only the registered MenuType (read off the block entity) differs. */
public class AdvancedForgeContainer extends AbstractContainerMenu {

    public final AdvancedForgeBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    // The real number of this container's own slots -- NOT derivable from
    // player.getInventory().getContainerSize(), which in this MC version also counts the
    // offhand/body-armor/saddle equipment slots and is nowhere near 36, silently making every
    // quickMoveStack's machineSlots computation negative if it's used for that subtraction.
    private final int machineSlotCount;
    public FunctionalIntReferenceHolder currentSmeltTime;
    public FunctionalIntReferenceHolder currentEnergy;
    public FunctionalIntReferenceHolder currentTemperature;
    public FunctionalIntReferenceHolder formed;

    public AdvancedForgeContainer(final int id, final Inventory player, final AdvancedForgeBlockEntity blockEntity) {
        super(blockEntity.getMenuType(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        ModItemHandler inventory = blockEntity.getInventory();
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, 0, 47, 22));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, 1, 73, 22));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, 2, 47, 48));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, 3, 73, 48));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, AdvancedForgeBlockEntity.OUTPUT_ID, 127, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

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

        this.addDataSlot(currentSmeltTime = new FunctionalIntReferenceHolder(() -> this.blockEntity.currentSmeltTime, value -> this.blockEntity.currentSmeltTime = value));
        this.addDataSlot(currentEnergy = new FunctionalIntReferenceHolder(this.blockEntity::getEnergyAmount, this.blockEntity::setEnergyAmount));
        this.addDataSlot(currentTemperature = new FunctionalIntReferenceHolder(() -> this.blockEntity.currentTemperature, value -> this.blockEntity.currentTemperature = value));
        this.addDataSlot(formed = new FunctionalIntReferenceHolder(
                () -> this.blockEntity.isFormed() ? 1 : 0, value -> this.blockEntity.setFormed(value != 0)));
    }

    public AdvancedForgeContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static AdvancedForgeBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof AdvancedForgeBlockEntity forge) {
            return forge;
        }
        throw new IllegalStateException("BlockEntity is not correct " + blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, blockEntity.getBlockState().getBlock());
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

    public int getSmeltProgressionScaled() {
        return this.currentSmeltTime.get() != 0
                ? this.currentSmeltTime.get() * 24 / this.blockEntity.maxSmeltTime
                : 0;
    }

    public int getCurrentEnergyScaled() {
        return this.currentEnergy.get() != 0
                ? this.currentEnergy.get() * 70 / AdvancedForgeBlockEntity.MAX_ENERGY
                : 0;
    }

    public int getTemperatureScaled() {
        return this.currentTemperature.get() != 0
                ? this.currentTemperature.get() * 70 / this.blockEntity.maxTemperature
                : 0;
    }

    public boolean isFormed() {
        return this.formed.get() != 0;
    }

}
