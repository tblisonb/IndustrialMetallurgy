package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.ExtruderBlock;
import com.onlytanner.industrialmetallurgy.containers.ExtruderContainer;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.recipes.ExtruderRecipe;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class ExtruderBlockEntity extends BlockEntity implements MenuProvider {

    public static final int INPUT_ID = 0;
    public static final int OUTPUT_ID = 1;
    public static final int MAX_SMELT_TIME = 50;
    public static final int MAX_ENERGY = 100000;
    public static final int ENERGY_USAGE_PER_TICK = 40;

    private static final RecipeManager.CachedCheck<SingleRecipeInput, ExtruderRecipe> QUICK_CHECK =
            RecipeManager.createCheck(ModRecipes.EXTRUDER_TYPE.get());

    private Component customName;
    public int currentSmeltTime;
    private final ModItemHandler inventory = new ModItemHandler(2, Set.of(OUTPUT_ID));
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            ExtruderBlockEntity.this.setChanged();
        }
    };

    public ExtruderBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.EXTRUDER.get(), pos, state);
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<ExtruderRecipe>> recipe = getRecipe(serverLevel);
            if (recipe.isPresent() && canProcess(recipe.get()) && this.energyHandler.getAmountAsInt() >= ENERGY_USAGE_PER_TICK) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ExtruderBlock.LIT, true));
                if (this.currentSmeltTime != MAX_SMELT_TIME) {
                    this.currentSmeltTime++;
                } else {
                    this.currentSmeltTime = 0;
                    processRecipe(recipe.get());
                }
                this.energyHandler.set(this.energyHandler.getAmountAsInt() - ENERGY_USAGE_PER_TICK);
                dirty = true;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ExtruderBlock.LIT, false));
                currentSmeltTime = 0;
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    private void processRecipe(RecipeHolder<ExtruderRecipe> recipe) {
        ExtruderRecipe extruderRecipe = recipe.value();
        this.inventory.insertItem(OUTPUT_ID, extruderRecipe.getResultItem(), false);
        this.inventory.decrStackSize(INPUT_ID, extruderRecipe.input().count());
    }

    private boolean canProcess(RecipeHolder<ExtruderRecipe> recipe) {
        ItemStack output = this.inventory.getStackInSlot(OUTPUT_ID);
        ItemStack recipeOutput = recipe.value().getResultItem();
        return output.getCount() < 64 && (output.isEmpty() || output.getItem().equals(recipeOutput.getItem()));
    }

    private Optional<RecipeHolder<ExtruderRecipe>> getRecipe(ServerLevel serverLevel) {
        ItemStack inputStack = this.inventory.getStackInSlot(INPUT_ID);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }
        return QUICK_CHECK.getRecipeFor(new SingleRecipeInput(inputStack), serverLevel);
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable("container." + IndustrialMetallurgy.MODID + ".extruder");
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
        return new ExtruderContainer(containerId, playerInventory, this);
    }

}
