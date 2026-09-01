package com.onlytanner.industrialmetallurgy.util;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

// Real-world justification for each tier lives in ROADMAP.md; the short version: Steel is the
// existing 1.16.4 baseline (iron harvest level, 3x iron durability, diamond speed/damage/enchant).
// Cobalt Steel and Stellite are real cutting-tool alloys prized for edge retention and wear
// resistance respectively, so they're lateral/lightly-ahead of Steel rather than a harvest-level
// jump. Tungsten Steel and Tungsten-Rhenium (real: used for rocket nozzles and high-temperature
// thermocouples) cross into the diamond/netherite mining tier. Tungsten-Rhenium keeps the old
// Nequitum capstone's durability of 0 -- an item with no max damage can't be damaged at all.
public class ModToolMaterials {

    public static final ToolMaterial STEEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 750, 8.0F, 3.0F, 10, itemTag("steel_tool_materials"));

    public static final ToolMaterial COBALT_STEEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 900, 9.5F, 3.0F, 10, itemTag("cobalt_steel_tool_materials"));

    public static final ToolMaterial STELLITE = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1800, 8.5F, 3.5F, 12, itemTag("stellite_tool_materials"));

    public static final ToolMaterial TUNGSTEN_STEEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 2000, 9.0F, 4.0F, 13, itemTag("tungsten_steel_tool_materials"));

    public static final ToolMaterial TUNGSTEN_RHENIUM = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 0, 10.0F, 7.0F, 25, itemTag("tungsten_rhenium_tool_materials"));

    private static TagKey<Item> itemTag(String name) {
        return ItemTags.create(Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, name));
    }

}
