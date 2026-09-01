package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.CrusherBlock;
import com.onlytanner.industrialmetallurgy.containers.CrusherContainer;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.recipes.CrusherRecipe;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class CrusherBlockEntity extends BlockEntity implements MenuProvider {

    public static final int INPUT_ID = 0;
    public static final int BURR_SET_ID = 1;
    public static final int OUTPUT_ID = 2;
    public static final int ACID_ID = 3;
    public static final int MAX_SMELT_TIME = 50;
    public static final int MAX_ENERGY = 100000;
    public static final int ENERGY_USAGE_PER_TICK = 40;
    public static final int MAX_ACID_LEVEL = 64;

    private static final RecipeManager.CachedCheck<SingleRecipeInput, CrusherRecipe> QUICK_CHECK =
            RecipeManager.createCheck(ModRecipes.CRUSHER_TYPE.get());

    private Component customName;
    public int currentSmeltTime;
    public int acidLevel;
    private final ModItemHandler inventory = new ModItemHandler(4);
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            CrusherBlockEntity.this.setChanged();
        }
    };

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.CRUSHER.get(), pos, state);
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.acidLevel == 0 && this.inventory.getStackInSlot(ACID_ID).getCount() > 0) {
                this.inventory.decrStackSize(ACID_ID, 1);
                this.acidLevel = MAX_ACID_LEVEL;
            }
            Optional<RecipeHolder<CrusherRecipe>> recipe = getRecipe(serverLevel);
            if (recipe.isPresent() && canProcess(recipe.get()) && this.inventory.getStackInSlot(BURR_SET_ID).getCount() > 0 && this.energyHandler.getAmountAsInt() >= ENERGY_USAGE_PER_TICK) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CrusherBlock.LIT, true));
                if (this.currentSmeltTime != MAX_SMELT_TIME) {
                    this.currentSmeltTime++;
                } else {
                    this.currentSmeltTime = 0;
                    processRecipe(recipe.get());
                }
                this.energyHandler.set(this.energyHandler.getAmountAsInt() - ENERGY_USAGE_PER_TICK);
                dirty = true;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CrusherBlock.LIT, false));
                currentSmeltTime = 0;
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    private void processRecipe(RecipeHolder<CrusherRecipe> recipe) {
        CrusherRecipe crusherRecipe = recipe.value();
        ItemStack output = crusherRecipe.getResultItem();
        output.setCount(getOutputForTier() * output.getCount());
        this.inventory.insertItem(OUTPUT_ID, output, false);

        ItemStack inputStack = this.inventory.getStackInSlot(INPUT_ID);
        if (!inputStack.isEmpty() && crusherRecipe.input().test(inputStack)) {
            this.inventory.decrStackSize(INPUT_ID, 1);
            acidLevel = (acidLevel >= 8) ? acidLevel - 8 : 0;

            ItemStack burrSet = this.inventory.getStackInSlot(BURR_SET_ID);
            if (burrSet.isDamageableItem()) {
                if (burrSet.getDamageValue() + 1 >= burrSet.getMaxDamage()) {
                    burrSet.shrink(1);
                } else {
                    burrSet.setDamageValue(burrSet.getDamageValue() + 1);
                }
            }
        }
    }

    private int getOutputForTier() {
        ItemStack burrSet = this.inventory.getStackInSlot(BURR_SET_ID);
        if (burrSet.getItem().equals(RegistryHandler.BRASS_BURR_SET.get())) {
            if (this.acidLevel > 0)
                return 1 + ((Math.random() >= 0.66) ? 1 : 0);
            return 1 + ((Math.random() >= 0.75) ? 1 : 0);
        } else if (burrSet.getItem().equals(RegistryHandler.STEEL_BURR_SET.get())) {
            if (this.acidLevel > 0)
                return 1 + ((Math.random() >= 0.25) ? 1 : 0);
            return 1 + ((Math.random() >= 0.5) ? 1 : 0);
        } else if (burrSet.getItem().equals(RegistryHandler.CHROMIUM_BURR_SET.get())) {
            if (this.acidLevel > 0)
                return 2 + ((Math.random() >= 0.5) ? 1 : 0);
            return 2;
        } else if (burrSet.getItem().equals(RegistryHandler.TUNGSTEN_CARBIDE_BURR_SET.get())) {
            if (this.acidLevel > 0)
                return 3;
            return 2 + ((Math.random() >= 0.5) ? 1 : 0);
        } else if (burrSet.getItem().equals(RegistryHandler.TUNGSTEN_RHENIUM_BURR_SET.get())) {
            if (this.acidLevel > 0)
                return 3;
            return 2 + ((Math.random() >= 0.5) ? 1 : 0);
        }
        return 0;
    }

    private boolean canProcess(RecipeHolder<CrusherRecipe> recipe) {
        ItemStack output = this.inventory.getStackInSlot(OUTPUT_ID);
        ItemStack recipeOutput = recipe.value().getResultItem();
        return output.getCount() < 64 && (output.isEmpty() || output.getItem().equals(recipeOutput.getItem()));
    }

    private Optional<RecipeHolder<CrusherRecipe>> getRecipe(ServerLevel serverLevel) {
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
        return Component.translatable("container." + IndustrialMetallurgy.MODID + ".crusher");
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
        this.acidLevel = input.getIntOr("AcidLevel", 0);
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
        output.putInt("AcidLevel", this.acidLevel);
    }

    public final IItemHandlerModifiable getInventory() {
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
        return new CrusherContainer(containerId, playerInventory, this);
    }

}
