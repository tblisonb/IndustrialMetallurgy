package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.Reference;
import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;

import net.minecraft.item.Item;

public class ItemCobaltIngot extends Item {

    public ItemCobaltIngot() {
        setUnlocalizedName(Reference.IndustrialMetallurgyItems.COBALT_INGOT.getUnlocalizedName());
        setRegistryName(Reference.IndustrialMetallurgyItems.COBALT_INGOT.getRegistryName());
        setCreativeTab(IndustrialMetallurgy.RESOURCES_TAB);
    }
}