package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.SolderingStationRecipe;
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

// Slot 0-8 is the 3x3 main grid, slot 9 (if present) is the dedicated solder slot -- matches
// SolderingStationRecipeInput's positional layout (see SolderingStationRecipe's own doc comment).
// Background/positions are cropped straight from the real GUI texture.
public class SolderingStationRecipeCategory implements IRecipeCategory<RecipeHolder<SolderingStationRecipe>> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/soldering_station.png");

    public static final IRecipeHolderType<SolderingStationRecipe> TYPE = IRecipeHolderType.create(ModRecipes.SOLDERING_STATION_TYPE.get());

    private final IDrawable icon;
    private final IDrawable background;

    public SolderingStationRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.SOLDERING_STATION.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(TEXTURE, 34, 9, 126, 68);
    }

    @Override
    public IRecipeHolderType<SolderingStationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.industrialmetallurgy.soldering_station");
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
    public void draw(RecipeHolder<SolderingStationRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SolderingStationRecipe> recipeHolder, IFocusGroup focuses) {
        List<Ingredient> input = recipeHolder.value().input();
        for (int i = 0; i < input.size() && i < 9; i++) {
            int x = 8 + (i % 3) * 18;
            int y = 8 + (i / 3) * 18;
            builder.addInputSlot(x, y).add(input.get(i));
        }
        if (input.size() > 9) {
            builder.addInputSlot(98, 8).add(input.get(9));
        }
        builder.addOutputSlot(102, 40).add(recipeHolder.value().getResultItem());
    }

}
