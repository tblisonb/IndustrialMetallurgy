package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.ExtruderRecipe;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ExtruderRecipeCategory implements IRecipeCategory<RecipeHolder<ExtruderRecipe>> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/extruder.png");

    public static final IRecipeHolderType<ExtruderRecipe> TYPE = IRecipeHolderType.create(ModRecipes.EXTRUDER_TYPE.get());

    private final IDrawable icon;
    private final IDrawable background;

    public ExtruderRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.EXTRUDER.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(TEXTURE, 48, 27, 92, 32);
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
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(RecipeHolder<ExtruderRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ExtruderRecipe> recipeHolder, IFocusGroup focuses) {
        ExtruderRecipe recipe = recipeHolder.value();
        // The recipe's SizedIngredient carries an exact input count (e.g. 3 brass ingots); JEI's
        // slot only shows the item choices, not that count -- close enough for a spot-check.
        builder.addInputSlot(8, 8).add(recipe.input().ingredient());
        builder.addOutputSlot(68, 8).add(recipe.getResultItem());
    }

}
