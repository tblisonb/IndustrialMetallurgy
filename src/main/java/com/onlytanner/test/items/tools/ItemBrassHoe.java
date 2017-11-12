package com.onlytanner.test.items.tools;

import com.onlytanner.test.Reference;
import com.onlytanner.test.TestMod;
import com.onlytanner.test.init.ModItems;

import net.minecraft.item.ItemHoe;

public class ItemBrassHoe extends ItemHoe
{
	public ItemBrassHoe() 
	{
		super(ModItems.BRASS);
		setUnlocalizedName(Reference.TestModItems.BRASS_HOE.getUnlocalizedName());
		setRegistryName(Reference.TestModItems.BRASS_HOE.getRegistryName());
		setCreativeTab(TestMod.TOOLS_TAB);
	}
}
