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
 * A multi-ingredient reactor recipe: every ingredient must be present across the reactor's 3
 * input slots (in any slot, order doesn't matter), with no other items filled -- same matching
 * scheme as {@link ForgeRecipe}. Produces 1 or 2 outputs: the first always goes to the main
 * output slot, and a second (when present) to the reactor's dedicated bottle slot.
 */
public record ChemicalReactorRecipe(List<Ingredient> input, List<ItemStackTemplate> output) implements Recipe<ChemicalReactorRecipeInput> {

    @Override
    public boolean matches(ChemicalReactorRecipeInput recipeInput, Level level) {
        List<ItemStack> filled = recipeInput.items().stream().filter(stack -> !stack.isEmpty()).toList();
        if (filled.size() != this.input.size()) {
            return false;
        }
        return RecipeMatcher.findMatches(filled, this.input) != null;
    }

    @Override
    public ItemStack assemble(ChemicalReactorRecipeInput recipeInput) {
        return this.getResultItems().get(0);
    }

    /** Exposes every recipe output (1 or 2 items) as fresh ItemStacks, for use outside of #assemble. */
    public List<ItemStack> getResultItems() {
        return this.output.stream().map(ItemStackTemplate::create).toList();
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
    public RecipeSerializer<ChemicalReactorRecipe> getSerializer() {
        return ModRecipes.CHEMICAL_REACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<ChemicalReactorRecipe> getType() {
        return ModRecipes.CHEMICAL_REACTOR_TYPE.get();
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
