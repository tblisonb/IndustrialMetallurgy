package com.onlytanner.industrialmetallurgy.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/** A snapshot of the soldering station's 9 main slots plus its dedicated solder slot (index 9). */
public record SolderingStationRecipeInput(List<ItemStack> items) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

}
