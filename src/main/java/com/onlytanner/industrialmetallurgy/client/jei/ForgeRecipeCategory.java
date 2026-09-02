package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.ForgeRecipe;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

// Every Forge tier (Iron/Steel/Cobalt/Tungsten/Arc Furnace) shares this one recipe type, gated by
// ForgeRecipe#tier() rather than being 5 separate JEI categories; the tier is drawn onto each
// recipe so it's still clear which machine actually accepts it.
public class ForgeRecipeCategory implements IRecipeCategory<RecipeHolder<ForgeRecipe>> {

    private static final int[][] INPUT_SLOTS = {{0, 0}, {18, 0}, {0, 18}, {18, 18}};

    public static final IRecipeHolderType<ForgeRecipe> TYPE = IRecipeHolderType.create(ModRecipes.FORGE_TYPE.get());

    private final IDrawable icon;

    public ForgeRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.ARC_FURNACE.get().asItem().getDefaultInstance());
    }

    @Override
    public IRecipeHolderType<ForgeRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Forge");
    }

    @Override
    public int getWidth() {
        return 84;
    }

    @Override
    public int getHeight() {
        return 36;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ForgeRecipe> recipeHolder, IFocusGroup focuses) {
        List<Ingredient> input = recipeHolder.value().input();
        for (int i = 0; i < input.size() && i < INPUT_SLOTS.length; i++) {
            builder.addInputSlot(INPUT_SLOTS[i][0], INPUT_SLOTS[i][1]).add(input.get(i));
        }
        builder.addOutputSlot(62, 9).add(recipeHolder.value().getResultItem());
    }

    @Override
    public void draw(RecipeHolder<ForgeRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        graphics.text(Minecraft.getInstance().font, Component.translatable("block.industrialmetallurgy." + tierBlockKey(recipeHolder.value().tier())), 0, 26, 0x808080);
    }

    private static String tierBlockKey(String tier) {
        return switch (tier) {
            case "iron" -> "forge_tier1";
            case "steel" -> "forge_tier2";
            case "cobalt" -> "forge_tier3";
            case "tungsten" -> "forge_tier4";
            default -> "arc_furnace";
        };
    }

}
