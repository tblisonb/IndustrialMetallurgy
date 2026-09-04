package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.tileentity.ChemicalReactorBlockEntity;
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
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class ChemicalReactorContainer extends AbstractContainerMenu {

    public final ChemicalReactorBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntReferenceHolder currentSmeltTime;
    public FunctionalIntReferenceHolder currentEnergy;
    private final Inventory playerInventory;

    public ChemicalReactorContainer(final int id, final Inventory player, final ChemicalReactorBlockEntity blockEntity) {
        super(ModContainerTypes.CHEMICAL_REACTOR.get(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.playerInventory = player;

        ModItemHandler inventory = blockEntity.getInventory();
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, ChemicalReactorBlockEntity.INPUT_ID, 57, 24));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, ChemicalReactorBlockEntity.INPUT_ID + 1, 80, 17));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, ChemicalReactorBlockEntity.INPUT_ID + 2, 103, 24));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, ChemicalReactorBlockEntity.BOTTLE_ID, 152, 62) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem().equals(Items.GLASS_BOTTLE);
            }
        });
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, ChemicalReactorBlockEntity.OUTPUT_ID, 80, 58) {
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

    public ChemicalReactorContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static ChemicalReactorBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof ChemicalReactorBlockEntity reactor) {
            return reactor;
        }
        throw new IllegalStateException("BlockEntity is not correct " + blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, RegistryHandler.CHEMICAL_REACTOR.get());
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

    public int getSmeltProgressionScaled() {
        return this.currentSmeltTime.get() != 0
                ? this.currentSmeltTime.get() * 28 / ChemicalReactorBlockEntity.MAX_SMELT_TIME
                : 0;
    }

    public int getCurrentEnergyScaled() {
        return this.currentEnergy.get() != 0
                ? this.currentEnergy.get() * 70 / ChemicalReactorBlockEntity.MAX_ENERGY
                : 0;
    }

}
