package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.tileentity.SolderingStationBlockEntity;
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

public class SolderingStationContainer extends AbstractContainerMenu {

    public final SolderingStationBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntReferenceHolder currentSmeltTime;
    public FunctionalIntReferenceHolder currentEnergy;
    private final Inventory playerInventory;

    public SolderingStationContainer(final int id, final Inventory player, final SolderingStationBlockEntity blockEntity) {
        super(ModContainerTypes.SOLDERING_STATION.get(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.playerInventory = player;

        IItemHandler inventory = blockEntity.getInventory();
        // 3x3 main grid
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(inventory, i * 3 + j, 42 + (j * 18), 17 + (i * 18)));
            }
        }
        this.addSlot(new SlotItemHandler(inventory, SolderingStationBlockEntity.SOLDER_ID, 132, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem().equals(RegistryHandler.SOLDER_WIRE.get());
            }
        });
        this.addSlot(new SlotItemHandler(inventory, SolderingStationBlockEntity.OUTPUT_ID, 136, 49) {
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

    public SolderingStationContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static SolderingStationBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof SolderingStationBlockEntity solderingStation) {
            return solderingStation;
        }
        throw new IllegalStateException("BlockEntity is not correct " + blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, RegistryHandler.SOLDERING_STATION.get());
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
                ? this.currentSmeltTime.get() * 24 / SolderingStationBlockEntity.MAX_SMELT_TIME
                : 0;
    }

    public int getCurrentEnergyScaled() {
        return this.currentEnergy.get() != 0
                ? this.currentEnergy.get() * 70 / SolderingStationBlockEntity.MAX_ENERGY
                : 0;
    }

}
