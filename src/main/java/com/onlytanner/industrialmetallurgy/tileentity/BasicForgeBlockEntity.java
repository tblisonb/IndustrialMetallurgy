package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.blocks.BasicForgeBlock;
import com.onlytanner.industrialmetallurgy.containers.BasicForgeContainer;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.ForgeRecipe;
import com.onlytanner.industrialmetallurgy.recipes.ForgeRecipeInput;
import com.onlytanner.industrialmetallurgy.util.ModItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Shared logic for the solid-fuel forges (tiers 1-2; tiers 3-4 run on electricity instead, see
 * a future AdvancedForgeBlockEntity). A forge has 4 input slots, 1 fuel slot, 1 output slot, and
 * ramps a "temperature" up while lit/burning; recipes only run once past a per-tier threshold.
 */
public abstract class BasicForgeBlockEntity extends BlockEntity implements MenuProvider {

    public static final int NUM_INPUT_SLOTS = 4;
    public static final int FUEL_ID = 4;
    public static final int OUTPUT_ID = 5;
    public static final int MAX_BURN_TIME = 1600;

    private static final RecipeManager.CachedCheck<ForgeRecipeInput, ForgeRecipe> QUICK_CHECK =
            RecipeManager.createCheck(ModRecipes.FORGE_TYPE.get());

    protected final ModItemHandler inventory = new ModItemHandler(6);
    protected Component customName;
    public int currentSmeltTime;
    public int burnTimeRemaining;
    public int currentTemperature;
    public final int maxSmeltTime;
    public final int maxTemperature;
    public final int degreesPerTick;
    public final int minRunningTemperature;
    private final Set<String> acceptedTiers;
    private final Component defaultName;
    private final MenuType<BasicForgeContainer> menuType;

    protected BasicForgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                     int maxSmeltTime, int maxTemperature, int degreesPerTick, int minRunningTemperature,
                                     Set<String> acceptedTiers, Component defaultName, MenuType<BasicForgeContainer> menuType) {
        super(type, pos, state);
        this.maxSmeltTime = maxSmeltTime;
        this.maxTemperature = maxTemperature;
        this.degreesPerTick = degreesPerTick;
        this.minRunningTemperature = minRunningTemperature;
        this.acceptedTiers = acceptedTiers;
        this.defaultName = defaultName;
        this.menuType = menuType;
    }

    public MenuType<BasicForgeContainer> getMenuType() {
        return this.menuType;
    }

    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<ForgeRecipe>> recipe = getRecipe(serverLevel);
            if (recipe.isPresent() && canProcess(recipe.get()) && burnTimeRemaining > 0 && currentTemperature > minRunningTemperature) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BasicForgeBlock.LIT, true));
                heatUp();
                this.burnTimeRemaining--;
                if (this.currentSmeltTime != this.maxSmeltTime) {
                    this.currentSmeltTime++;
                } else {
                    this.currentSmeltTime = 0;
                    processRecipe(recipe.get());
                }
                dirty = true;
            } else if (this.burnTimeRemaining == 0 && hasFuel() && recipe.isPresent() && canProcess(recipe.get())) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BasicForgeBlock.LIT, true));
                heatUp();
                consumeFuel();
                dirty = true;
            } else if (burnTimeRemaining > 0) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BasicForgeBlock.LIT, true));
                heatUp();
                this.burnTimeRemaining--;
                currentSmeltTime = 0;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BasicForgeBlock.LIT, false));
                this.currentTemperature = Math.max(0, this.currentTemperature - 1);
                currentSmeltTime = 0;
            }
        }
        if (dirty) {
            this.setChanged();
        }
    }

    private void heatUp() {
        this.currentTemperature = Math.min(this.currentTemperature + degreesPerTick, maxTemperature);
    }

    private void processRecipe(RecipeHolder<ForgeRecipe> recipe) {
        ForgeRecipe forgeRecipe = recipe.value();
        this.inventory.insertItem(OUTPUT_ID, forgeRecipe.getResultItem(), false);
        for (int i = 0; i < NUM_INPUT_SLOTS; i++) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                this.inventory.decrStackSize(i, 1);
            }
        }
    }

    private boolean canProcess(RecipeHolder<ForgeRecipe> recipe) {
        ItemStack output = this.inventory.getStackInSlot(OUTPUT_ID);
        ItemStack recipeOutput = recipe.value().getResultItem();
        return output.getCount() < 64 && (output.isEmpty() || output.getItem().equals(recipeOutput.getItem()));
    }

    private Optional<RecipeHolder<ForgeRecipe>> getRecipe(ServerLevel serverLevel) {
        List<ItemStack> slots = List.of(
                this.inventory.getStackInSlot(0), this.inventory.getStackInSlot(1),
                this.inventory.getStackInSlot(2), this.inventory.getStackInSlot(3));
        if (slots.stream().allMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return QUICK_CHECK.getRecipeFor(new ForgeRecipeInput(slots), serverLevel)
                .filter(holder -> this.acceptedTiers.contains(holder.value().tier()));
    }

    public boolean hasFuel() {
        return this.inventory.getStackInSlot(FUEL_ID).getCount() > 0;
    }

    public void consumeFuel() {
        this.inventory.decrStackSize(FUEL_ID, 1);
        this.burnTimeRemaining = MAX_BURN_TIME;
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public Component getDefaultName() {
        return this.defaultName;
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
        this.currentTemperature = input.getIntOr("CurrentTemperature", 0);
        this.burnTimeRemaining = input.getIntOr("BurnTimeRemaining", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.customName != null) {
            output.store("CustomName", ComponentSerialization.CODEC, this.customName);
        }
        this.inventory.serialize(output.child("Inventory"));
        output.putInt("CurrentSmeltTime", this.currentSmeltTime);
        output.putInt("CurrentTemperature", this.currentTemperature);
        output.putInt("BurnTimeRemaining", this.burnTimeRemaining);
    }

    public final IItemHandlerModifiable getInventory() {
        return this.inventory;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BasicForgeContainer(containerId, playerInventory, this);
    }

}
