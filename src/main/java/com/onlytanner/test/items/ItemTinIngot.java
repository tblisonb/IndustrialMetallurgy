package com.onlytanner.test.items;

import com.onlytanner.test.Reference;
import com.onlytanner.test.TestMod;

import net.minecraft.item.Item;

public class ItemTinIngot extends Item 
{
	public ItemTinIngot()
	{
		setUnlocalizedName(Reference.TestModItems.TIN_INGOT.getUnlocalizedName());
		setRegistryName(Reference.TestModItems.TIN_INGOT.getRegistryName());
		setCreativeTab(TestMod.RESOURCES_TAB);
	}
}
