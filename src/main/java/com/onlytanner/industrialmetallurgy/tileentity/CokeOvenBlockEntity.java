package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.CokeOvenBlock;
import com.onlytanner.industrialmetallurgy.containers.CokeOvenContainer;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.recipes.CokeOvenRecipe;
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
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Optional;

public class CokeOvenBlockEntity extends BlockEntity implements MenuProvider {

    public static final int INPUT_ID = 0;
    public static final int OUTPUT_ID = 1;
    public static final int MAX_BURN_TIME = 800;

    private static final RecipeManager.CachedCheck<SingleRecipeInput, CokeOvenRecipe> QUICK_CHECK =
            RecipeManager.createCheck(ModRecipes.COKE_OVEN_TYPE.get());

    private Component customName;
    public int burnTimeRemaining;
    public int currentSmeltTime;
    private final ModItemHandler inventory = new ModItemHandler(2);

    public CokeOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.COKE_OVEN.get(), pos, state);
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<CokeOvenRecipe>> recipe = getRecipe(serverLevel);
            if (recipe.isPresent() && canProcess(recipe.get())) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CokeOvenBlock.LIT, true));
                if (this.currentSmeltTime != MAX_BURN_TIME) {
                    this.currentSmeltTime++;
                    this.burnTimeRemaining = MAX_BURN_TIME - currentSmeltTime;
                    dirty = true;
                } else {
                    this.currentSmeltTime = 0;
                    this.burnTimeRemaining = 0;
                    processRecipe(recipe.get());
                    dirty = true;
                }
            } else {
                if (burnTimeRemaining > 0) {
                    this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CokeOvenBlock.LIT, true));
                    this.burnTimeRemaining = 0;
                } else {
                    this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CokeOvenBlock.LIT, false));
                }
                currentSmeltTime = 0;
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    private void processRecipe(RecipeHolder<CokeOvenRecipe> recipe) {
        CokeOvenRecipe cokeOvenRecipe = recipe.value();
        this.inventory.insertItem(OUTPUT_ID, cokeOvenRecipe.getResultItem(), false);

        ItemStack inputStack = this.inventory.getStackInSlot(INPUT_ID);
        if (!inputStack.isEmpty() && cokeOvenRecipe.input().test(inputStack)) {
            this.inventory.decrStackSize(INPUT_ID, 1);
        }
    }

    private boolean canProcess(RecipeHolder<CokeOvenRecipe> recipe) {
        ItemStack output = this.inventory.getStackInSlot(OUTPUT_ID);
        ItemStack recipeOutput = recipe.value().getResultItem();
        return output.getCount() < 64 && (output.isEmpty() || output.getItem().equals(recipeOutput.getItem()));
    }

    private Optional<RecipeHolder<CokeOvenRecipe>> getRecipe(ServerLevel serverLevel) {
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
        return Component.translatable("container." + IndustrialMetallurgy.MODID + ".coke_oven");
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
        this.burnTimeRemaining = input.getIntOr("BurnTimeRemaining", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.customName != null) {
            output.store("CustomName", ComponentSerialization.CODEC, this.customName);
        }
        this.inventory.serialize(output.child("Inventory"));
        output.putInt("BurnTimeRemaining", this.burnTimeRemaining);
    }

    public final IItemHandlerModifiable getInventory() {
        return this.inventory;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CokeOvenContainer(containerId, playerInventory, this);
    }

}
