package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.CokeOvenRecipe;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class CokeOvenRecipeCategory implements IRecipeCategory<RecipeHolder<CokeOvenRecipe>> {

    public static final IRecipeHolderType<CokeOvenRecipe> TYPE = IRecipeHolderType.create(ModRecipes.COKE_OVEN_TYPE.get());

    private final IDrawable icon;

    public CokeOvenRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.COKE_OVEN.get().asItem().getDefaultInstance());
    }

    @Override
    public IRecipeHolderType<CokeOvenRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.industrialmetallurgy.coke_oven");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CokeOvenRecipe> recipeHolder, IFocusGroup focuses) {
        CokeOvenRecipe recipe = recipeHolder.value();
        builder.addInputSlot(0, 0).add(recipe.input());
        builder.addOutputSlot(44, 0).add(recipe.getResultItem());
    }

}
