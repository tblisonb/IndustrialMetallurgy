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

import java.util.List;

/**
 * A positional (not order-independent) recipe: {@code input.get(i)} must match the item sitting
 * in slot {@code i} of the soldering station's 9 main slots + solder slot (index 9). Recipes may
 * list fewer than 10 ingredients -- any slot past the list's length is simply not checked or
 * consumed, matching the original 1.16.4 behavior.
 */
public record SolderingStationRecipe(List<Ingredient> input, ItemStackTemplate output) implements Recipe<SolderingStationRecipeInput> {

    @Override
    public boolean matches(SolderingStationRecipeInput recipeInput, Level level) {
        for (int i = 0; i < this.input.size(); i++) {
            if (!this.input.get(i).test(recipeInput.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(SolderingStationRecipeInput recipeInput) {
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
    public RecipeSerializer<SolderingStationRecipe> getSerializer() {
        return ModRecipes.SOLDERING_STATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<SolderingStationRecipe> getType() {
        return ModRecipes.SOLDERING_STATION_TYPE.get();
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
