package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.Reference;
import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;

import net.minecraft.item.Item;

public class ItemInvarNugget extends Item {

    public ItemInvarNugget() {
        setUnlocalizedName(Reference.IndustrialMetallurgyItems.INVAR_NUGGET.getUnlocalizedName());
        setRegistryName(Reference.IndustrialMetallurgyItems.INVAR_NUGGET.getRegistryName());
        setCreativeTab(IndustrialMetallurgy.RESOURCES_TAB);
    }
}
