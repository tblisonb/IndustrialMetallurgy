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
 * A multi-ingredient leaching recipe: every ingredient (a crushed ore plus its lixiviant bottle --
 * an acid, base, or cyanide solution, depending on the ore's real chemistry) must be present
 * across the autoclave's 3 input slots, with no other items filled -- same matching scheme as
 * {@link ForgeRecipe}. Produces a single pregnant leach solution bottle, which the Chemical
 * Reactor then precipitates into a metal concentrate.
 */
public record AutoclaveRecipe(List<Ingredient> input, ItemStackTemplate output) implements Recipe<AutoclaveRecipeInput> {

    @Override
    public boolean matches(AutoclaveRecipeInput recipeInput, Level level) {
        List<ItemStack> filled = recipeInput.items().stream().filter(stack -> !stack.isEmpty()).toList();
        if (filled.size() != this.input.size()) {
            return false;
        }
        return RecipeMatcher.findMatches(filled, this.input) != null;
    }

    @Override
    public ItemStack assemble(AutoclaveRecipeInput recipeInput) {
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
    public RecipeSerializer<AutoclaveRecipe> getSerializer() {
        return ModRecipes.AUTOCLAVE_SERIALIZER.get();
    }

    @Override
    public RecipeType<AutoclaveRecipe> getType() {
        return ModRecipes.AUTOCLAVE_TYPE.get();
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
