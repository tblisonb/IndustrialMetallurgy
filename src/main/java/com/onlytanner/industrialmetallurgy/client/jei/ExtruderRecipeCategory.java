package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.ExtruderRecipe;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ExtruderRecipeCategory implements IRecipeCategory<RecipeHolder<ExtruderRecipe>> {

    public static final IRecipeHolderType<ExtruderRecipe> TYPE = IRecipeHolderType.create(ModRecipes.EXTRUDER_TYPE.get());

    private final IDrawable icon;

    public ExtruderRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.EXTRUDER.get().asItem().getDefaultInstance());
    }

    @Override
    public IRecipeHolderType<ExtruderRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.industrialmetallurgy.extruder");
    }

    @Override
    public int getWidth() {
        return 62;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ExtruderRecipe> recipeHolder, IFocusGroup focuses) {
        ExtruderRecipe recipe = recipeHolder.value();
        // The recipe's SizedIngredient carries an exact input count (e.g. 3 brass ingots); JEI's
        // slot only shows the item choices, not that count -- close enough for a spot-check.
        builder.addInputSlot(0, 0).add(recipe.input().ingredient());
        builder.addOutputSlot(44, 0).add(recipe.getResultItem());
    }

}
