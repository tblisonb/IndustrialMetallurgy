package com.onlytanner.test.items.tools;

import com.onlytanner.test.Reference;
import com.onlytanner.test.TestMod;
import com.onlytanner.test.init.ModItems;

import net.minecraft.item.ItemSword;

public class ItemNitinolSword extends ItemSword {

    public ItemNitinolSword() {
        super(ModItems.NITINOL);
        setUnlocalizedName(Reference.TestModItems.NITINOL_SWORD.getUnlocalizedName());
        setRegistryName(Reference.TestModItems.NITINOL_SWORD.getRegistryName());
        setCreativeTab(TestMod.TOOLS_TAB);
    }
}
