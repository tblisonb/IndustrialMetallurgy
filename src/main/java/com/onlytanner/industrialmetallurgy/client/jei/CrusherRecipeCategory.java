package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.CrusherRecipe;
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

// Background is a real crop of the crusher's own GUI texture (input/output slot area only, not
// the full player-inventory-sized panel) rather than a generic slot grid, so this actually looks
// like the machine.
public class CrusherRecipeCategory implements IRecipeCategory<RecipeHolder<CrusherRecipe>> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/crusher.png");

    public static final IRecipeHolderType<CrusherRecipe> TYPE = IRecipeHolderType.create(ModRecipes.CRUSHER_TYPE.get());

    private final IDrawable icon;
    private final IDrawable background;

    public CrusherRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.CRUSHER.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(TEXTURE, 48, 27, 92, 32);
    }

    @Override
    public IRecipeHolderType<CrusherRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.industrialmetallurgy.crusher");
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
    public void draw(RecipeHolder<CrusherRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CrusherRecipe> recipeHolder, IFocusGroup focuses) {
        CrusherRecipe recipe = recipeHolder.value();
        builder.addInputSlot(8, 8).add(recipe.input());
        builder.addOutputSlot(68, 8).add(recipe.getResultItem());
    }

}
