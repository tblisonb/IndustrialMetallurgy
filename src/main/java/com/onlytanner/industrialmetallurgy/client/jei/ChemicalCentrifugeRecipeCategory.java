package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.ChemicalCentrifugeRecipe;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.Optional;

public class ChemicalCentrifugeRecipeCategory implements IRecipeCategory<RecipeHolder<ChemicalCentrifugeRecipe>> {

    public static final IRecipeHolderType<ChemicalCentrifugeRecipe> TYPE = IRecipeHolderType.create(ModRecipes.CHEMICAL_CENTRIFUGE_TYPE.get());

    private final IDrawable icon;

    public ChemicalCentrifugeRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.CHEMICAL_CENTRIFUGE.get().asItem().getDefaultInstance());
    }

    @Override
    public IRecipeHolderType<ChemicalCentrifugeRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.industrialmetallurgy.chemical_centrifuge");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChemicalCentrifugeRecipe> recipeHolder, IFocusGroup focuses) {
        ChemicalCentrifugeRecipe recipe = recipeHolder.value();
        builder.addInputSlot(0, 9).add(recipe.input());
        Optional<SizedIngredient> bottle = recipe.bottle();
        if (bottle.isPresent()) {
            builder.addInputSlot(0, 27).add(bottle.get().ingredient());
        }

        List<ItemStack> outputs = recipe.getResultItems();
        for (int i = 0; i < outputs.size(); i++) {
            builder.addOutputSlot(44, i * 18).add(outputs.get(i));
        }
    }

}
