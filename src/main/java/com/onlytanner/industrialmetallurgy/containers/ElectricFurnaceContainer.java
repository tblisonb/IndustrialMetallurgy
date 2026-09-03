package com.onlytanner.industrialmetallurgy.containers;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.tileentity.ElectricFurnaceBlockEntity;
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

public class ElectricFurnaceContainer extends AbstractContainerMenu {

    public final ElectricFurnaceBlockEntity blockEntity;
    private final ContainerLevelAccess canInteractWithCallable;
    public FunctionalIntReferenceHolder currentSmeltTime;
    public FunctionalIntReferenceHolder currentEnergy;

    public ElectricFurnaceContainer(final int id, final Inventory player, final ElectricFurnaceBlockEntity blockEntity) {
        super(ModContainerTypes.ELECTRIC_FURNACE.get(), id);
        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        ModItemHandler inventory = blockEntity.getInventory();
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, ElectricFurnaceBlockEntity.INPUT_ID, 56, 35));
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, ElectricFurnaceBlockEntity.HEATING_ELEMENT_ID, 152, 8) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem().equals(RegistryHandler.HEATING_ELEMENT.get());
            }
        });
        this.addSlot(new ResourceHandlerSlot(inventory, inventory::set, ElectricFurnaceBlockEntity.OUTPUT_ID, 116, 35) {
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

    public ElectricFurnaceContainer(final int id, final Inventory player, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        this(id, player, getBlockEntity(player, data));
    }

    private static ElectricFurnaceBlockEntity getBlockEntity(final Inventory playerInv, final net.minecraft.network.RegistryFriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        var blockEntity = playerInv.player.level().getBlockEntity(pos);
        if (blockEntity instanceof ElectricFurnaceBlockEntity electricFurnace) {
            return electricFurnace;
        }
        throw new IllegalStateException("BlockEntity is not correct " + blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(canInteractWithCallable, player, RegistryHandler.ELECTRIC_FURNACE.get());
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

    // Every ported smelting recipe (Part 1) uses the default 200-tick cooking time; this is
    // purely a display scale, so it doesn't need to track a specific recipe's cookingTime().
    private static final int DISPLAY_SMELT_TIME = 200;

    public int getSmeltProgressionScaled() {
        return this.currentSmeltTime.get() != 0
                ? Math.min(24, this.currentSmeltTime.get() * 24 / DISPLAY_SMELT_TIME)
                : 0;
    }

    public int getCurrentEnergyScaled() {
        return this.currentEnergy.get() != 0
                ? this.currentEnergy.get() * 70 / ElectricFurnaceBlockEntity.MAX_ENERGY
                : 0;
    }

}
