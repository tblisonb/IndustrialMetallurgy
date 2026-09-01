package com.onlytanner.industrialmetallurgy.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/** A snapshot of the chemical centrifuge's main input slot and its dedicated bottle slot. */
public record ChemicalCentrifugeRecipeInput(ItemStack mainItem, ItemStack bottleItem) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.mainItem;
            case 1 -> this.bottleItem;
            default -> throw new IllegalArgumentException("No item for index " + index);
        };
    }

    @Override
    public int size() {
        return 2;
    }

}
