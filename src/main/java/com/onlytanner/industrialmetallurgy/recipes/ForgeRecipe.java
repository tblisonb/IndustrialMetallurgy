package com.onlytanner.industrialmetallurgy.recipes;

import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import java.util.List;

/**
 * A multi-ingredient forge recipe: every ingredient must be present across the forge's input
 * slots (in any slot, order doesn't matter), with no other items filled. Gated by {@link #tier()}
 * -- ForgeTier1BlockEntity/ForgeTier2BlockEntity/etc. only run recipes whose tier they accept;
 * that check lives on the block entity side, not here, since it's about the machine's capability
 * rather than whether the ingredients match.
 */
public record ForgeRecipe(List<Ingredient> input, ItemStackTemplate output, String tier) implements Recipe<ForgeRecipeInput> {

    @Override
    public boolean matches(ForgeRecipeInput recipeInput, Level level) {
        List<ItemStack> filled = recipeInput.items().stream().filter(stack -> !stack.isEmpty()).toList();
        if (filled.size() != this.input.size()) {
            return false;
        }
        return RecipeMatcher.findMatches(filled, this.input) != null;
    }

    @Override
    public ItemStack assemble(ForgeRecipeInput recipeInput) {
        return this.output.create();
    }

    /** Exposes the recipe's result as a fresh ItemStack for use outside of #assemble. */
    public ItemStack getResultItem() {
        return this.output.create();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<ForgeRecipe> getSerializer() {
        return ModRecipes.FORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<ForgeRecipe> getType() {
        return ModRecipes.FORGE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.input);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

}
