package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.blocks.ForgeBlock;
import com.onlytanner.industrialmetallurgy.containers.AdvancedForgeContainer;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.multiblock.MultiblockPattern;
import com.onlytanner.industrialmetallurgy.recipes.ForgeRecipe;
import com.onlytanner.industrialmetallurgy.recipes.ForgeRecipeInput;
import com.onlytanner.industrialmetallurgy.util.ModItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Electric counterpart to BasicForgeBlockEntity (tiers 3-4): no fuel slot, draws from a
 * SimpleEnergyHandler instead -- same energy-capability pattern as the crusher.
 */
public abstract class AdvancedForgeBlockEntity extends BlockEntity implements MenuProvider, ForgeBlockEntity {

    public static final int NUM_INPUT_SLOTS = 4;
    public static final int OUTPUT_ID = 4;
    public static final int MAX_ENERGY = 100000;
    public static final int ENERGY_USAGE_PER_TICK = 40;

    private static final RecipeManager.CachedCheck<ForgeRecipeInput, ForgeRecipe> QUICK_CHECK =
            RecipeManager.createCheck(ModRecipes.FORGE_TYPE.get());

    protected final ModItemHandler inventory = new ModItemHandler(5);
    protected Component customName;
    public int currentSmeltTime;
    public int currentTemperature;
    public final int maxSmeltTime;
    public final int maxTemperature;
    public final int degreesPerTick;
    public final int minRunningTemperature;
    private final Set<String> acceptedTiers;
    private final Component defaultName;
    private final MenuType<AdvancedForgeContainer> menuType;
    /**
     * True whenever this tier has no multiblock structure to check ({@link #getMultiblockPattern()}
     * returns null, the default) -- Tiers 3/4 are plain single-block machines and are always
     * considered formed. Only a tier that overrides {@link #getMultiblockPattern()} (currently just
     * the Arc Furnace) tracks real formed/unformed state here.
     */
    private boolean formed = true;
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(MAX_ENERGY) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            AdvancedForgeBlockEntity.this.setChanged();
        }
    };

    protected AdvancedForgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                        int maxSmeltTime, int maxTemperature, int degreesPerTick, int minRunningTemperature,
                                        Set<String> acceptedTiers, Component defaultName, MenuType<AdvancedForgeContainer> menuType) {
        super(type, pos, state);
        this.maxSmeltTime = maxSmeltTime;
        this.maxTemperature = maxTemperature;
        this.degreesPerTick = degreesPerTick;
        this.minRunningTemperature = minRunningTemperature;
        this.acceptedTiers = acceptedTiers;
        this.defaultName = defaultName;
        this.menuType = menuType;
    }

    public MenuType<AdvancedForgeContainer> getMenuType() {
        return this.menuType;
    }

    /**
     * The multiblock structure this tier requires around itself, or null for a plain single-block
     * tier (the default -- overridden only by {@link ArcFurnaceBlockEntity} today). Authored
     * against {@link ForgeBlock#FACING} = NORTH; see {@link com.onlytanner.industrialmetallurgy.multiblock.MultiblockPatternBuilder}.
     */
    @Nullable
    protected MultiblockPattern getMultiblockPattern() {
        return null;
    }

    /**
     * Re-checks {@link #getMultiblockPattern()} against the world every tick -- cheap enough (a
     * handful of block-state lookups) not to need throttling, and simpler/more correct than
     * relying on {@code neighborChanged} propagation, which only reaches blocks directly adjacent
     * to the controller and would miss most of a structure this size being broken further away.
     * A no-op whenever there's no pattern to check.
     */
    private void refreshStructure() {
        MultiblockPattern pattern = getMultiblockPattern();
        if (pattern == null) {
            return;
        }
        boolean matches = pattern.matches(this.level, this.worldPosition, this.getBlockState().getValue(ForgeBlock.FACING));
        if (matches != this.formed) {
            this.formed = matches;
            this.setChanged();
        }
    }

    /** True if this tier has no structure requirement, or its structure is currently intact. */
    public boolean isFormed() {
        return this.formed;
    }

    /** For the menu's synced formed-state DataSlot; see FunctionalIntReferenceHolder. */
    public void setFormed(boolean formed) {
        this.formed = formed;
    }

    /**
     * Sneak + right-click diagnostic (see {@link ForgeBlock#useWithoutItem}): rather than leaving
     * a player to guess why a multiblock won't form, point at the first cell that's wrong,
     * described relative to the player the same way the Prospector reports a find. A no-op for a
     * tier with no pattern at all.
     */
    public void reportStructureMismatch(Player player) {
        MultiblockPattern pattern = getMultiblockPattern();
        if (pattern == null || this.level == null || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        Optional<MultiblockPattern.Mismatch> mismatch = pattern.findFirstMismatch(
                this.level, this.worldPosition, this.getBlockState().getValue(ForgeBlock.FACING));
        if (mismatch.isEmpty()) {
            serverPlayer.sendOverlayMessage(Component.translatable("message.industrialmetallurgy.structure_complete")
                    .withStyle(ChatFormatting.GREEN));
            return;
        }
        BlockPos pos = mismatch.get().pos();
        BlockPos origin = player.blockPosition();
        Component expected = mismatch.get().expectedName() != null
                ? mismatch.get().expectedName()
                : Component.translatable("message.industrialmetallurgy.structure_mismatch_generic_block");
        String location = locationDescription(pos.getX() - origin.getX(), pos.getY() - origin.getY(), pos.getZ() - origin.getZ());
        serverPlayer.sendOverlayMessage(Component.translatable("message.industrialmetallurgy.structure_mismatch", expected, location)
                .withStyle(ChatFormatting.YELLOW));
    }

    private static String locationDescription(int dx, int dy, int dz) {
        String vertical = dy > 0 ? "above you" : dy < 0 ? "below you" : "at your level";
        if (dx == 0 && dz == 0) {
            return dy > 0 ? "directly above you" : dy < 0 ? "directly below you" : "right where you're standing";
        }
        return bearing(dx, dz) + ", " + vertical;
    }

    private static String bearing(int dx, int dz) {
        double degrees = Math.toDegrees(Math.atan2(dx, -dz));
        degrees = (degrees + 360.0) % 360.0;
        String[] names = {"north", "north-east", "east", "south-east", "south", "south-west", "west", "north-west"};
        int index = (int) Math.round(degrees / 45.0) % 8;
        return names[index];
    }

    @Override
    public void tick() {
        boolean dirty = false;
        if (this.level instanceof ServerLevel serverLevel) {
            refreshStructure();
            Optional<RecipeHolder<ForgeRecipe>> recipe = this.formed ? getRecipe(serverLevel) : Optional.empty();
            int energy = this.energyHandler.getAmountAsInt();
            if (recipe.isPresent() && canProcess(recipe.get()) && energy >= ENERGY_USAGE_PER_TICK && currentTemperature > minRunningTemperature) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ForgeBlock.LIT, true));
                heatUp();
                this.energyHandler.set(energy - ENERGY_USAGE_PER_TICK);
                if (this.currentSmeltTime != this.maxSmeltTime) {
                    this.currentSmeltTime++;
                } else {
                    this.currentSmeltTime = 0;
                    processRecipe(recipe.get());
                }
                dirty = true;
            } else if (recipe.isPresent() && canProcess(recipe.get()) && energy >= ENERGY_USAGE_PER_TICK) {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ForgeBlock.LIT, true));
                heatUp();
                this.energyHandler.set(energy - ENERGY_USAGE_PER_TICK);
                dirty = true;
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(ForgeBlock.LIT, false));
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

    @Override
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
        this.energyHandler.deserialize(input.childOrEmpty("Energy"));
        this.formed = input.getBooleanOr("Formed", true);
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
        this.energyHandler.serialize(output.child("Energy"));
        output.putBoolean("Formed", this.formed);
    }

    public final ModItemHandler getInventory() {
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
        return new AdvancedForgeContainer(containerId, playerInventory, this);
    }

}
