package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.ChemicalCentrifugeBlock;
import com.onlytanner.industrialmetallurgy.containers.ChemicalCentrifugeContainer;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.recipes.ChemicalCentrifugeRecipe;
import com.onlytanner.industrialmetallurgy.recipes.ChemicalCentrifugeRecipeInput;
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
import java.util.List;
import java.util.Optional;

public class ChemicalCentrifugeBlockEntity extends BlockEntity implements MenuProvider {

    public static final int INPUT_ID = 0;
    public static final int BOTTLE_ID = 1;
    public static final int OUTPUT_ID = 2;
    public static final int NUM_OUTPUT_SLOTS = 3;
    public static final int MAX_SMELT_TIME = 50;
    public static final int MAX_ENERGY = 100000;
    public static final int ENERGY_USAGE_PER_TICK = 40;

    private static final RecipeManager.CachedCheck<ChemicalCentrifugeRecipeInput, ChemicalCentrifugeRecipe> QUICK_CHECK =
            RecipeManager.createCheck(ModRecipes.CHEMICAL_CENTRIFUGE_TYPE.get());

    private Component customName;
    public int currentSmeltTime;
    private final ModItemHandler inventory = new ModItemHandler(5);
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            ChemicalCentrifugeBlockEntity.this.setChanged();
        }
    };

    public ChemicalCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.CHEMICAL_CENTRIFUGE.get(), pos, state);
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<ChemicalCentrifugeRecipe>> recipe = getRecipe(serverLevel);
            if (recipe.isPresent() && canProcess(recipe.get()) && this.energyHandler.getAmountAsInt() >= ENERGY_USAGE_PER_TICK) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ChemicalCentrifugeBlock.LIT, true));
                if (this.currentSmeltTime != MAX_SMELT_TIME) {
                    this.currentSmeltTime++;
                } else {
                    this.currentSmeltTime = 0;
                    processRecipe(recipe.get());
                }
                this.energyHandler.set(this.energyHandler.getAmountAsInt() - ENERGY_USAGE_PER_TICK);
                dirty = true;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ChemicalCentrifugeBlock.LIT, false));
                currentSmeltTime = 0;
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    private void processRecipe(RecipeHolder<ChemicalCentrifugeRecipe> recipe) {
        ChemicalCentrifugeRecipe centrifugeRecipe = recipe.value();
        List<ItemStack> outputs = centrifugeRecipe.getResultItems();
        for (int i = 0; i < outputs.size() && i < NUM_OUTPUT_SLOTS; i++) {
            this.inventory.insertItem(OUTPUT_ID + i, outputs.get(i), false);
        }
        this.inventory.decrStackSize(INPUT_ID, 1);
        centrifugeRecipe.bottle().ifPresent(bottle -> this.inventory.decrStackSize(BOTTLE_ID, bottle.count()));
    }

    private boolean canProcess(RecipeHolder<ChemicalCentrifugeRecipe> recipe) {
        List<ItemStack> outputs = recipe.value().getResultItems();
        for (int i = 0; i < outputs.size() && i < NUM_OUTPUT_SLOTS; i++) {
            ItemStack existing = this.inventory.getStackInSlot(OUTPUT_ID + i);
            ItemStack recipeOutput = outputs.get(i);
            if (existing.getCount() >= 64 || (!existing.isEmpty() && !existing.getItem().equals(recipeOutput.getItem()))) {
                return false;
            }
        }
        return true;
    }

    private Optional<RecipeHolder<ChemicalCentrifugeRecipe>> getRecipe(ServerLevel serverLevel) {
        ItemStack inputStack = this.inventory.getStackInSlot(INPUT_ID);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }
        ItemStack bottleStack = this.inventory.getStackInSlot(BOTTLE_ID);
        return QUICK_CHECK.getRecipeFor(new ChemicalCentrifugeRecipeInput(inputStack, bottleStack), serverLevel);
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable("container." + IndustrialMetallurgy.MODID + ".chemical_centrifuge");
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
        return new ChemicalCentrifugeContainer(containerId, playerInventory, this);
    }

}
