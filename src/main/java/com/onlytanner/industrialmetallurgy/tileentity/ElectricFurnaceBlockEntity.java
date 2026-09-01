package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.ElectricFurnaceBlock;
import com.onlytanner.industrialmetallurgy.containers.ElectricFurnaceContainer;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.util.ModItemHandler;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

import javax.annotation.Nullable;
import java.util.Optional;

// Processes vanilla minecraft:smelting recipes directly (same JSON every ore already has) instead
// of a custom recipe type -- FE and a wearing heating_element slot stand in for a fuel slot.
public class ElectricFurnaceBlockEntity extends BlockEntity implements MenuProvider {

    public static final int INPUT_ID = 0;
    public static final int HEATING_ELEMENT_ID = 1;
    public static final int OUTPUT_ID = 2;
    public static final int MAX_ENERGY = 100000;
    public static final int ENERGY_USAGE_PER_TICK = 40;
    public static final int MAX_HEATING_ELEMENT_USES = 200;

    private static final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> QUICK_CHECK =
            RecipeManager.createCheck(RecipeType.SMELTING);

    private Component customName;
    public int currentSmeltTime;
    private int heatingElementUses = MAX_HEATING_ELEMENT_USES;
    private final ModItemHandler inventory = new ModItemHandler(3);
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            ElectricFurnaceBlockEntity.this.setChanged();
        }
    };

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.ELECTRIC_FURNACE.get(), pos, state);
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<SmeltingRecipe>> recipe = getRecipe(serverLevel);
            if (recipe.isPresent() && canProcess(recipe.get()) && hasHeatingElement() && this.energyHandler.getAmountAsInt() >= ENERGY_USAGE_PER_TICK) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ElectricFurnaceBlock.LIT, true));
                int maxSmeltTime = recipe.get().value().cookingTime();
                if (this.currentSmeltTime < maxSmeltTime) {
                    this.currentSmeltTime++;
                } else {
                    this.currentSmeltTime = 0;
                    processRecipe(recipe.get());
                }
                this.energyHandler.set(this.energyHandler.getAmountAsInt() - ENERGY_USAGE_PER_TICK);
                dirty = true;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ElectricFurnaceBlock.LIT, false));
                this.currentSmeltTime = 0;
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    private void processRecipe(RecipeHolder<SmeltingRecipe> recipe) {
        ItemStack inputStack = this.inventory.getStackInSlot(INPUT_ID);
        ItemStack output = recipe.value().assemble(new SingleRecipeInput(inputStack));
        this.inventory.insertItem(OUTPUT_ID, output, false);
        this.inventory.decrStackSize(INPUT_ID, 1);
        consumeHeatingElementUse();
    }

    private void consumeHeatingElementUse() {
        if (this.heatingElementUses > 0) {
            this.heatingElementUses--;
        }
        if (this.heatingElementUses <= 0) {
            this.inventory.decrStackSize(HEATING_ELEMENT_ID, 1);
            this.heatingElementUses = MAX_HEATING_ELEMENT_USES;
        }
    }

    private boolean canProcess(RecipeHolder<SmeltingRecipe> recipe) {
        ItemStack output = this.inventory.getStackInSlot(OUTPUT_ID);
        ItemStack recipeOutput = recipe.value().assemble(new SingleRecipeInput(this.inventory.getStackInSlot(INPUT_ID)));
        return output.getCount() < 64 && (output.isEmpty() || output.getItem().equals(recipeOutput.getItem()));
    }

    private Optional<RecipeHolder<SmeltingRecipe>> getRecipe(ServerLevel serverLevel) {
        ItemStack inputStack = this.inventory.getStackInSlot(INPUT_ID);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }
        return QUICK_CHECK.getRecipeFor(new SingleRecipeInput(inputStack), serverLevel);
    }

    public boolean hasHeatingElement() {
        return this.inventory.getStackInSlot(HEATING_ELEMENT_ID).getCount() > 0;
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable("container." + IndustrialMetallurgy.MODID + ".electric_furnace");
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.customName = input.read("CustomName", ComponentSerialization.CODEC).orElse(null);
        this.inventory.deserialize(input.childOrEmpty("Inventory"));
        this.currentSmeltTime = input.getIntOr("CurrentSmeltTime", 0);
        this.heatingElementUses = input.getIntOr("HeatingElementUses", MAX_HEATING_ELEMENT_USES);
        this.energyHandler.deserialize(input.childOrEmpty("Energy"));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.customName != null) {
            output.store("CustomName", ComponentSerialization.CODEC, this.customName);
        }
        this.inventory.serialize(output.child("Inventory"));
        output.putInt("CurrentSmeltTime", this.currentSmeltTime);
        output.putInt("HeatingElementUses", this.heatingElementUses);
        this.energyHandler.serialize(output.child("Energy"));
    }

    public final IItemHandlerModifiable getInventory() {
        return this.inventory;
    }

    public final EnergyHandler getEnergyHandler() {
        return this.energyHandler;
    }

    public int getEnergyAmount() {
        return this.energyHandler.getAmountAsInt();
    }

    public void setEnergyAmount(int amount) {
        this.energyHandler.set(Math.max(0, amount));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricFurnaceContainer(containerId, playerInventory, this);
    }

}
