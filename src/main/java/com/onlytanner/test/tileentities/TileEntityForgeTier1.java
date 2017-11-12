package com.onlytanner.test.tileentities;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.onlytanner.test.blocks.BlockForgeTier1;
import com.onlytanner.test.container.ContainerForgeTier1;
import com.onlytanner.test.items.crafting.CrusherRecipes;
import com.onlytanner.test.items.crafting.ForgeRecipes;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
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
import net.minecraft.world.EnumSkyBlock;

public class TileEntityForgeTier1 extends TileEntityLockable implements IInventory, ITickable {

    public static final int FUEL_SLOTS_COUNT = 1;
    public static final int INPUT_SLOTS_COUNT = 2;
    public static final int OUTPUT_SLOTS_COUNT = 2;
    public static final int TOTAL_SLOTS_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

    public static final int FIRST_FUEL_SLOT = 0;
    public static final int FIRST_INPUT_SLOT = FIRST_FUEL_SLOT + FUEL_SLOTS_COUNT;
    public static final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT;

    private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOTS_COUNT];

    /**
     * The number of burn ticks remaining on the current piece of fuel
     */
    private int[] burnTimeRemaining = new int[FUEL_SLOTS_COUNT];
    /**
     * The initial fuel value of the currently burning fuel (in ticks of burn
     * duration)
     */
    private int[] burnTimeInitialValue = new int[FUEL_SLOTS_COUNT];

    /**
     * The number of ticks the current item has been cooking
     */
    private short cookTime;
    /**
     * The number of ticks required to cook an item
     */
    private static final short COOK_TIME_FOR_COMPLETION = 200;  // vanilla value is 200 = 10 seconds

    private int cachedNumberOfBurningSlots = -1;
    private short totalCookTime;

    /**
     * Returns the amount of fuel remaining on the currently burning item in the
     * given fuel slot.
     *
     * @fuelSlot the number of the fuel slot (0..3)
     * @return fraction remaining, between 0 - 1
     */
    public double fractionOfFuelRemaining(int fuelSlot) {
        if (burnTimeInitialValue[fuelSlot] <= 0) {
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
    public int secondsOfFuelRemaining(int fuelSlot) {
        if (burnTimeRemaining[fuelSlot] <= 0) {
            return 0;
        }
        return burnTimeRemaining[fuelSlot] / 20; // 20 ticks per second
    }

    /**
     * Get the number of slots which have fuel burning in them.
     *
     * @return number of slots with burning fuel, 0 - FUEL_SLOTS_COUNT
     */
    public int numberOfBurningFuelSlots() {
        int burningCount = 0;
        for (int burnTime : burnTimeRemaining) {
            if (burnTime > 0) {
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
    public double fractionOfCookTimeComplete() {
        double fraction = cookTime / (double) COOK_TIME_FOR_COMPLETION;
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    // This method is called every tick to update the tile entity, i.e.
    // - see if the fuel has run out, and if so turn the furnace "off" and slowly uncook the current item (if any)
    // - see if any of the items have finished smelting
    // It runs both on the server and the client.
    @Override
    public void update() {
        // If there is nothing to smelt or there is no room in the output, reset cookTime and return
        boolean flag = this.isBurning();
        boolean flag1 = false;

        if (smeltItem(false)) {
            int numberOfFuelBurning = burnFuel();
            // If fuel is available, keep cooking the item, otherwise start "uncooking" it at double speed
            if (numberOfFuelBurning > 0) {
                cookTime += numberOfFuelBurning;
            } else {
                cookTime -= 2;
            }

            if (cookTime < 0) {
                cookTime = 0;
            }

            // If cookTime has reached maxCookTime smelt the item and reset cookTime
            if (cookTime >= COOK_TIME_FOR_COMPLETION) {
                smeltItem(true);
                cookTime = 0;
            }
        } else if (burnTimeRemaining[0] > 1) {
            cookTime = 0;
            burnFuel();
        } else {
            cookTime = 0;
        }

        if (flag != this.isBurning()) {
            flag1 = true;
            BlockForgeTier1.setState(this.isBurning(), this.worldObj, this.pos);
        }

        // when the number of burning slots changes, we need to force the block to re-render, otherwise the change in
        //   state will not be visible.  Likewise, we need to force a lighting recalculation.
        // The block update (for renderer) is only required on client side, but the lighting is required on both, since
        //    the client needs it for rendering and the server needs it for crop growth etc
        int numberBurning = numberOfBurningFuelSlots();
        if (cachedNumberOfBurningSlots != numberBurning) {
            cachedNumberOfBurningSlots = numberBurning;
            if (worldObj.isRemote) {
                IBlockState iblockstate = this.worldObj.getBlockState(pos);
                final int FLAGS = 3;  // I'm not sure what these flags do, exactly.
                worldObj.notifyBlockUpdate(pos, iblockstate, iblockstate, FLAGS);
            }
            worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
        }
        if (flag1) {
            this.markDirty();
        }
    }

    public static int getItemBurnTime(ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime(stack) / 2;
    }

    public boolean isBurning() {
        return cookTime > 1;
    }

    /**
     * for each fuel slot: decreases the burn time, checks if burnTimeRemaining
     * = 0 and tries to consume a new piece of fuel if one is available
     *
     * @return the number of fuel slots which are burning
     */
    private int burnFuel() {
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

    /**
     * checks that there is an item to be smelted in one of the input slots and
     * that there is room for the result in the output slots If desired,
     * performs the smelt
     *
     * @param performSmelt if true, perform the smelt. if false, check whether
     * smelting is possible, but don't change the inventory
     * @return false if no items can be smelted, true otherwise
     */
    private boolean smeltItem(boolean performSmelt) {
        if (((itemStacks[FIRST_INPUT_SLOT] != null && itemStacks[FIRST_INPUT_SLOT + 1] != null) && itemStacks[FIRST_INPUT_SLOT].isItemEqual(itemStacks[FIRST_INPUT_SLOT + 1]))) {
            return getResult(0, 2, performSmelt);
        } else if (itemStacks[FIRST_INPUT_SLOT] != null && itemStacks[FIRST_INPUT_SLOT + 1] == null) {
            return getResult(0, 1, performSmelt);
        } else if (itemStacks[FIRST_INPUT_SLOT] == null && itemStacks[FIRST_INPUT_SLOT + 1] != null) {
            return getResult(1, 1, performSmelt);
        } else if (itemStacks[FIRST_INPUT_SLOT] != null && itemStacks[FIRST_INPUT_SLOT + 1] != null) {
            return getResult(performSmelt);
        } else {
            return false;
        }
    }

    private boolean getResult(int index, int numOfOutputs, boolean performSmelt) {
        ItemStack result = getSmeltingResultForItem(itemStacks[FIRST_INPUT_SLOT + index], numOfOutputs);

        if ((itemStacks[FIRST_INPUT_SLOT] == null && itemStacks[FIRST_INPUT_SLOT + 1] == null) || result == null) {
            return false;
        }
        if (!performSmelt
                && (itemStacks[FIRST_OUTPUT_SLOT] == null || itemStacks[FIRST_OUTPUT_SLOT + 1] == null
                || (itemStacks[FIRST_OUTPUT_SLOT] != null && itemStacks[FIRST_OUTPUT_SLOT].isItemEqual(result))
                || (itemStacks[FIRST_OUTPUT_SLOT + 1] != null && itemStacks[FIRST_OUTPUT_SLOT + 1].isItemEqual(result)))) {
            return true;
        }

        // alter input and output
        if (numOfOutputs == 1) {
            if (itemStacks[FIRST_INPUT_SLOT + index].stackSize <= 1) {
                itemStacks[FIRST_INPUT_SLOT + index] = null;
            } else {
                itemStacks[FIRST_INPUT_SLOT + index].stackSize--;
            }
        } else if (itemStacks[FIRST_INPUT_SLOT + index].stackSize <= 1 && itemStacks[FIRST_INPUT_SLOT + index + 1].stackSize <= 1) {
            itemStacks[FIRST_INPUT_SLOT + index] = null;
            itemStacks[FIRST_INPUT_SLOT + index + 1] = null;
        } else if (itemStacks[FIRST_INPUT_SLOT + index].stackSize <= 1 && itemStacks[FIRST_INPUT_SLOT + index + 1].stackSize > 1) {
            itemStacks[FIRST_INPUT_SLOT + index] = null;
            itemStacks[FIRST_INPUT_SLOT + index + 1].stackSize--;
        } else if (itemStacks[FIRST_INPUT_SLOT + index].stackSize > 1 && itemStacks[FIRST_INPUT_SLOT + index + 1].stackSize <= 1) {
            itemStacks[FIRST_INPUT_SLOT + index].stackSize--;
            itemStacks[FIRST_INPUT_SLOT + index + 1] = null;
        } else {
            itemStacks[FIRST_INPUT_SLOT + index].stackSize--;
            itemStacks[FIRST_INPUT_SLOT + index + 1].stackSize--;
        }

        if (itemStacks[FIRST_OUTPUT_SLOT] == null) {
            itemStacks[FIRST_OUTPUT_SLOT] = result.copy(); // Use deep .copy() to avoid altering the recipe
        } else if (itemStacks[FIRST_OUTPUT_SLOT].stackSize + result.stackSize <= this.getInventoryStackLimit()) {
            if (itemStacks[FIRST_OUTPUT_SLOT].getItem() == result.getItem()) {
                itemStacks[FIRST_OUTPUT_SLOT].stackSize += result.stackSize;
            } else if (itemStacks[FIRST_OUTPUT_SLOT + 1] == null) {
                itemStacks[FIRST_OUTPUT_SLOT + 1] = result.copy();
            } else if (itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize + result.stackSize <= this.getInventoryStackLimit() && result.getItem() == itemStacks[FIRST_OUTPUT_SLOT + 1].getItem()) {
                itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize += result.stackSize;
            } else {
                return false;
            }
        } else if (itemStacks[FIRST_OUTPUT_SLOT].stackSize + result.stackSize > this.getInventoryStackLimit() && itemStacks[FIRST_OUTPUT_SLOT].stackSize < this.getInventoryStackLimit()) {
            if (itemStacks[FIRST_OUTPUT_SLOT].getItem() == result.getItem()) {
                int temp = itemStacks[FIRST_OUTPUT_SLOT].stackSize;
                itemStacks[FIRST_OUTPUT_SLOT].stackSize = this.getInventoryStackLimit();
                if (itemStacks[FIRST_OUTPUT_SLOT + 1] == null) {
                    itemStacks[FIRST_OUTPUT_SLOT + 1] = new ItemStack(result.getItem(), result.stackSize - (this.getInventoryStackLimit() - temp));
                } else if (itemStacks[FIRST_OUTPUT_SLOT + 1].getItem() == result.getItem()) {
                    itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize += this.getInventoryStackLimit() - temp;
                } else {
                    return false;
                }
            } else {
                if (itemStacks[FIRST_OUTPUT_SLOT + 1] == null) {
                    itemStacks[FIRST_OUTPUT_SLOT + 1] = result.copy();
                } else if (itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize + result.stackSize <= this.getInventoryStackLimit() && result.getItem() == itemStacks[FIRST_OUTPUT_SLOT + 1].getItem()) {
                    itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize += result.stackSize;
                } else {
                    return false;
                }
            }
        } else if (itemStacks[FIRST_OUTPUT_SLOT].stackSize == this.getInventoryStackLimit()) {
            if (itemStacks[FIRST_OUTPUT_SLOT + 1] == null) {
                itemStacks[FIRST_OUTPUT_SLOT + 1] = result.copy();
            } else if (itemStacks[FIRST_OUTPUT_SLOT + 1].getItem() == result.getItem() && itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize + result.stackSize <= this.getInventoryStackLimit()) {
                itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize += result.stackSize;
            } else {
                return false;
            }
        } else {
            return false;
        }

        markDirty();
        return true;
    }

    private boolean getResult(boolean performSmelt) {
        ItemStack result = getAlloyResultForItem(itemStacks[FIRST_INPUT_SLOT], itemStacks[FIRST_INPUT_SLOT + 1]);

        if ((itemStacks[FIRST_INPUT_SLOT] == null || itemStacks[FIRST_INPUT_SLOT + 1] == null) || result == null) {
            return false;
        }
        if (!performSmelt
                && (itemStacks[FIRST_OUTPUT_SLOT] == null || itemStacks[FIRST_OUTPUT_SLOT + 1] == null
                || (itemStacks[FIRST_OUTPUT_SLOT] != null && itemStacks[FIRST_OUTPUT_SLOT].isItemEqual(result))
                || (itemStacks[FIRST_OUTPUT_SLOT + 1] != null && itemStacks[FIRST_OUTPUT_SLOT + 1].isItemEqual(result)))) {
            return true;
        }

        // alter input and output
        ItemStack[] stack = ForgeRecipes.getReducedStacks(itemStacks[FIRST_INPUT_SLOT], itemStacks[FIRST_INPUT_SLOT + 1]);
        if (itemStacks[FIRST_INPUT_SLOT].stackSize <= stack[0].stackSize) {
            if (itemStacks[FIRST_INPUT_SLOT + 1].stackSize <= stack[1].stackSize) {
                itemStacks[FIRST_INPUT_SLOT] = null;
                itemStacks[FIRST_INPUT_SLOT + 1] = null;
            } else {
                itemStacks[FIRST_INPUT_SLOT] = null;
                itemStacks[FIRST_INPUT_SLOT + 1] = new ItemStack(itemStacks[FIRST_INPUT_SLOT + 1].getItem(), itemStacks[FIRST_INPUT_SLOT + 1].stackSize - stack[1].stackSize);
            }
        } else if (itemStacks[FIRST_INPUT_SLOT + 1].stackSize <= stack[1].stackSize) {
            itemStacks[FIRST_INPUT_SLOT] = new ItemStack(itemStacks[FIRST_INPUT_SLOT].getItem(), itemStacks[FIRST_INPUT_SLOT].stackSize - stack[0].stackSize);
            itemStacks[FIRST_INPUT_SLOT + 1] = null;
        } else {
            itemStacks[FIRST_INPUT_SLOT] = new ItemStack(itemStacks[FIRST_INPUT_SLOT].getItem(), itemStacks[FIRST_INPUT_SLOT].stackSize - stack[0].stackSize);
            itemStacks[FIRST_INPUT_SLOT + 1] = new ItemStack(itemStacks[FIRST_INPUT_SLOT + 1].getItem(), itemStacks[FIRST_INPUT_SLOT + 1].stackSize - stack[1].stackSize);
        }

        if (itemStacks[FIRST_OUTPUT_SLOT] == null) {
            itemStacks[FIRST_OUTPUT_SLOT] = result.copy(); // Use deep .copy() to avoid altering the recipe
        } else if (itemStacks[FIRST_OUTPUT_SLOT].stackSize + result.stackSize <= this.getInventoryStackLimit()) {
            if (itemStacks[FIRST_OUTPUT_SLOT].getItem() == result.getItem()) {
                itemStacks[FIRST_OUTPUT_SLOT].stackSize += result.stackSize;
            } else if (itemStacks[FIRST_OUTPUT_SLOT + 1] == null) {
                itemStacks[FIRST_OUTPUT_SLOT + 1] = result.copy();
            } else if (itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize + result.stackSize <= this.getInventoryStackLimit() && result.getItem() == itemStacks[FIRST_OUTPUT_SLOT + 1].getItem()) {
                itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize += result.stackSize;
            } else {
                return false;
            }
        } else if (itemStacks[FIRST_OUTPUT_SLOT].stackSize + result.stackSize > this.getInventoryStackLimit() && itemStacks[FIRST_OUTPUT_SLOT].stackSize < this.getInventoryStackLimit()) {
            if (itemStacks[FIRST_OUTPUT_SLOT].getItem() == result.getItem()) {
                int temp = itemStacks[FIRST_OUTPUT_SLOT].stackSize;
                itemStacks[FIRST_OUTPUT_SLOT].stackSize = this.getInventoryStackLimit();
                if (itemStacks[FIRST_OUTPUT_SLOT + 1] == null) {
                    itemStacks[FIRST_OUTPUT_SLOT + 1] = new ItemStack(result.getItem(), result.stackSize - (this.getInventoryStackLimit() - temp));
                } else if (itemStacks[FIRST_OUTPUT_SLOT + 1].getItem() == result.getItem()) {
                    itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize += this.getInventoryStackLimit() - temp;
                } else {
                    return false;
                }
            } else {
                if (itemStacks[FIRST_OUTPUT_SLOT + 1] == null) {
                    itemStacks[FIRST_OUTPUT_SLOT + 1] = result.copy();
                } else if (itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize + result.stackSize <= this.getInventoryStackLimit() && result.getItem() == itemStacks[FIRST_OUTPUT_SLOT + 1].getItem()) {
                    itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize += result.stackSize;
                } else {
                    return false;
                }
            }
        } else if (itemStacks[FIRST_OUTPUT_SLOT].stackSize == this.getInventoryStackLimit()) {
            if (itemStacks[FIRST_OUTPUT_SLOT + 1] == null) {
                itemStacks[FIRST_OUTPUT_SLOT + 1] = result.copy();
            } else if (itemStacks[FIRST_OUTPUT_SLOT + 1].getItem() == result.getItem() && itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize + result.stackSize <= this.getInventoryStackLimit()) {
                itemStacks[FIRST_OUTPUT_SLOT + 1].stackSize += result.stackSize;
            } else {
                return false;
            }
        } else {
            return false;
        }

        markDirty();
        return false;
    }

    // returns the smelting result for the given stack. Returns null if the given stack can not be smelted
    public static ItemStack getSmeltingResultForItem(ItemStack stack, int numOfOutput) {
        try {
            return new ItemStack(FurnaceRecipes.instance().getSmeltingResult(stack).getItem(), numOfOutput);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static ItemStack getAlloyResultForItem(ItemStack stack1, ItemStack stack2) {
        try {
            return ForgeRecipes.getInstance().getAlloyResult(stack1, stack2);
        } catch (NullPointerException e) {
            return null;
        }
    }

    // Gets the number of slots in the inventory
    @Override
    public int getSizeInventory() {
        return itemStacks.length;
    }

    // Gets the stack in the given slot
    @Override
    public ItemStack getStackInSlot(int i) {
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
    public ItemStack decrStackSize(int slotIndex, int count) {
        ItemStack itemStackInSlot = getStackInSlot(slotIndex);
        if (itemStackInSlot == null) {
            return null;
        }

        ItemStack itemStackRemoved;
        if (itemStackInSlot.stackSize <= count) {
            itemStackRemoved = itemStackInSlot;
            setInventorySlotContents(slotIndex, null);
        } else {
            itemStackRemoved = itemStackInSlot.splitStack(count);
            if (itemStackInSlot.stackSize == 0) {
                setInventorySlotContents(slotIndex, null);
            }
        }
        markDirty();
        return itemStackRemoved;
    }

    // overwrites the stack in the given slotIndex with the given stack
    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
        itemStacks[slotIndex] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    // This is the maximum number if items allowed in each slot
    // This only affects things such as hoppers trying to insert items you need to use the container to enforce this for players
    // inserting items via the gui
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    // Return true if the given player is able to use this block. In this case it checks that
    // 1) the world tileentity hasn't been replaced in the meantime, and
    // 2) the player isn't too far away from the centre of the block
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (this.worldObj.getTileEntity(this.pos) != this) {
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
    public static boolean isItemValidForFuelSlot(ItemStack itemStack) {
        return TileEntityFurnace.isItemFuel(itemStack);
    }

    // Return true if the given stack is allowed to be inserted in the given slot
    // Unlike the vanilla furnace, we allow anything to be placed in the fuel slots
    public static boolean isItemValidForInputSlot(ItemStack itemStack) {
        return true;
    }

    // Return true if the given stack is allowed to be inserted in the given slot
    // Unlike the vanilla furnace, we allow anything to be placed in the fuel slots
    public static boolean isItemValidForOutputSlot(ItemStack itemStack) {
        return false;
    }

    //------------------------------
    // This is where you save any data that you don't want to lose when the tile entity unloads
    // In this case, it saves the state of the furnace (burn time etc) and the itemstacks stored in the fuel, input, and output slots
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound parentNBTTagCompound) {
        super.writeToNBT(parentNBTTagCompound); // The super call is required to save and load the tiles location

//		// Save the stored item stacks
        // to use an analogy with Java, this code generates an array of hashmaps
        // The itemStack in each slot is converted to an NBTTagCompound, which is effectively a hashmap of key->value pairs such
        //   as slot=1, id=2353, count=1, etc
        // Each of these NBTTagCompound are then inserted into NBTTagList, which is similar to an array.
        NBTTagList dataForAllSlots = new NBTTagList();
        for (int i = 0; i < this.itemStacks.length; ++i) {
            if (this.itemStacks[i] != null) {
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
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound); // The super call is required to save and load the tiles location
        final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType() for a listing
        NBTTagList dataForAllSlots = nbtTagCompound.getTagList("Items", NBT_TYPE_COMPOUND);

        Arrays.fill(itemStacks, null);           // set all slots to empty
        for (int i = 0; i < dataForAllSlots.tagCount(); ++i) {
            NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
            byte slotNumber = dataForOneSlot.getByte("Slot");
            if (slotNumber >= 0 && slotNumber < this.itemStacks.length) {
                this.itemStacks[slotNumber] = ItemStack.loadItemStackFromNBT(dataForOneSlot);
            }
        }

        // Load everything else.  Trim the arrays (or pad with 0) to make sure they have the correct number of elements
        cookTime = nbtTagCompound.getShort("CookTime");
        burnTimeRemaining = Arrays.copyOf(nbtTagCompound.getIntArray("burnTimeRemaining"), FUEL_SLOTS_COUNT);
        burnTimeInitialValue = Arrays.copyOf(nbtTagCompound.getIntArray("burnTimeInitial"), FUEL_SLOTS_COUNT);
        cachedNumberOfBurningSlots = -1;
    }

//	// When the world loads from disk, the server needs to send the TileEntity information to the client
//	//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound updateTagDescribingTileEntityState = getUpdateTag();
        final int METADATA = 0;
        return new SPacketUpdateTileEntity(this.pos, METADATA, updateTagDescribingTileEntityState);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound updateTagDescribingTileEntityState = pkt.getNbtCompound();
        handleUpdateTag(updateTagDescribingTileEntityState);
    }

    /* Creates a tag containing the TileEntity information, used by vanilla to transmit from server to client
     Warning - although our getUpdatePacket() uses this method, vanilla also calls it directly, so don't remove it.
     */
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    /* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
   Warning - although our onDataPacket() uses this method, vanilla also calls it directly, so don't remove it.
     */
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }
    //------------------------

    // set all slots to empty
    @Override
    public void clear() {
        Arrays.fill(itemStacks, null);
    }

    // will add a key for this container to the lang file so we can name it in the GUI
    @Override
    public String getName() {
        return "            Forge (Tier 1)";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    // standard code to look up what the human-readable name is
    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    public int getField(int id) {
        switch (id) {
            case 0:
                return this.burnTimeInitialValue[0];
            case 1:
                return this.burnTimeRemaining[0];
            case 2:
                return this.cookTime;
            case 3:
                return this.totalCookTime;
            default:
                return 0;
        }
    }

    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.burnTimeInitialValue[0] = value;
                break;
            case 1:
                this.burnTimeRemaining[0] = value;
                break;
            case 2:
                this.cookTime = (short) value;
                break;
            case 3:
                this.totalCookTime = (short) value;
        }
    }

    public int getFieldCount() {
        return 4;
    }

    public int getTemperatureOfCurrent() {
        if (itemStacks[FIRST_INPUT_SLOT] != null && itemStacks[FIRST_INPUT_SLOT + 1] != null) {
            try {
                return ForgeRecipes.getTemperature(ForgeRecipes.getAlloyResult(itemStacks[FIRST_INPUT_SLOT], itemStacks[FIRST_INPUT_SLOT + 1]).getItem());
            } catch (NullPointerException e) {
                return 500;
            }
        } else {
            return 500;
        }
    }

    // -----------------------------------------------------------------------------------------------------------
    // The following methods are not needed for this example but are part of IInventory so they must be implemented
    // Unused unless your container specifically uses it.
    // Return true if the given stack is allowed to go in the given slot
    @Override
    public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
        return false;
    }

    /**
     * This method removes the entire contents of the given slot and returns it.
     * Used by containers such as crafting tables which return any items in
     * their slots when you close the GUI
     *
     * @param slotIndex
     * @return
     */
    @Override
    public ItemStack removeStackFromSlot(int slotIndex) {
        ItemStack itemStack = getStackInSlot(slotIndex);
        if (itemStack != null) {
            setInventorySlotContents(slotIndex, null);
        }
        return itemStack;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerForgeTier1(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return "testmod:forge_tier1";
    }
}
