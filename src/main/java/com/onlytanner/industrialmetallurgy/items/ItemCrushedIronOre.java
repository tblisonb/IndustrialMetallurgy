package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.Reference;
import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;

import net.minecraft.item.Item;

public class ItemCrushedIronOre extends Item {

    public ItemCrushedIronOre() {
        setUnlocalizedName(Reference.IndustrialMetallurgyItems.CRUSHED_IRON_ORE.getUnlocalizedName());
        setRegistryName(Reference.IndustrialMetallurgyItems.CRUSHED_IRON_ORE.getRegistryName());
        setCreativeTab(IndustrialMetallurgy.RESOURCES_TAB);
    }
}
