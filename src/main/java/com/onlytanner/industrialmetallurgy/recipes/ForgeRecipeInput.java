package com.onlytanner.industrialmetallurgy.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/** A snapshot of a forge's (up to) four input slots. */
public record ForgeRecipeInput(List<ItemStack> items) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

}
