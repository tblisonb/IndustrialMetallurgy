package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.ChemicalReactorRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

// Recipe's first output always goes to the main slot, and an optional second one goes to the
// reactor's dedicated bottle slot -- matches ChemicalReactorRecipe's own doc comment.
public class ChemicalReactorRecipeCategory implements IRecipeCategory<RecipeHolder<ChemicalReactorRecipe>> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/chemical_reactor.png");
    private static final int[][] INPUT_SLOTS = {{8, 15}, {31, 8}, {54, 15}};

    public static final IRecipeHolderType<ChemicalReactorRecipe> TYPE = IRecipeHolderType.create(ModRecipes.CHEMICAL_REACTOR_TYPE.get());

    private final IDrawable icon;
    private final IDrawable background;

    public ChemicalReactorRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.CHEMICAL_REACTOR.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(TEXTURE, 49, 9, 127, 77);
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
    public void draw(RecipeHolder<ChemicalReactorRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChemicalReactorRecipe> recipeHolder, IFocusGroup focuses) {
        ChemicalReactorRecipe recipe = recipeHolder.value();
        List<Ingredient> inputs = recipe.input();
        for (int i = 0; i < inputs.size() && i < INPUT_SLOTS.length; i++) {
            builder.addInputSlot(INPUT_SLOTS[i][0], INPUT_SLOTS[i][1]).add(inputs.get(i));
        }

        List<ItemStack> outputs = recipe.getResultItems();
        if (!outputs.isEmpty()) {
            builder.addOutputSlot(31, 49).add(outputs.get(0));
        }
        if (outputs.size() > 1) {
            builder.addOutputSlot(103, 53).add(outputs.get(1));
        }
    }

}
