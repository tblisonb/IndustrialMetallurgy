package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.ChemicalReactorRecipe;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class ChemicalReactorRecipeCategory implements IRecipeCategory<RecipeHolder<ChemicalReactorRecipe>> {

    public static final IRecipeHolderType<ChemicalReactorRecipe> TYPE = IRecipeHolderType.create(ModRecipes.CHEMICAL_REACTOR_TYPE.get());

    private final IDrawable icon;

    public ChemicalReactorRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.CHEMICAL_REACTOR.get().asItem().getDefaultInstance());
    }

    @Override
    public IRecipeHolderType<ChemicalReactorRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.industrialmetallurgy.chemical_reactor");
    }

    @Override
    public int getWidth() {
        return 80;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChemicalReactorRecipe> recipeHolder, IFocusGroup focuses) {
        ChemicalReactorRecipe recipe = recipeHolder.value();
        List<Ingredient> inputs = recipe.input();
        for (int i = 0; i < inputs.size(); i++) {
            builder.addInputSlot(0, i * 18).add(inputs.get(i));
        }

        List<ItemStack> outputs = recipe.getResultItems();
        for (int i = 0; i < outputs.size(); i++) {
            builder.addOutputSlot(44, i * 18).add(outputs.get(i));
        }
    }

}
