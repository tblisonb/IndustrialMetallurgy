package com.onlytanner.industrialmetallurgy.recipes;

import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

/**
 * A single-slot extruder recipe whose input carries a count (e.g. 3 brass ingots -> 1 gear),
 * which plain {@link net.minecraft.world.item.crafting.Ingredient}/SingleItemRecipe can't express.
 */
public record ExtruderRecipe(SizedIngredient input, ItemStackTemplate output) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return this.input.test(recipeInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput) {
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
    public RecipeSerializer<ExtruderRecipe> getSerializer() {
        return ModRecipes.EXTRUDER_SERIALIZER.get();
    }

    @Override
    public RecipeType<ExtruderRecipe> getType() {
        return ModRecipes.EXTRUDER_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.input.ingredient());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

}
