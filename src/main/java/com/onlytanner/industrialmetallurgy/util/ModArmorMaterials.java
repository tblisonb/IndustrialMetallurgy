package com.onlytanner.industrialmetallurgy.util;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

// Only 4 of the mod's ~28 metals get armor -- see ROADMAP.md for why each one specifically.
// Steel is a plain baseline (no ArmorSetBonusHandler entry). Titanium and Stellite sit at
// diamond-equivalent raw defense, differentiated by their passive set bonus (light/agile vs
// tough/fireproof) rather than by numbers. Tungsten-Rhenium is the unambiguous capstone,
// matching or beating netherite on every stat and carrying both bonuses at once.
public class ModArmorMaterials {

    public static final ResourceKey<EquipmentAsset> STEEL_ASSET = assetId("steel");
    public static final ResourceKey<EquipmentAsset> TITANIUM_ASSET = assetId("titanium");
    public static final ResourceKey<EquipmentAsset> STELLITE_ASSET = assetId("stellite");
    public static final ResourceKey<EquipmentAsset> TUNGSTEN_RHENIUM_ASSET = assetId("tungsten_rhenium");

    public static final ArmorMaterial STEEL = new ArmorMaterial(
            20, defense(2, 6, 7, 3, 12), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.5F, 0.0F, itemTag("repairs_steel_armor"), STEEL_ASSET);

    public static final ArmorMaterial TITANIUM = new ArmorMaterial(
            30, defense(3, 6, 8, 3, 11), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 1.5F, 0.0F, itemTag("repairs_titanium_armor"), TITANIUM_ASSET);

    public static final ArmorMaterial STELLITE = new ArmorMaterial(
            40, defense(3, 6, 8, 3, 11), 10, SoundEvents.ARMOR_EQUIP_IRON, 2.0F, 0.0F, itemTag("repairs_stellite_armor"), STELLITE_ASSET);

    public static final ArmorMaterial TUNGSTEN_RHENIUM = new ArmorMaterial(
            45, defense(3, 6, 8, 3, 19), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, itemTag("repairs_tungsten_rhenium_armor"), TUNGSTEN_RHENIUM_ASSET);

    private static Map<ArmorType, Integer> defense(int boots, int legs, int chest, int helm, int body) {
        return Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, legs, ArmorType.CHESTPLATE, chest, ArmorType.HELMET, helm, ArmorType.BODY, body);
    }

    private static TagKey<Item> itemTag(String name) {
        return ItemTags.create(Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, name));
    }

    private static ResourceKey<EquipmentAsset> assetId(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, name));
    }

}
