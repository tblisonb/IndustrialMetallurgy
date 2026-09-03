package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.AutoclaveRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class AutoclaveRecipeCategory implements IRecipeCategory<RecipeHolder<AutoclaveRecipe>> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/autoclave.png");
    private static final int[][] INPUT_SLOTS = {{16, 15}, {39, 8}, {16, 42}};

    public static final IRecipeHolderType<AutoclaveRecipe> TYPE = IRecipeHolderType.create(ModRecipes.AUTOCLAVE_TYPE.get());

    private final IDrawable icon;
    private final IDrawable background;

    public AutoclaveRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.AUTOCLAVE.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(TEXTURE, 23, 9, 111, 60);
    }

    @Override
    public IRecipeHolderType<AutoclaveRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.industrialmetallurgy.autoclave");
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
    public void draw(RecipeHolder<AutoclaveRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AutoclaveRecipe> recipeHolder, IFocusGroup focuses) {
        AutoclaveRecipe recipe = recipeHolder.value();
        List<Ingredient> inputs = recipe.input();
        for (int i = 0; i < inputs.size() && i < INPUT_SLOTS.length; i++) {
            builder.addInputSlot(INPUT_SLOTS[i][0], INPUT_SLOTS[i][1]).add(inputs.get(i));
        }

        builder.addOutputSlot(93, 26).add(recipe.getResultItem());
    }

}
