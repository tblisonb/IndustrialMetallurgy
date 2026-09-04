package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.SolderingStationBlock;
import com.onlytanner.industrialmetallurgy.containers.SolderingStationContainer;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.recipes.SolderingStationRecipe;
import com.onlytanner.industrialmetallurgy.recipes.SolderingStationRecipeInput;
import com.onlytanner.industrialmetallurgy.util.ModItemHandler;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SolderingStationBlockEntity extends BlockEntity implements MenuProvider {

    public static final int NUM_MAIN_SLOTS = 9;
    public static final int SOLDER_ID = 9;
    public static final int OUTPUT_ID = 10;
    public static final int MAX_SMELT_TIME = 50;
    public static final int MAX_ENERGY = 100000;
    public static final int ENERGY_USAGE_PER_TICK = 40;

    private static final RecipeManager.CachedCheck<SolderingStationRecipeInput, SolderingStationRecipe> QUICK_CHECK =
            RecipeManager.createCheck(ModRecipes.SOLDERING_STATION_TYPE.get());

    private Component customName;
    public int currentSmeltTime;
    private final ModItemHandler inventory = new ModItemHandler(11, Set.of(OUTPUT_ID));
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            SolderingStationBlockEntity.this.setChanged();
        }
    };

    public SolderingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.SOLDERING_STATION.get(), pos, state);
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<SolderingStationRecipe>> recipe = getRecipe(serverLevel);
            if (recipe.isPresent() && canProcess(recipe.get()) && this.energyHandler.getAmountAsInt() >= ENERGY_USAGE_PER_TICK) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(SolderingStationBlock.LIT, true));
                if (this.currentSmeltTime != MAX_SMELT_TIME) {
                    this.currentSmeltTime++;
                } else {
                    this.currentSmeltTime = 0;
                    processRecipe(recipe.get());
                }
                this.energyHandler.set(this.energyHandler.getAmountAsInt() - ENERGY_USAGE_PER_TICK);
                dirty = true;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(SolderingStationBlock.LIT, false));
                currentSmeltTime = 0;
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    private void processRecipe(RecipeHolder<SolderingStationRecipe> recipe) {
        SolderingStationRecipe solderingRecipe = recipe.value();
        this.inventory.insertItem(OUTPUT_ID, solderingRecipe.getResultItem(), false);
        int usedSlots = solderingRecipe.input().size();
        for (int i = 0; i < usedSlots; i++) {
            this.inventory.decrStackSize(i, 1);
        }
    }

    private boolean canProcess(RecipeHolder<SolderingStationRecipe> recipe) {
        ItemStack output = this.inventory.getStackInSlot(OUTPUT_ID);
        ItemStack recipeOutput = recipe.value().getResultItem();
        return output.getCount() < 64 && (output.isEmpty() || output.getItem().equals(recipeOutput.getItem()));
    }

    private Optional<RecipeHolder<SolderingStationRecipe>> getRecipe(ServerLevel serverLevel) {
        List<ItemStack> slots = new ArrayList<>(SOLDER_ID + 1);
        boolean anyFilled = false;
        for (int i = 0; i <= SOLDER_ID; i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            slots.add(stack);
            anyFilled |= !stack.isEmpty();
        }
        if (!anyFilled) {
            return Optional.empty();
        }
        return QUICK_CHECK.getRecipeFor(new SolderingStationRecipeInput(slots), serverLevel);
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable("container." + IndustrialMetallurgy.MODID + ".soldering_station");
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
        this.energyHandler.serialize(output.child("Energy"));
    }

    public final ModItemHandler getInventory() {
        return this.inventory;
    }

    public final EnergyHandler getEnergyHandler() {
        return this.energyHandler;
    }

    /** For the menu's synced energy DataSlot; see FunctionalIntReferenceHolder. */
    public int getEnergyAmount() {
        return this.energyHandler.getAmountAsInt();
    }

    public void setEnergyAmount(int amount) {
        this.energyHandler.set(Math.max(0, amount));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SolderingStationContainer(containerId, playerInventory, this);
    }

}
