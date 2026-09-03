package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.tileentity.BasicForgeBlockEntity;
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

/** Shared by all solid-fuel forge tiers; only the registered MenuType (read off the block entity) differs. */
public class BasicForgeContainer extends AbstractContainerMenu {

    public final BasicForgeBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntReferenceHolder currentSmeltTime;
    public FunctionalIntReferenceHolder burnTimeRemaining;
    public FunctionalIntReferenceHolder currentTemperature;

    public BasicForgeContainer(final int id, final Inventory player, final BasicForgeBlockEntity blockEntity) {
        super(blockEntity.getMenuType(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        ModItemHandler inventory = blockEntity.getInventory();
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, 0, 47, 22));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, 1, 73, 22));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, 2, 47, 48));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, 3, 73, 48));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, BasicForgeBlockEntity.FUEL_ID, 17, 35));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, BasicForgeBlockEntity.OUTPUT_ID, 127, 35) {
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
        this.addDataSlot(burnTimeRemaining = new FunctionalIntReferenceHolder(() -> this.blockEntity.burnTimeRemaining, value -> this.blockEntity.burnTimeRemaining = value));
        this.addDataSlot(currentTemperature = new FunctionalIntReferenceHolder(() -> this.blockEntity.currentTemperature, value -> this.blockEntity.currentTemperature = value));
    }

    public BasicForgeContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static BasicForgeBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof BasicForgeBlockEntity forge) {
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
                ? this.currentSmeltTime.get() * 24 / this.blockEntity.maxSmeltTime
                : 0;
    }

    public int getBurnTimeScaled() {
        return this.burnTimeRemaining.get() != 0
                ? this.burnTimeRemaining.get() * 14 / BasicForgeBlockEntity.MAX_BURN_TIME
                : 0;
    }

    public int getTemperatureScaled() {
        return this.currentTemperature.get() != 0
                ? this.currentTemperature.get() * 70 / this.blockEntity.maxTemperature
                : 0;
    }

}
