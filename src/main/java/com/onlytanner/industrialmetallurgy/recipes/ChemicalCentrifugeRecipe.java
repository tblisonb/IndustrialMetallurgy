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
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.Optional;

/**
 * A single-input, multi-output recipe (the reverse of {@link CrusherRecipe}): the main item
 * breaks down into up to 3 byproducts. A couple of recipes (e.g. bitumen distillation) also
 * require a counted item in the dedicated bottle slot; recipes that don't declare a {@link #bottle()}
 * ignore whatever sits in that slot rather than requiring it to be empty.
 */
public record ChemicalCentrifugeRecipe(Ingredient input, Optional<SizedIngredient> bottle, List<ItemStackTemplate> output) implements Recipe<ChemicalCentrifugeRecipeInput> {

    @Override
    public boolean matches(ChemicalCentrifugeRecipeInput recipeInput, Level level) {
        if (!this.input.test(recipeInput.mainItem())) {
            return false;
        }
        return this.bottle.isEmpty() || this.bottle.get().test(recipeInput.bottleItem());
    }

    @Override
    public ItemStack assemble(ChemicalCentrifugeRecipeInput recipeInput) {
        return this.getResultItems().get(0);
    }

    /** Exposes every recipe output as fresh ItemStacks, for use outside of #assemble. */
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
    public RecipeSerializer<ChemicalCentrifugeRecipe> getSerializer() {
        return ModRecipes.CHEMICAL_CENTRIFUGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<ChemicalCentrifugeRecipe> getType() {
        return ModRecipes.CHEMICAL_CENTRIFUGE_TYPE.get();
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
