package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.AutoclaveBlock;
import com.onlytanner.industrialmetallurgy.containers.AutoclaveContainer;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.recipes.AutoclaveRecipe;
import com.onlytanner.industrialmetallurgy.recipes.AutoclaveRecipeInput;
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

/**
 * A late-game pressure vessel: leaches a crushed ore with a lixiviant bottle (an acid, base, or
 * cyanide solution -- whichever matches that ore's real-world chemistry) into a pregnant leach
 * solution bottle. That solution is inert on its own; it's the Chemical Reactor's job to
 * precipitate the dissolved metal back out as a concentrate, which then smelts at a better yield
 * than going straight from crushed ore. See GUIDE.md for the full writeup.
 */
public class AutoclaveBlockEntity extends BlockEntity implements MenuProvider {

    public static final int INPUT_ID = 0;
    public static final int NUM_INPUT_SLOTS = 3;
    public static final int OUTPUT_ID = 3;
    public static final int MAX_PROCESS_TIME = 100;
    public static final int MAX_ENERGY = 250000;
    public static final int ENERGY_USAGE_PER_TICK = 100;

    private static final RecipeManager.CachedCheck<AutoclaveRecipeInput, AutoclaveRecipe> QUICK_CHECK =
            RecipeManager.createCheck(ModRecipes.AUTOCLAVE_TYPE.get());

    private Component customName;
    public int currentProcessTime;
    private final ModItemHandler inventory = new ModItemHandler(4);
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            AutoclaveBlockEntity.this.setChanged();
        }
    };

    public AutoclaveBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.AUTOCLAVE.get(), pos, state);
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<AutoclaveRecipe>> recipe = getRecipe(serverLevel);
            if (recipe.isPresent() && canProcess(recipe.get()) && this.energyHandler.getAmountAsInt() >= ENERGY_USAGE_PER_TICK) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(AutoclaveBlock.LIT, true));
                if (this.currentProcessTime != MAX_PROCESS_TIME) {
                    this.currentProcessTime++;
                } else {
                    this.currentProcessTime = 0;
                    processRecipe(recipe.get());
                }
                this.energyHandler.set(this.energyHandler.getAmountAsInt() - ENERGY_USAGE_PER_TICK);
                dirty = true;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(AutoclaveBlock.LIT, false));
                this.currentProcessTime = 0;
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    private void processRecipe(RecipeHolder<AutoclaveRecipe> recipe) {
        this.inventory.insertItem(OUTPUT_ID, recipe.value().getResultItem(), false);
        for (int i = 0; i < NUM_INPUT_SLOTS; i++) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                this.inventory.decrStackSize(i, 1);
            }
        }
    }

    private boolean canProcess(RecipeHolder<AutoclaveRecipe> recipe) {
        ItemStack output = this.inventory.getStackInSlot(OUTPUT_ID);
        ItemStack recipeOutput = recipe.value().getResultItem();
        return output.getCount() < 64 && (output.isEmpty() || output.getItem().equals(recipeOutput.getItem()));
    }

    private Optional<RecipeHolder<AutoclaveRecipe>> getRecipe(ServerLevel serverLevel) {
        List<ItemStack> slots = List.of(
                this.inventory.getStackInSlot(0), this.inventory.getStackInSlot(1), this.inventory.getStackInSlot(2));
        if (slots.stream().allMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return QUICK_CHECK.getRecipeFor(new AutoclaveRecipeInput(slots), serverLevel);
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable("container." + IndustrialMetallurgy.MODID + ".autoclave");
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
        this.currentProcessTime = input.getIntOr("CurrentProcessTime", 0);
        this.energyHandler.deserialize(input.childOrEmpty("Energy"));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.customName != null) {
            output.store("CustomName", ComponentSerialization.CODEC, this.customName);
        }
        this.inventory.serialize(output.child("Inventory"));
        output.putInt("CurrentProcessTime", this.currentProcessTime);
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
        return new AutoclaveContainer(containerId, playerInventory, this);
    }

}
