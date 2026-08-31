package com.onlytanner.industrialmetallurgy.recipes;

import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;

public class CokeOvenRecipe extends SingleItemRecipe {

    public CokeOvenRecipe(Recipe.CommonInfo commonInfo, Ingredient input, ItemStackTemplate result) {
        super(commonInfo, input, result);
    }

    @Override
    public RecipeSerializer<CokeOvenRecipe> getSerializer() {
        return ModRecipes.COKE_OVEN_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends SingleItemRecipe> getType() {
        return ModRecipes.COKE_OVEN_TYPE.get();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    /** Exposes the recipe's result as a fresh ItemStack; SingleItemRecipe#result() is protected. */
    public ItemStack getResultItem() {
        return this.result().create();
    }

}
