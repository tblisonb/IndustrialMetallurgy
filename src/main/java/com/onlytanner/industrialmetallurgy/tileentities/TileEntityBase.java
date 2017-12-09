package com.onlytanner.industrialmetallurgy.tileentities;

import java.util.Arrays;
import javax.annotation.Nullable;

import com.onlytanner.industrialmetallurgy.container.ContainerForgeTier1;
import com.onlytanner.industrialmetallurgy.items.crafting.ForgeRecipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class TileEntityBase extends TileEntityLockable implements IInventory, ITickable
{
    public final int FUEL_SLOTS_COUNT;
    public final int INPUT_SLOTS_COUNT;
    public final int OUTPUT_SLOTS_COUNT;
    public final int TOTAL_SLOTS_COUNT;

    public final int FIRST_FUEL_SLOT;
    public final int FIRST_INPUT_SLOT;
    public final int FIRST_OUTPUT_SLOT;

    protected ItemStack[] itemStacks; //Array of ItemStacks, one for each slot in the container
    protected int[] burnTimeRemaining; //The number of burn ticks remaining on the current piece of fuel
    protected int[] burnTimeInitialValue; //The initial fuel value of the currently burning fuel (in ticks of burn duration)
    protected short cookTime; //The number of ticks the current item has been cooking
    protected short totalCookTime;
    public static final short COOK_TIME_FOR_COMPLETION = 200;  // The number of ticks required to cook an item

    public TileEntityBase(int numInputSlots, int numOutputSlots, int numFuelSlots)
    {
        INPUT_SLOTS_COUNT = numInputSlots;
        OUTPUT_SLOTS_COUNT = numOutputSlots;
        FUEL_SLOTS_COUNT = numFuelSlots;

        TOTAL_SLOTS_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;
        FIRST_FUEL_SLOT = 0;
        FIRST_INPUT_SLOT = FIRST_FUEL_SLOT + FUEL_SLOTS_COUNT;
        FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT;

        itemStacks = new ItemStack[TOTAL_SLOTS_COUNT];
        burnTimeRemaining = new int[FUEL_SLOTS_COUNT];
        burnTimeInitialValue = new int[FUEL_SLOTS_COUNT];
    }

    /**
     * Returns the amount of fuel remaining on the currently burning item in the
     * given fuel slot.
     *
     * @fuelSlot the number of the fuel slot (0..3)
     * @return fraction remaining, between 0 - 1
     */
    public double fractionOfFuelRemaining(int fuelSlot)
    {
        if (burnTimeInitialValue[fuelSlot] <= 0)
        {
            return 0;
        }
        double fraction = burnTimeRemaining[fuelSlot] / (double) burnTimeInitialValue[fuelSlot];
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    /**
     * return the remaining burn time of the fuel in the given slot
     *
     * @param fuelSlot the number of the fuel slot (0..3)
     * @return seconds remaining
     */
    public int secondsOfFuelRemaining(int fuelSlot)
    {
        if (burnTimeRemaining[fuelSlot] <= 0)
        {
            return 0;
        }
        return burnTimeRemaining[fuelSlot] / 20; // 20 ticks per second
    }

    /**
     * Get the number of slots which have fuel burning in them.
     *
     * @return number of slots with burning fuel, 0 - FUEL_SLOTS_COUNT
     */
    public int numberOfBurningFuelSlots()
    {
        int burningCount = 0;
        for (int burnTime : burnTimeRemaining)
        {
            if (burnTime > 0)
            {
                ++burningCount;
            }
        }
        return burningCount;
    }

    /**
     * Returns the amount of cook time completed on the currently cooking item.
     *
     * @return fraction remaining, between 0 - 1
     */
    public double fractionOfCookTimeComplete()
    {
        double fraction = cookTime / (double) COOK_TIME_FOR_COMPLETION;
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    /**
     * This method is called every tick to update the tile entity, i.e. - see if
     * the fuel has run out, and if so turn the furnace "off" and slowly uncook
     * the current item (if any) - see if any of the items have finished
     * smelting It runs both on the server and the client.
     */
    @Override
    public abstract void update();

    public static int getItemBurnTime(ItemStack stack)
    {
        return TileEntityFurnace.getItemBurnTime(stack) / 2;
    }

    public boolean isBurning()
    {
        return cookTime > 1;
    }

    // returns the smelting result for the given stack. Returns null if the given stack can not be smelted
    public static ItemStack getSmeltingResultForItem(ItemStack stack, int numOfOutput)
    {
        try
        {
            return new ItemStack(FurnaceRecipes.instance().getSmeltingResult(stack).getItem(), numOfOutput);
        } 
        catch (NullPointerException e)
        {
            return null;
        }
    }

    public static ItemStack getAlloyResultForItem(ItemStack stack1, ItemStack stack2)
    {
        try
        {
            return ForgeRecipes.getInstance().getAlloyResult(stack1, stack2);
        } 
        catch (NullPointerException e)
        {
            return null;
        }
    }

    // Gets the number of slots in the inventory
    @Override
    public int getSizeInventory()
    {
        return itemStacks.length;
    }

    // Gets the stack in the given slot
    @Override
    public ItemStack getStackInSlot(int i)
    {
        return itemStacks[i];
    }

    /**
     * Removes some of the units from itemstack in the given slot, and returns
     * as a separate itemstack
     *
     * @param slotIndex the slot number to remove the items from
     * @param count the number of units to remove
     * @return a new itemstack containing the units removed from the slot
     */
    @Override
    public ItemStack decrStackSize(int slotIndex, int count)
    {
        ItemStack itemStackInSlot = getStackInSlot(slotIndex);
        if (itemStackInSlot == null)
            return null;


        ItemStack itemStackRemoved;
        if (itemStackInSlot.stackSize <= count) 
        {
            itemStackRemoved = itemStackInSlot;
            setInventorySlotContents(slotIndex, null);
        } 
        else 
        {
            itemStackRemoved = itemStackInSlot.splitStack(count);
            if (itemStackInSlot.stackSize == 0)
                setInventorySlotContents(slotIndex, null);
        }
        markDirty();
        return itemStackRemoved;
    }

    // overwrites the stack in the given slotIndex with the given stack
    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack itemstack)
    {
        itemStacks[slotIndex] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    // This is the maximum number if items allowed in each slot
    // This only affects things such as hoppers trying to insert items you need to use the container to enforce this for players
    // inserting items via the gui
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    // Return true if the given player is able to use this block. In this case it checks that
    // 1) the world tileentity hasn't been replaced in the meantime, and
    // 2) the player isn't too far away from the centre of the block
    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        if (this.worldObj.getTileEntity(this.pos) != this)
        {
            return false;
        }
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    // Return true if the given stack is allowed to be inserted in the given slot
    // Unlike the vanilla furnace, we allow anything to be placed in the fuel slots
    public static boolean isItemValidForFuelSlot(ItemStack itemStack)
    {
        return TileEntityFurnace.isItemFuel(itemStack);
    }

    // Return true if the given stack is allowed to be inserted in the given slot
    // Unlike the vanilla furnace, we allow anything to be placed in the fuel slots
    public static boolean isItemValidForInputSlot(ItemStack itemStack)
    {
        return true;
    }

    // Return true if the given stack is allowed to be inserted in the given slot
    // Unlike the vanilla furnace, we allow anything to be placed in the fuel slots
    public static boolean isItemValidForOutputSlot(ItemStack itemStack)
    {
        return false;
    }

    //------------------------------
    // This is where you save any data that you don't want to lose when the tile entity unloads
    // In this case, it saves the state of the furnace (burn time etc) and the itemstacks stored in the fuel, input, and output slots
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound parentNBTTagCompound)
    {
        super.writeToNBT(parentNBTTagCompound); // The super call is required to save and load the tiles location

//		// Save the stored item stacks
        // to use an analogy with Java, this code generates an array of hashmaps
        // The itemStack in each slot is converted to an NBTTagCompound, which is effectively a hashmap of key->value pairs such
        //   as slot=1, id=2353, count=1, etc
        // Each of these NBTTagCompound are then inserted into NBTTagList, which is similar to an array.
        NBTTagList dataForAllSlots = new NBTTagList();
        for (int i = 0; i < this.itemStacks.length; ++i)
        {
            if (this.itemStacks[i] != null)
            {
                NBTTagCompound dataForThisSlot = new NBTTagCompound();
                dataForThisSlot.setByte("Slot", (byte) i);
                this.itemStacks[i].writeToNBT(dataForThisSlot);
                dataForAllSlots.appendTag(dataForThisSlot);
            }
        }
        // the array of hashmaps is then inserted into the parent hashmap for the container
        parentNBTTagCompound.setTag("Items", dataForAllSlots);

        // Save everything else
        parentNBTTagCompound.setShort("CookTime", cookTime);
        parentNBTTagCompound.setTag("burnTimeRemaining", new NBTTagIntArray(burnTimeRemaining));
        parentNBTTagCompound.setTag("burnTimeInitial", new NBTTagIntArray(burnTimeInitialValue));
        return parentNBTTagCompound;
    }

    // This is where you load the data that you saved in writeToNBT
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound); // The super call is required to save and load the tiles location
        final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType() for a listing
        NBTTagList dataForAllSlots = nbtTagCompound.getTagList("Items", NBT_TYPE_COMPOUND);

        Arrays.fill(itemStacks, null);           // set all slots to empty
        for (int i = 0; i < dataForAllSlots.tagCount(); ++i)
        {
            NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
            byte slotNumber = dataForOneSlot.getByte("Slot");
            if (slotNumber >= 0 && slotNumber < this.itemStacks.length)
            {
                this.itemStacks[slotNumber] = ItemStack.loadItemStackFromNBT(dataForOneSlot);
            }
        }

        // Load everything else.  Trim the arrays (or pad with 0) to make sure they have the correct number of elements
        cookTime = nbtTagCompound.getShort("CookTime");
        burnTimeRemaining = Arrays.copyOf(nbtTagCompound.getIntArray("burnTimeRemaining"), FUEL_SLOTS_COUNT);
        burnTimeInitialValue = Arrays.copyOf(nbtTagCompound.getIntArray("burnTimeInitial"), FUEL_SLOTS_COUNT);
    }

    // When the world loads from disk, the server needs to send the TileEntity information to the client
    //  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound updateTagDescribingTileEntityState = getUpdateTag();
        final int METADATA = 0;
        return new SPacketUpdateTileEntity(this.pos, METADATA, updateTagDescribingTileEntityState);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        NBTTagCompound updateTagDescribingTileEntityState = pkt.getNbtCompound();
        handleUpdateTag(updateTagDescribingTileEntityState);
    }

    /**
     * Creates a tag containing the TileEntity information, used by vanilla to
     * transmit from server to client Warning - although our getUpdatePacket()
     * uses this method, vanilla also calls it directly, so don't remove it.
     *
     * @return nbtTagCompound
     */
    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    /**
     * Populates this TileEntity with information from the tag, used by vanilla
     * to transmit from server to client Warning - vanilla calls it directly, so
     * don't remove it.
     *
     * @param tag
     */
    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);
    }

    /**
     * Clears all Slots in container.
     */
    @Override
    public void clear()
    {
        Arrays.fill(itemStacks, null);
    }

    /**
     * Adds a key to the lang file so it can be given a name
     *
     * @return name
     */
    @Override
    public abstract String getName();

    @Override
    public abstract boolean hasCustomName();

    // standard code to look up what the human-readable name is
    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public abstract int getField(int id);

    @Override
    public abstract void setField(int id, int value);

    @Override
    public abstract int getFieldCount();

    // -----------------------------------------------------------------------------------------------------------
    // The following methods are not needed for this example but are part of IInventory so they must be implemented
    // Unused unless your container specifically uses it.
    // Return true if the given stack is allowed to go in the given slot
    @Override
    public abstract boolean isItemValidForSlot(int slotIndex, ItemStack itemstack);

    /**
     * This method removes the entire contents of the given slot and returns it.
     * Used by containers such as crafting tables which return any items in
     * their slots when you close the GUI
     *
     * @param slotIndex
     * @return
     */
    @Override
    public ItemStack removeStackFromSlot(int slotIndex)
    {
        ItemStack itemStack = getStackInSlot(slotIndex);
        if (itemStack != null) {
            setInventorySlotContents(slotIndex, null);
        }
        return itemStack;
    }

    @Override
    public abstract void openInventory(EntityPlayer player);

    @Override
    public abstract void closeInventory(EntityPlayer player);

    /**
     * Returns a container object of the type this tile entity represents
     *
     * @param playerInventory
     * @param playerIn
     * @return container
     */
    @Override
    public abstract Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn);

    /**
     * Return a String ID of the tile entity - i.e. return
     * "modname:tile_entity_name";
     *
     * @return String ID
     */
    @Override
    public abstract String getGuiID();
}
