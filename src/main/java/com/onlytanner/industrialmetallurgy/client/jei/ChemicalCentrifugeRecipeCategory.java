package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.recipes.ChemicalCentrifugeRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.Optional;

public class ChemicalCentrifugeRecipeCategory implements IRecipeCategory<RecipeHolder<ChemicalCentrifugeRecipe>> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/chemical_centrifuge.png");
    private static final int[][] OUTPUT_SLOTS = {{8, 51}, {31, 58}, {54, 51}};

    public static final IRecipeHolderType<ChemicalCentrifugeRecipe> TYPE = IRecipeHolderType.create(ModRecipes.CHEMICAL_CENTRIFUGE_TYPE.get());

    private final IDrawable icon;
    private final IDrawable background;

    public ChemicalCentrifugeRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(RegistryHandler.CHEMICAL_CENTRIFUGE.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(TEXTURE, 49, 0, 127, 82);
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
    public void draw(RecipeHolder<ChemicalCentrifugeRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        this.background.draw(graphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChemicalCentrifugeRecipe> recipeHolder, IFocusGroup focuses) {
        ChemicalCentrifugeRecipe recipe = recipeHolder.value();
        builder.addInputSlot(31, 17).add(recipe.input());
        Optional<SizedIngredient> bottle = recipe.bottle();
        if (bottle.isPresent()) {
            builder.addInputSlot(103, 8).add(bottle.get().ingredient());
        }

        List<ItemStack> outputs = recipe.getResultItems();
        for (int i = 0; i < outputs.size() && i < OUTPUT_SLOTS.length; i++) {
            builder.addOutputSlot(OUTPUT_SLOTS[i][0], OUTPUT_SLOTS[i][1]).add(outputs.get(i));
        }
    }

}
