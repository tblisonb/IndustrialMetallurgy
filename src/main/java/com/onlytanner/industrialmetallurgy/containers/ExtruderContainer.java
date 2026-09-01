package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.tileentity.ExtruderBlockEntity;
import com.onlytanner.industrialmetallurgy.util.FunctionalIntReferenceHolder;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ExtruderContainer extends AbstractContainerMenu {

    public final ExtruderBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntReferenceHolder currentSmeltTime;
    public FunctionalIntReferenceHolder currentEnergy;
    private final Inventory playerInventory;

    public ExtruderContainer(final int id, final Inventory player, final ExtruderBlockEntity blockEntity) {
        super(ModContainerTypes.EXTRUDER.get(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.playerInventory = player;

        IItemHandler inventory = blockEntity.getInventory();
        this.addSlot(new SlotItemHandler(inventory, ExtruderBlockEntity.INPUT_ID, 56, 35));
        this.addSlot(new SlotItemHandler(inventory, ExtruderBlockEntity.OUTPUT_ID, 116, 35) {
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

        this.addDataSlot(currentSmeltTime = new FunctionalIntReferenceHolder(() -> this.blockEntity.currentSmeltTime, value -> this.blockEntity.currentSmeltTime = value));
        this.addDataSlot(currentEnergy = new FunctionalIntReferenceHolder(this.blockEntity::getEnergyAmount, this.blockEntity::setEnergyAmount));
    }

    public ExtruderContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static ExtruderBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof ExtruderBlockEntity extruder) {
            return extruder;
        }
        throw new IllegalStateException("BlockEntity is not correct " + blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, RegistryHandler.EXTRUDER.get());
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
            } else if (!this.moveItemStackTo(slotStack, 0, machineSlots, false)) {
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
                ? this.currentSmeltTime.get() * 18 / ExtruderBlockEntity.MAX_SMELT_TIME
                : 0;
    }

    public int getCurrentEnergyScaled() {
        return this.currentEnergy.get() != 0
                ? this.currentEnergy.get() * 70 / ExtruderBlockEntity.MAX_ENERGY
                : 0;
    }

}
