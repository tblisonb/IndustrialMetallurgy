package com.onlytanner.industrialmetallurgy.tileentities;

import com.onlytanner.industrialmetallurgy.blocks.BlockCoalGenerator;

import com.onlytanner.industrialmetallurgy.container.ContainerCoalGenerator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;

public class TileEntityCoalGenerator extends TileEntityBase implements IEnergyProvider
{
    private final int MAX_CAPACITY = 500000;
    private final ModEnergyStorage storage = new ModEnergyStorage(MAX_CAPACITY, 0, 80);

    public TileEntityCoalGenerator()
    {
        super(0, 0, 1);
    }

    @Override
    public void update()
    {
        // If there is nothing to smelt or there is no room in the output, reset cookTime and return
        boolean flag = this.isBurning();
        boolean flag1 = false;

        if (getItemBurnTime(itemStacks[0]) > 0) {
            burnFuel();
        }
        if (flag != this.isBurning()) {
            flag1 = true;
            BlockCoalGenerator.setState(flag, this.worldObj, this.pos);
        }

        if (isBurning()) {
            storage.receiveEnergyInternal(80, false);
        }

        if (flag1) {
            this.markDirty();
        }
    }

    /**
     * for each fuel slot: decreases the burn time, checks if burnTimeRemaining
     * = 0 and tries to consume a new piece of fuel if one is available
     *
     * @return the number of fuel slots which are burning
     */
    private int burnFuel()
    {
        int burningCount = 0;
        boolean inventoryChanged = false;
        // Iterate over all the fuel slots
        for (int i = 0; i < FUEL_SLOTS_COUNT; i++) {
            int fuelSlotNumber = i + FIRST_FUEL_SLOT;
            if (burnTimeRemaining[i] > 0) {
                --burnTimeRemaining[i];
                ++burningCount;
            }
            if (burnTimeRemaining[i] == 0) {
                if (itemStacks[fuelSlotNumber] != null && getItemBurnTime(itemStacks[fuelSlotNumber]) > 0) {
                    // If the stack in this slot is not null and is fuel, set burnTimeRemaining & burnTimeInitialValue to the
                    // item's burn time and decrease the stack size
                    burnTimeRemaining[i] = burnTimeInitialValue[i] = getItemBurnTime(itemStacks[fuelSlotNumber]);
                    --itemStacks[fuelSlotNumber].stackSize;
                    ++burningCount;
                    inventoryChanged = true;
                    // If the stack size now equals 0 set the slot contents to the items container item. This is for fuel
                    // items such as lava buckets so that the bucket is not consumed. If the item dose not have
                    // a container item getContainerItem returns null which sets the slot contents to null
                    if (itemStacks[fuelSlotNumber].stackSize == 0) {
                        itemStacks[fuelSlotNumber] = itemStacks[fuelSlotNumber].getItem().getContainerItem(itemStacks[fuelSlotNumber]);
                    }
                }
            }
        }
        if (inventoryChanged) {
            markDirty();
        }
        return burningCount;
    }

    @Override
    public String getName()
    {
        return "           Coal Generator";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public int getField(int id)
    {
        switch (id) {
            case 0:
                return this.burnTimeInitialValue[0];
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value)
    {
        switch (id) {
            case 0:
                this.burnTimeInitialValue[0] = value;
                break;
        }
    }

    @Override
    public int getFieldCount()
    {
        return 1;
    }

    @Override
    public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack)
    {
        return TileEntityFurnace.getItemBurnTime(itemstack) > 0;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerCoalGenerator(playerInventory, this);
    }

    @Override
    public String getGuiID()
    {
        return "industrialmetallurgy:coal_generator";
    }

    @Override
    public int getEnergyToSplitShare()
    {
        return this.storage.getEnergyStored();
    }

    public double getFractionOfEnergyRemaining()
    {
        return (double) getEnergyToSplitShare() / (double) this.MAX_CAPACITY;
    }

    @Override
    public boolean doesShareEnergy()
    {
        return true;
    }

    @Override
    public EnumFacing[] getEnergyShareSides()
    {
        return EnumFacing.values();
    }

    @Override
    public boolean canShareTo(TileEntity entity)
    {
        return true;
    }
}