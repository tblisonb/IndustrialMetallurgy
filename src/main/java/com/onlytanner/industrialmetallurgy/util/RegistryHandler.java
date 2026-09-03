package com.onlytanner.industrialmetallurgy.util;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.*;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier1BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier2BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier3BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier4BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ArcFurnaceBlockEntity;
import com.onlytanner.industrialmetallurgy.items.BatteryPackItem;
import com.onlytanner.industrialmetallurgy.items.ChainsawItem;
import com.onlytanner.industrialmetallurgy.items.CultivatorItem;
import com.onlytanner.industrialmetallurgy.items.GuideBookItem;
import com.onlytanner.industrialmetallurgy.items.PowerDrillItem;
import com.onlytanner.industrialmetallurgy.items.ProspectorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class RegistryHandler {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IndustrialMetallurgy.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(IndustrialMetallurgy.MODID);

    // Items
    // Metal Ingots
    public static final DeferredItem<Item> ALNICO_INGOT = ITEMS.registerSimpleItem("alnico_ingot");
    public static final DeferredItem<Item> ALUMINUM_INGOT = ITEMS.registerSimpleItem("aluminum_ingot");
    public static final DeferredItem<Item> BRASS_INGOT = ITEMS.registerSimpleItem("brass_ingot");
    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.registerSimpleItem("bronze_ingot");
    public static final DeferredItem<Item> CHROMIUM_INGOT = ITEMS.registerSimpleItem("chromium_ingot");
    public static final DeferredItem<Item> COBALT_INGOT = ITEMS.registerSimpleItem("cobalt_ingot");
    public static final DeferredItem<Item> COBALT_STEEL_INGOT = ITEMS.registerSimpleItem("cobalt_steel_ingot");
    public static final DeferredItem<Item> CONSTANTAN_INGOT = ITEMS.registerSimpleItem("constantan_ingot");
    public static final DeferredItem<Item> COPPER_TUNGSTEN_INGOT = ITEMS.registerSimpleItem("copper_tungsten_ingot");
    public static final DeferredItem<Item> ELECTRUM_INGOT = ITEMS.registerSimpleItem("electrum_ingot");
    public static final DeferredItem<Item> INVAR_INGOT = ITEMS.registerSimpleItem("invar_ingot");
    public static final DeferredItem<Item> KANTHAL_INGOT = ITEMS.registerSimpleItem("kanthal_ingot");
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot");
    public static final DeferredItem<Item> MANGANESE_INGOT = ITEMS.registerSimpleItem("manganese_ingot");
    // Rhenium is only smeltable in the Arc Furnace (real rhenium has the 2nd-highest melting
    // point of any element) -- see forge/rhenium_ingot.json.
    public static final DeferredItem<Item> RHENIUM_INGOT = ITEMS.registerSimpleItem("rhenium_ingot");
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_INGOT = ITEMS.registerSimpleItem("tungsten_rhenium_ingot");
    public static final DeferredItem<Item> NICKEL_INGOT = ITEMS.registerSimpleItem("nickel_ingot");
    public static final DeferredItem<Item> NICHROME_INGOT = ITEMS.registerSimpleItem("nichrome_ingot");
    public static final DeferredItem<Item> NIKROTHAL_INGOT = ITEMS.registerSimpleItem("nikrothal_ingot");
    public static final DeferredItem<Item> NITINOL_INGOT = ITEMS.registerSimpleItem("nitinol_ingot");
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerSimpleItem("silver_ingot");
    public static final DeferredItem<Item> SOLDER_INGOT = ITEMS.registerSimpleItem("solder_ingot");
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.registerSimpleItem("steel_ingot");
    public static final DeferredItem<Item> STELLITE_INGOT = ITEMS.registerSimpleItem("stellite_ingot");
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.registerSimpleItem("tin_ingot");
    public static final DeferredItem<Item> TITANIUM_INGOT = ITEMS.registerSimpleItem("titanium_ingot");
    public static final DeferredItem<Item> TUNGSTEN_INGOT = ITEMS.registerSimpleItem("tungsten_ingot");
    public static final DeferredItem<Item> TUNGSTEN_STEEL_INGOT = ITEMS.registerSimpleItem("tungsten_steel_ingot");
    public static final DeferredItem<Item> ZINC_INGOT = ITEMS.registerSimpleItem("zinc_ingot");
    // Metal Nuggets
    public static final DeferredItem<Item> ALNICO_NUGGET = ITEMS.registerSimpleItem("alnico_nugget");
    public static final DeferredItem<Item> ALUMINUM_NUGGET = ITEMS.registerSimpleItem("aluminum_nugget");
    public static final DeferredItem<Item> BRASS_NUGGET = ITEMS.registerSimpleItem("brass_nugget");
    public static final DeferredItem<Item> BRONZE_NUGGET = ITEMS.registerSimpleItem("bronze_nugget");
    public static final DeferredItem<Item> CHROMIUM_NUGGET = ITEMS.registerSimpleItem("chromium_nugget");
    public static final DeferredItem<Item> COBALT_NUGGET = ITEMS.registerSimpleItem("cobalt_nugget");
    public static final DeferredItem<Item> COBALT_STEEL_NUGGET = ITEMS.registerSimpleItem("cobalt_steel_nugget");
    public static final DeferredItem<Item> CONSTANTAN_NUGGET = ITEMS.registerSimpleItem("constantan_nugget");
    public static final DeferredItem<Item> COPPER_TUNGSTEN_NUGGET = ITEMS.registerSimpleItem("copper_tungsten_nugget");
    public static final DeferredItem<Item> ELECTRUM_NUGGET = ITEMS.registerSimpleItem("electrum_nugget");
    public static final DeferredItem<Item> INVAR_NUGGET = ITEMS.registerSimpleItem("invar_nugget");
    public static final DeferredItem<Item> KANTHAL_NUGGET = ITEMS.registerSimpleItem("kanthal_nugget");
    public static final DeferredItem<Item> LEAD_NUGGET = ITEMS.registerSimpleItem("lead_nugget");
    public static final DeferredItem<Item> MANGANESE_NUGGET = ITEMS.registerSimpleItem("manganese_nugget");
    public static final DeferredItem<Item> RHENIUM_NUGGET = ITEMS.registerSimpleItem("rhenium_nugget");
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_NUGGET = ITEMS.registerSimpleItem("tungsten_rhenium_nugget");
    public static final DeferredItem<Item> NICHROME_NUGGET = ITEMS.registerSimpleItem("nichrome_nugget");
    public static final DeferredItem<Item> NICKEL_NUGGET = ITEMS.registerSimpleItem("nickel_nugget");
    public static final DeferredItem<Item> NIKROTHAL_NUGGET = ITEMS.registerSimpleItem("nikrothal_nugget");
    public static final DeferredItem<Item> NITINOL_NUGGET = ITEMS.registerSimpleItem("nitinol_nugget");
    public static final DeferredItem<Item> SILVER_NUGGET = ITEMS.registerSimpleItem("silver_nugget");
    public static final DeferredItem<Item> SOLDER_NUGGET = ITEMS.registerSimpleItem("solder_nugget");
    public static final DeferredItem<Item> STEEL_NUGGET = ITEMS.registerSimpleItem("steel_nugget");
    public static final DeferredItem<Item> STELLITE_NUGGET = ITEMS.registerSimpleItem("stellite_nugget");
    public static final DeferredItem<Item> TIN_NUGGET = ITEMS.registerSimpleItem("tin_nugget");
    public static final DeferredItem<Item> TITANIUM_NUGGET = ITEMS.registerSimpleItem("titanium_nugget");
    public static final DeferredItem<Item> TUNGSTEN_NUGGET = ITEMS.registerSimpleItem("tungsten_nugget");
    public static final DeferredItem<Item> TUNGSTEN_STEEL_NUGGET = ITEMS.registerSimpleItem("tungsten_steel_nugget");
    public static final DeferredItem<Item> ZINC_NUGGET = ITEMS.registerSimpleItem("zinc_nugget");
    // Raw Ores -- dropped by mining the ore block directly (silk touch gets the block instead),
    // matching vanilla's raw_iron/raw_gold/raw_copper pattern. Feeds the Crusher exactly like the
    // ore block used to; Lepidolite has no entry here since it already skips the Crusher entirely
    // (see LEPIDOLITE below).
    public static final DeferredItem<Item> RAW_ARGENTITE_ORE = ITEMS.registerSimpleItem("raw_argentite_ore");
    public static final DeferredItem<Item> RAW_BAUXITE_ORE = ITEMS.registerSimpleItem("raw_bauxite_ore");
    public static final DeferredItem<Item> RAW_CASSITERITE_ORE = ITEMS.registerSimpleItem("raw_cassiterite_ore");
    public static final DeferredItem<Item> RAW_CHROMITE_ORE = ITEMS.registerSimpleItem("raw_chromite_ore");
    public static final DeferredItem<Item> RAW_COBALTITE_ORE = ITEMS.registerSimpleItem("raw_cobaltite_ore");
    public static final DeferredItem<Item> RAW_GALENA_ORE = ITEMS.registerSimpleItem("raw_galena_ore");
    public static final DeferredItem<Item> RAW_GARNIERITE_ORE = ITEMS.registerSimpleItem("raw_garnierite_ore");
    public static final DeferredItem<Item> RAW_PYROLUSITE_ORE = ITEMS.registerSimpleItem("raw_pyrolusite_ore");
    public static final DeferredItem<Item> RAW_RUTILE_ORE = ITEMS.registerSimpleItem("raw_rutile_ore");
    public static final DeferredItem<Item> RAW_SCHEELITE_ORE = ITEMS.registerSimpleItem("raw_scheelite_ore");
    public static final DeferredItem<Item> RAW_SPHALERITE_ORE = ITEMS.registerSimpleItem("raw_sphalerite_ore");
    public static final DeferredItem<Item> RAW_RHENIITE_ORE = ITEMS.registerSimpleItem("raw_rheniite_ore");
    // Crushed Ores
    public static final DeferredItem<Item> CRUSHED_ARGENTITE_ORE = ITEMS.registerSimpleItem("crushed_argentite_ore");
    public static final DeferredItem<Item> CRUSHED_BAUXITE_ORE = ITEMS.registerSimpleItem("crushed_bauxite_ore");
    public static final DeferredItem<Item> CRUSHED_CASSITERITE_ORE = ITEMS.registerSimpleItem("crushed_cassiterite_ore");
    public static final DeferredItem<Item> CRUSHED_CHROMITE_ORE = ITEMS.registerSimpleItem("crushed_chromite_ore");
    public static final DeferredItem<Item> CRUSHED_COBALTITE_ORE = ITEMS.registerSimpleItem("crushed_cobaltite_ore");
    public static final DeferredItem<Item> CRUSHED_GALENA_ORE = ITEMS.registerSimpleItem("crushed_galena_ore");
    public static final DeferredItem<Item> CRUSHED_GARNIERITE_ORE = ITEMS.registerSimpleItem("crushed_garnierite_ore");
    public static final DeferredItem<Item> CRUSHED_GOLD_ORE = ITEMS.registerSimpleItem("crushed_gold_ore");
    public static final DeferredItem<Item> CRUSHED_IRON_ORE = ITEMS.registerSimpleItem("crushed_iron_ore");
    public static final DeferredItem<Item> CRUSHED_PYROLUSITE_ORE = ITEMS.registerSimpleItem("crushed_pyrolusite_ore");
    public static final DeferredItem<Item> CRUSHED_RHENIITE_ORE = ITEMS.registerSimpleItem("crushed_rheniite_ore");
    public static final DeferredItem<Item> CRUSHED_RUTILE_ORE = ITEMS.registerSimpleItem("crushed_rutile_ore");
    public static final DeferredItem<Item> CRUSHED_SPHALERITE_ORE = ITEMS.registerSimpleItem("crushed_sphalerite_ore");
    public static final DeferredItem<Item> CRUSHED_SCHEELITE_ORE = ITEMS.registerSimpleItem("crushed_scheelite_ore");
    // Misc Resources
    public static final DeferredItem<Item> LEPIDOLITE = ITEMS.registerSimpleItem("lepidolite");
    public static final DeferredItem<Item> LITHIUM_DUST = ITEMS.registerSimpleItem("lithium_dust");
    public static final DeferredItem<Item> CRUSHED_COAL = ITEMS.registerSimpleItem("crushed_coal");
    public static final DeferredItem<Item> CRUSHED_DIAMOND = ITEMS.registerSimpleItem("crushed_diamond");
    public static final DeferredItem<Item> TUNGSTEN_CARBIDE_DUST = ITEMS.registerSimpleItem("tungsten_carbide_dust");
    public static final DeferredItem<Item> PHOSPHORUS = ITEMS.registerSimpleItem("phosphorus");
    public static final DeferredItem<Item> ARSENIC = ITEMS.registerSimpleItem("arsenic");
    public static final DeferredItem<Item> LEAD_SULFATE = ITEMS.registerSimpleItem("lead_sulfate");
    public static final DeferredItem<Item> CALCIUM_OXIDE = ITEMS.registerSimpleItem("calcium_oxide");
    public static final DeferredItem<Item> POTASSIUM_NITRATE = ITEMS.registerSimpleItem("potassium_nitrate");
    public static final DeferredItem<Item> LITHIUM_IRON_PHOSPHATE = ITEMS.registerSimpleItem("lithium_iron_phosphate");
    public static final DeferredItem<Item> WELDING_FLUX = ITEMS.registerSimpleItem("welding_flux");
    public static final DeferredItem<Item> ETHYLENE_BOTTLE = ITEMS.registerSimpleItem("ethylene_bottle");
    public static final DeferredItem<Item> ETHYLENE_GLYCOL_BOTTLE = ITEMS.registerSimpleItem("ethylene_glycol_bottle");
    public static final DeferredItem<Item> METHANE_BOTTLE = ITEMS.registerSimpleItem("methane_bottle");
    public static final DeferredItem<Item> PETROLEUM_BOTTLE = ITEMS.registerSimpleItem("petroleum_bottle");
    public static final DeferredItem<Item> SULFURIC_ACID_BOTTLE = ITEMS.registerSimpleItem("sulfuric_acid_bottle");
    // Lixiviants -- the leaching reagents an Autoclave recipe requires, matching each ore's real
    // acid/alkaline/cyanide leaching chemistry. See sulfuric_acid_bottle above for the acid case.
    public static final DeferredItem<Item> SODIUM_HYDROXIDE_BOTTLE = ITEMS.registerSimpleItem("sodium_hydroxide_bottle");
    public static final DeferredItem<Item> SODIUM_CYANIDE_BOTTLE = ITEMS.registerSimpleItem("sodium_cyanide_bottle");
    // Pregnant leach solutions -- the Autoclave's output, and the Chemical Reactor's precipitation
    // input. Each is the real intermediate compound for that metal's leaching route.
    public static final DeferredItem<Item> NICKEL_SULFATE_BOTTLE = ITEMS.registerSimpleItem("nickel_sulfate_bottle");
    public static final DeferredItem<Item> ZINC_SULFATE_BOTTLE = ITEMS.registerSimpleItem("zinc_sulfate_bottle");
    public static final DeferredItem<Item> COBALT_SULFATE_BOTTLE = ITEMS.registerSimpleItem("cobalt_sulfate_bottle");
    public static final DeferredItem<Item> MANGANESE_SULFATE_BOTTLE = ITEMS.registerSimpleItem("manganese_sulfate_bottle");
    public static final DeferredItem<Item> TITANYL_SULFATE_BOTTLE = ITEMS.registerSimpleItem("titanyl_sulfate_bottle");
    public static final DeferredItem<Item> SODIUM_ALUMINATE_BOTTLE = ITEMS.registerSimpleItem("sodium_aluminate_bottle");
    public static final DeferredItem<Item> SILVER_CYANIDE_BOTTLE = ITEMS.registerSimpleItem("silver_cyanide_bottle");
    // Metal concentrates -- precipitated out of a leach solution by the Chemical Reactor; smelts
    // into the metal's ingot at a better yield than smelting the crushed ore directly.
    public static final DeferredItem<Item> NICKEL_CONCENTRATE = ITEMS.registerSimpleItem("nickel_concentrate");
    public static final DeferredItem<Item> ZINC_CONCENTRATE = ITEMS.registerSimpleItem("zinc_concentrate");
    public static final DeferredItem<Item> COBALT_CONCENTRATE = ITEMS.registerSimpleItem("cobalt_concentrate");
    public static final DeferredItem<Item> MANGANESE_CONCENTRATE = ITEMS.registerSimpleItem("manganese_concentrate");
    public static final DeferredItem<Item> SYNTHETIC_RUTILE = ITEMS.registerSimpleItem("synthetic_rutile");
    public static final DeferredItem<Item> ALUMINA = ITEMS.registerSimpleItem("alumina");
    public static final DeferredItem<Item> SILVER_CONCENTRATE = ITEMS.registerSimpleItem("silver_concentrate");
    // Calcium sulfate (gypsum) -- the real byproduct of neutralizing a sulfate leach solution with
    // calcium oxide during precipitation. Not consumed by anything yet; noted as a future ROADMAP
    // item (decorative blocks, or a Cultivator fertilizer input).
    public static final DeferredItem<Item> CALCIUM_SULFATE = ITEMS.registerSimpleItem("calcium_sulfate");
    public static final DeferredItem<Item> COAL_COKE = ITEMS.registerSimpleItem("coal_coke");
    public static final DeferredItem<Item> SILICON = ITEMS.registerSimpleItem("silicon");
    public static final DeferredItem<Item> BITUMEN = ITEMS.registerSimpleItem("bitumen");
    public static final DeferredItem<Item> OILY_SAND = ITEMS.registerSimpleItem("oily_sand");
    public static final DeferredItem<Item> PEAT = ITEMS.registerSimpleItem("peat");
    public static final DeferredItem<Item> FERTILIZER = ITEMS.registerSimpleItem("fertilizer");
    public static final DeferredItem<Item> HDPE_SHEET = ITEMS.registerSimpleItem("hdpe_sheet");
    public static final DeferredItem<Item> CERAMIC_FIBER = ITEMS.registerSimpleItem("ceramic_fiber");
    public static final DeferredItem<Item> GRAPHITE_ROD = ITEMS.registerSimpleItem("graphite_rod");
    public static final DeferredItem<Item> REFRACTORY_BRICK = ITEMS.registerSimpleItem("refractory_brick");
    public static final DeferredItem<Item> REFRACTORY_COMPOSITE = ITEMS.registerSimpleItem("refractory_composite");
    public static final DeferredItem<Item> PERMANENT_MAGNET = ITEMS.registerSimpleItem("permanent_magnet");
    public static final DeferredItem<Item> DRY_CELL = ITEMS.registerSimpleItem("dry_cell");
    public static final DeferredItem<Item> DRY_CELL_BANK = ITEMS.registerSimpleItem("dry_cell_bank");
    public static final DeferredItem<Item> BATTERY_CELL = ITEMS.registerSimpleItem("battery_cell");
    public static final DeferredItem<Item> BATTERY_BANK = ITEMS.registerSimpleItem("battery_bank");
    public static final DeferredItem<Item> LITHIUM_BATTERY_CELL = ITEMS.registerSimpleItem("lithium_battery_cell");
    public static final DeferredItem<Item> LITHIUM_BATTERY_BANK = ITEMS.registerSimpleItem("lithium_battery_bank");
    public static final DeferredItem<Item> CONDUCTING_ELEMENT = ITEMS.registerSimpleItem("conducting_element");
    public static final DeferredItem<Item> PRINTED_CIRCUIT_BOARD = ITEMS.registerSimpleItem("printed_circuit_board");
    public static final DeferredItem<Item> INTEGRATED_CIRCUIT = ITEMS.registerSimpleItem("integrated_circuit");
    public static final DeferredItem<Item> CONTROLLER_BOARD = ITEMS.registerSimpleItem("controller_board");
    public static final DeferredItem<Item> CERAMIC_CAPACITOR = ITEMS.registerSimpleItem("ceramic_capacitor");
    public static final DeferredItem<Item> ELECTROLYTIC_CAPACITOR = ITEMS.registerSimpleItem("electrolytic_capacitor");
    public static final DeferredItem<Item> ELECTRIC_MOTOR = ITEMS.registerSimpleItem("electric_motor");
    public static final DeferredItem<Item> STATOR = ITEMS.registerSimpleItem("stator");
    public static final DeferredItem<Item> ROTOR = ITEMS.registerSimpleItem("rotor");
    public static final DeferredItem<Item> FIELD_COIL = ITEMS.registerSimpleItem("field_coil");
    public static final DeferredItem<Item> GEAR = ITEMS.registerSimpleItem("gear");
    public static final DeferredItem<Item> HEAT_SINK = ITEMS.registerSimpleItem("heat_sink");
    public static final DeferredItem<Item> HEATING_ELEMENT = ITEMS.registerSimpleItem("heating_element");
    public static final DeferredItem<Item> INDUCTION_CORE = ITEMS.registerSimpleItem("induction_core");
    // Real thermocouples pair Constantan with Copper for the Seebeck effect -- this is that pair,
    // as an installed (not consumed) upgrade for the Thermoelectric Generator. See
    // ThermoelectricGeneratorBlockEntity#hasCoupling.
    public static final DeferredItem<Item> THERMOELECTRIC_COUPLING = ITEMS.registerSimpleItem("thermoelectric_coupling");
    public static final DeferredItem<Item> MAGNET_WIRE = ITEMS.registerSimpleItem("magnet_wire");
    public static final DeferredItem<Item> MEMORY_WIRE = ITEMS.registerSimpleItem("memory_wire");
    public static final DeferredItem<Item> RESISTANCE_WIRE = ITEMS.registerSimpleItem("resistance_wire");
    public static final DeferredItem<Item> SOLDER_WIRE = ITEMS.registerSimpleItem("solder_wire");
    // Burr sets have 480 durability; matches the original mod's behavior where wearing one out
    // shrinks the stack instead of "breaking" it (see CrusherBlockEntity#processRecipe).
    public static final DeferredItem<Item> BRASS_BURR_SET = ITEMS.registerSimpleItem("brass_burr_set", props -> props.durability(480));
    public static final DeferredItem<Item> STEEL_BURR_SET = ITEMS.registerSimpleItem("steel_burr_set", props -> props.durability(480));
    public static final DeferredItem<Item> CHROMIUM_BURR_SET = ITEMS.registerSimpleItem("chromium_burr_set", props -> props.durability(480));
    public static final DeferredItem<Item> TUNGSTEN_CARBIDE_BURR_SET = ITEMS.registerSimpleItem("tungsten_carbide_burr_set", props -> props.durability(480));
    // Preserved from the original mod: the tungsten_rhenium burr set has no durability set (never wears out).
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_BURR_SET = ITEMS.registerSimpleItem("tungsten_rhenium_burr_set");
    public static final DeferredItem<Item> LEAD_SULFATE_PLATE = ITEMS.registerSimpleItem("lead_sulfate_plate");
    public static final DeferredItem<Item> SILICON_PLATE = ITEMS.registerSimpleItem("silicon_plate");
    public static final DeferredItem<Item> STEEL_PLATE = ITEMS.registerSimpleItem("steel_plate");
    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.registerSimpleItem("copper_plate");
    public static final DeferredItem<Item> INVAR_PLATE = ITEMS.registerSimpleItem("invar_plate");
    public static final DeferredItem<Item> ALUMINUM_PLATE = ITEMS.registerSimpleItem("aluminum_plate");
    public static final DeferredItem<Item> ALUMINUM_FOIL = ITEMS.registerSimpleItem("aluminum_foil");
    // Tools -- 5 tiers (Steel/Cobalt Steel/Stellite/Tungsten Steel/Tungsten-Rhenium), see ModToolMaterials
    // for the real-world justification behind each. Pickaxe/sword are plain Item + Properties#pickaxe/
    // sword; axe/hoe/shovel keep their dedicated classes for their unique right-click behavior.
    public static final DeferredItem<Item> STEEL_PICKAXE = ITEMS.registerSimpleItem("steel_pickaxe", props -> props.pickaxe(ModToolMaterials.STEEL, 1.0F, -2.8F));
    public static final DeferredItem<Item> STEEL_SWORD = ITEMS.registerSimpleItem("steel_sword", props -> props.sword(ModToolMaterials.STEEL, 3.0F, -2.4F));
    public static final DeferredItem<AxeItem> STEEL_AXE = ITEMS.registerItem("steel_axe", props -> new AxeItem(ModToolMaterials.STEEL, 5.0F, -3.0F, props), UnaryOperator.identity());
    public static final DeferredItem<HoeItem> STEEL_HOE = ITEMS.registerItem("steel_hoe", props -> new HoeItem(ModToolMaterials.STEEL, -3.0F, 0.0F, props), UnaryOperator.identity());
    public static final DeferredItem<ShovelItem> STEEL_SHOVEL = ITEMS.registerItem("steel_shovel", props -> new ShovelItem(ModToolMaterials.STEEL, 1.5F, -3.0F, props), UnaryOperator.identity());

    public static final DeferredItem<Item> COBALT_STEEL_PICKAXE = ITEMS.registerSimpleItem("cobalt_steel_pickaxe", props -> props.pickaxe(ModToolMaterials.COBALT_STEEL, 1.0F, -2.8F));
    public static final DeferredItem<Item> COBALT_STEEL_SWORD = ITEMS.registerSimpleItem("cobalt_steel_sword", props -> props.sword(ModToolMaterials.COBALT_STEEL, 3.0F, -2.4F));
    public static final DeferredItem<AxeItem> COBALT_STEEL_AXE = ITEMS.registerItem("cobalt_steel_axe", props -> new AxeItem(ModToolMaterials.COBALT_STEEL, 5.0F, -3.0F, props), UnaryOperator.identity());
    public static final DeferredItem<HoeItem> COBALT_STEEL_HOE = ITEMS.registerItem("cobalt_steel_hoe", props -> new HoeItem(ModToolMaterials.COBALT_STEEL, -3.0F, 0.0F, props), UnaryOperator.identity());
    public static final DeferredItem<ShovelItem> COBALT_STEEL_SHOVEL = ITEMS.registerItem("cobalt_steel_shovel", props -> new ShovelItem(ModToolMaterials.COBALT_STEEL, 1.5F, -3.0F, props), UnaryOperator.identity());

    public static final DeferredItem<Item> STELLITE_PICKAXE = ITEMS.registerSimpleItem("stellite_pickaxe", props -> props.pickaxe(ModToolMaterials.STELLITE, 1.0F, -2.8F));
    public static final DeferredItem<Item> STELLITE_SWORD = ITEMS.registerSimpleItem("stellite_sword", props -> props.sword(ModToolMaterials.STELLITE, 3.0F, -2.4F));
    public static final DeferredItem<AxeItem> STELLITE_AXE = ITEMS.registerItem("stellite_axe", props -> new AxeItem(ModToolMaterials.STELLITE, 5.0F, -3.0F, props), UnaryOperator.identity());
    public static final DeferredItem<HoeItem> STELLITE_HOE = ITEMS.registerItem("stellite_hoe", props -> new HoeItem(ModToolMaterials.STELLITE, -3.0F, 0.0F, props), UnaryOperator.identity());
    public static final DeferredItem<ShovelItem> STELLITE_SHOVEL = ITEMS.registerItem("stellite_shovel", props -> new ShovelItem(ModToolMaterials.STELLITE, 1.5F, -3.0F, props), UnaryOperator.identity());

    public static final DeferredItem<Item> TUNGSTEN_STEEL_PICKAXE = ITEMS.registerSimpleItem("tungsten_steel_pickaxe", props -> props.pickaxe(ModToolMaterials.TUNGSTEN_STEEL, 1.0F, -2.8F));
    public static final DeferredItem<Item> TUNGSTEN_STEEL_SWORD = ITEMS.registerSimpleItem("tungsten_steel_sword", props -> props.sword(ModToolMaterials.TUNGSTEN_STEEL, 3.0F, -2.4F));
    public static final DeferredItem<AxeItem> TUNGSTEN_STEEL_AXE = ITEMS.registerItem("tungsten_steel_axe", props -> new AxeItem(ModToolMaterials.TUNGSTEN_STEEL, 5.0F, -3.0F, props), UnaryOperator.identity());
    public static final DeferredItem<HoeItem> TUNGSTEN_STEEL_HOE = ITEMS.registerItem("tungsten_steel_hoe", props -> new HoeItem(ModToolMaterials.TUNGSTEN_STEEL, -3.0F, 0.0F, props), UnaryOperator.identity());
    public static final DeferredItem<ShovelItem> TUNGSTEN_STEEL_SHOVEL = ITEMS.registerItem("tungsten_steel_shovel", props -> new ShovelItem(ModToolMaterials.TUNGSTEN_STEEL, 1.5F, -3.0F, props), UnaryOperator.identity());

    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_PICKAXE = ITEMS.registerSimpleItem("tungsten_rhenium_pickaxe", props -> props.pickaxe(ModToolMaterials.TUNGSTEN_RHENIUM, 1.0F, -2.8F));
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_SWORD = ITEMS.registerSimpleItem("tungsten_rhenium_sword", props -> props.sword(ModToolMaterials.TUNGSTEN_RHENIUM, 3.0F, -2.4F));
    public static final DeferredItem<AxeItem> TUNGSTEN_RHENIUM_AXE = ITEMS.registerItem("tungsten_rhenium_axe", props -> new AxeItem(ModToolMaterials.TUNGSTEN_RHENIUM, 5.0F, -3.0F, props), UnaryOperator.identity());
    public static final DeferredItem<HoeItem> TUNGSTEN_RHENIUM_HOE = ITEMS.registerItem("tungsten_rhenium_hoe", props -> new HoeItem(ModToolMaterials.TUNGSTEN_RHENIUM, -3.0F, 0.0F, props), UnaryOperator.identity());
    public static final DeferredItem<ShovelItem> TUNGSTEN_RHENIUM_SHOVEL = ITEMS.registerItem("tungsten_rhenium_shovel", props -> new ShovelItem(ModToolMaterials.TUNGSTEN_RHENIUM, 1.5F, -3.0F, props), UnaryOperator.identity());

    // Armor -- only 4 of the mod's ~28 metals, see ModArmorMaterials for why. Full-set bonuses for
    // Titanium/Stellite/Tungsten-Rhenium are applied in ArmorSetBonusHandler, not here.
    public static final DeferredItem<Item> STEEL_HELMET = ITEMS.registerSimpleItem("steel_helmet", props -> props.humanoidArmor(ModArmorMaterials.STEEL, ArmorType.HELMET));
    public static final DeferredItem<Item> STEEL_CHESTPLATE = ITEMS.registerSimpleItem("steel_chestplate", props -> props.humanoidArmor(ModArmorMaterials.STEEL, ArmorType.CHESTPLATE));
    public static final DeferredItem<Item> STEEL_LEGGINGS = ITEMS.registerSimpleItem("steel_leggings", props -> props.humanoidArmor(ModArmorMaterials.STEEL, ArmorType.LEGGINGS));
    public static final DeferredItem<Item> STEEL_BOOTS = ITEMS.registerSimpleItem("steel_boots", props -> props.humanoidArmor(ModArmorMaterials.STEEL, ArmorType.BOOTS));

    public static final DeferredItem<Item> TITANIUM_HELMET = ITEMS.registerSimpleItem("titanium_helmet", props -> props.humanoidArmor(ModArmorMaterials.TITANIUM, ArmorType.HELMET));
    public static final DeferredItem<Item> TITANIUM_CHESTPLATE = ITEMS.registerSimpleItem("titanium_chestplate", props -> props.humanoidArmor(ModArmorMaterials.TITANIUM, ArmorType.CHESTPLATE));
    public static final DeferredItem<Item> TITANIUM_LEGGINGS = ITEMS.registerSimpleItem("titanium_leggings", props -> props.humanoidArmor(ModArmorMaterials.TITANIUM, ArmorType.LEGGINGS));
    public static final DeferredItem<Item> TITANIUM_BOOTS = ITEMS.registerSimpleItem("titanium_boots", props -> props.humanoidArmor(ModArmorMaterials.TITANIUM, ArmorType.BOOTS));

    public static final DeferredItem<Item> STELLITE_HELMET = ITEMS.registerSimpleItem("stellite_helmet", props -> props.humanoidArmor(ModArmorMaterials.STELLITE, ArmorType.HELMET));
    public static final DeferredItem<Item> STELLITE_CHESTPLATE = ITEMS.registerSimpleItem("stellite_chestplate", props -> props.humanoidArmor(ModArmorMaterials.STELLITE, ArmorType.CHESTPLATE));
    public static final DeferredItem<Item> STELLITE_LEGGINGS = ITEMS.registerSimpleItem("stellite_leggings", props -> props.humanoidArmor(ModArmorMaterials.STELLITE, ArmorType.LEGGINGS));
    public static final DeferredItem<Item> STELLITE_BOOTS = ITEMS.registerSimpleItem("stellite_boots", props -> props.humanoidArmor(ModArmorMaterials.STELLITE, ArmorType.BOOTS));

    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_HELMET = ITEMS.registerSimpleItem("tungsten_rhenium_helmet", props -> props.humanoidArmor(ModArmorMaterials.TUNGSTEN_RHENIUM, ArmorType.HELMET));
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_CHESTPLATE = ITEMS.registerSimpleItem("tungsten_rhenium_chestplate", props -> props.humanoidArmor(ModArmorMaterials.TUNGSTEN_RHENIUM, ArmorType.CHESTPLATE));
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_LEGGINGS = ITEMS.registerSimpleItem("tungsten_rhenium_leggings", props -> props.humanoidArmor(ModArmorMaterials.TUNGSTEN_RHENIUM, ArmorType.LEGGINGS));
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_BOOTS = ITEMS.registerSimpleItem("tungsten_rhenium_boots", props -> props.humanoidArmor(ModArmorMaterials.TUNGSTEN_RHENIUM, ArmorType.BOOTS));

    // Power tool implements -- worn/replaced independently of the tool body they're socketed
    // into. Same 5-tier durability ladder as the hand tools (ModToolMaterials), but each gets the
    // Tool component matching what it actually does: drill bits mine (pickaxe rules), chains fell
    // trees (axe rules -- logs are axe-mineable, not hardness-gated), cultivator blades till
    // (hoe rules, mostly just for durability/enchantability since tilling isn't tier-gated).
    public static final DeferredItem<Item> STEEL_DRILL_BIT = ITEMS.registerSimpleItem("steel_drill_bit", props -> props.pickaxe(ModToolMaterials.STEEL, 1.0F, -2.8F));
    public static final DeferredItem<Item> COBALT_STEEL_DRILL_BIT = ITEMS.registerSimpleItem("cobalt_steel_drill_bit", props -> props.pickaxe(ModToolMaterials.COBALT_STEEL, 1.0F, -2.8F));
    public static final DeferredItem<Item> STELLITE_DRILL_BIT = ITEMS.registerSimpleItem("stellite_drill_bit", props -> props.pickaxe(ModToolMaterials.STELLITE, 1.0F, -2.8F));
    public static final DeferredItem<Item> TUNGSTEN_STEEL_DRILL_BIT = ITEMS.registerSimpleItem("tungsten_steel_drill_bit", props -> props.pickaxe(ModToolMaterials.TUNGSTEN_STEEL, 1.0F, -2.8F));
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_DRILL_BIT = ITEMS.registerSimpleItem("tungsten_rhenium_drill_bit", props -> props.pickaxe(ModToolMaterials.TUNGSTEN_RHENIUM, 1.0F, -2.8F));

    public static final DeferredItem<Item> STEEL_CHAIN = ITEMS.registerSimpleItem("steel_chain", props -> props.axe(ModToolMaterials.STEEL, 5.0F, -3.0F));
    public static final DeferredItem<Item> COBALT_STEEL_CHAIN = ITEMS.registerSimpleItem("cobalt_steel_chain", props -> props.axe(ModToolMaterials.COBALT_STEEL, 5.0F, -3.0F));
    public static final DeferredItem<Item> STELLITE_CHAIN = ITEMS.registerSimpleItem("stellite_chain", props -> props.axe(ModToolMaterials.STELLITE, 5.0F, -3.0F));
    public static final DeferredItem<Item> TUNGSTEN_STEEL_CHAIN = ITEMS.registerSimpleItem("tungsten_steel_chain", props -> props.axe(ModToolMaterials.TUNGSTEN_STEEL, 5.0F, -3.0F));
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_CHAIN = ITEMS.registerSimpleItem("tungsten_rhenium_chain", props -> props.axe(ModToolMaterials.TUNGSTEN_RHENIUM, 5.0F, -3.0F));

    public static final DeferredItem<Item> STEEL_CULTIVATOR_BLADE = ITEMS.registerSimpleItem("steel_cultivator_blade", props -> props.hoe(ModToolMaterials.STEEL, -3.0F, 0.0F));
    public static final DeferredItem<Item> COBALT_STEEL_CULTIVATOR_BLADE = ITEMS.registerSimpleItem("cobalt_steel_cultivator_blade", props -> props.hoe(ModToolMaterials.COBALT_STEEL, -3.0F, 0.0F));
    public static final DeferredItem<Item> STELLITE_CULTIVATOR_BLADE = ITEMS.registerSimpleItem("stellite_cultivator_blade", props -> props.hoe(ModToolMaterials.STELLITE, -3.0F, 0.0F));
    public static final DeferredItem<Item> TUNGSTEN_STEEL_CULTIVATOR_BLADE = ITEMS.registerSimpleItem("tungsten_steel_cultivator_blade", props -> props.hoe(ModToolMaterials.TUNGSTEN_STEEL, -3.0F, 0.0F));
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_CULTIVATOR_BLADE = ITEMS.registerSimpleItem("tungsten_rhenium_cultivator_blade", props -> props.hoe(ModToolMaterials.TUNGSTEN_RHENIUM, -3.0F, 0.0F));

    // Battery packs -- rechargeable FE storage for the power tools, crafted from the lithium
    // battery chain (the only chemistry in the mod that's actually right for a cordless tool).
    public static final DeferredItem<BatteryPackItem> BATTERY_PACK = ITEMS.registerItem("battery_pack", props -> new BatteryPackItem(props, 100_000), UnaryOperator.identity());
    public static final DeferredItem<BatteryPackItem> ADVANCED_BATTERY_PACK = ITEMS.registerItem("advanced_battery_pack", props -> new BatteryPackItem(props, 300_000), UnaryOperator.identity());

    // Power tool bodies -- crafted once; the socketed implement and battery pack (Part 7) are
    // what actually determine what the tool can do and how well.
    public static final DeferredItem<PowerDrillItem> POWER_DRILL = ITEMS.registerItem("power_drill", props -> new PowerDrillItem(props.stacksTo(1)), UnaryOperator.identity());
    public static final DeferredItem<ChainsawItem> CHAINSAW = ITEMS.registerItem("chainsaw", props -> new ChainsawItem(props.stacksTo(1)), UnaryOperator.identity());
    public static final DeferredItem<CultivatorItem> CULTIVATOR = ITEMS.registerItem("cultivator", props -> new CultivatorItem(props.stacksTo(1)), UnaryOperator.identity());

    // Prospector -- a handheld ore magnetometer, not a mining tool. Socket a crushed-ore item (or
    // raw lepidolite) to calibrate the scan, right-click to sweep. Real cordless-scanner-style
    // cooldown between sweeps rather than spammable every tick.
    public static final DeferredItem<ProspectorItem> PROSPECTOR = ITEMS.registerItem("prospector", props -> new ProspectorItem(props.stacksTo(1).useCooldown(1.5F)), UnaryOperator.identity());

    // Guide book -- opens a static, vanilla-styled book UI (GuideBookContent, client package)
    // explaining the mod. Content is authored in GUIDE.md and generated into GuideBookData.java;
    // see tools/guide_book/gen_guide_data.py.
    public static final DeferredItem<GuideBookItem> GUIDE_BOOK = ITEMS.registerItem("guide_book", props -> new GuideBookItem(props.stacksTo(1)), UnaryOperator.identity());

    // Blocks
    // Metal Blocks
    public static final DeferredBlock<Block> ALNICO_BLOCK = BLOCKS.registerBlock("alnico_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> ALUMINUM_BLOCK = BLOCKS.registerBlock("aluminum_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> BRASS_BLOCK = BLOCKS.registerBlock("brass_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> BRONZE_BLOCK = BLOCKS.registerBlock("bronze_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> CHROMIUM_BLOCK = BLOCKS.registerBlock("chromium_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> COBALT_BLOCK = BLOCKS.registerBlock("cobalt_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> COBALT_STEEL_BLOCK = BLOCKS.registerBlock("cobalt_steel_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> CONSTANTAN_BLOCK = BLOCKS.registerBlock("constantan_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> COPPER_TUNSTEN_BLOCK = BLOCKS.registerBlock("copper_tungsten_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> ELECTRUM_BLOCK = BLOCKS.registerBlock("electrum_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> INVAR_BLOCK = BLOCKS.registerBlock("invar_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> KANTHAL_BLOCK = BLOCKS.registerBlock("kanthal_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> LEAD_BLOCK = BLOCKS.registerBlock("lead_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> MANGANESE_BLOCK = BLOCKS.registerBlock("manganese_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> RHENIUM_BLOCK = BLOCKS.registerBlock("rhenium_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> TUNGSTEN_RHENIUM_BLOCK = BLOCKS.registerBlock("tungsten_rhenium_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> NICHROME_BLOCK = BLOCKS.registerBlock("nichrome_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> NICKEL_BLOCK = BLOCKS.registerBlock("nickel_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> NIKROTHAL_BLOCK = BLOCKS.registerBlock("nikrothal_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> NITINOL_BLOCK = BLOCKS.registerBlock("nitinol_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> SILVER_BLOCK = BLOCKS.registerBlock("silver_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> SOLDER_BLOCK = BLOCKS.registerBlock("solder_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> STEEL_BLOCK = BLOCKS.registerBlock("steel_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> STELLITE_BLOCK = BLOCKS.registerBlock("stellite_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> TIN_BLOCK = BLOCKS.registerBlock("tin_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> TITANIUM_BLOCK = BLOCKS.registerBlock("titanium_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> TUNGSTEN_BLOCK = BLOCKS.registerBlock("tungsten_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> TUNGSTEN_STEEL_BLOCK = BLOCKS.registerBlock("tungsten_steel_block", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> ZINC_BLOCK = BLOCKS.registerBlock("zinc_block", MetalBlock::new, MetalBlock::newProperties);
    // Ores
    public static final DeferredBlock<Block> ARGENTITE_ORE = BLOCKS.registerBlock("argentite_ore", Tier2OreBlock::new, Tier2OreBlock::newProperties);
    public static final DeferredBlock<Block> BAUXITE_ORE = BLOCKS.registerBlock("bauxite_ore", Tier1OreBlock::new, Tier1OreBlock::newProperties);
    public static final DeferredBlock<Block> CASSITERITE_ORE = BLOCKS.registerBlock("cassiterite_ore", Tier1OreBlock::new, Tier1OreBlock::newProperties);
    public static final DeferredBlock<Block> CHROMITE_ORE = BLOCKS.registerBlock("chromite_ore", Tier3OreBlock::new, Tier3OreBlock::newProperties);
    public static final DeferredBlock<Block> COBALTITE_ORE = BLOCKS.registerBlock("cobaltite_ore", Tier3OreBlock::new, Tier3OreBlock::newProperties);
    public static final DeferredBlock<Block> GALENA_ORE = BLOCKS.registerBlock("galena_ore", Tier2OreBlock::new, Tier2OreBlock::newProperties);
    public static final DeferredBlock<Block> GARNIERITE_ORE = BLOCKS.registerBlock("garnierite_ore", Tier1OreBlock::new, Tier1OreBlock::newProperties);
    public static final DeferredBlock<Block> LEPIDOLITE_ORE = BLOCKS.registerBlock("lepidolite_ore", Tier4OreBlock::new, Tier4OreBlock::newProperties);
    public static final DeferredBlock<Block> PYROLUSITE_ORE = BLOCKS.registerBlock("pyrolusite_ore", Tier2OreBlock::new, Tier2OreBlock::newProperties);
    public static final DeferredBlock<Block> RUTILE_ORE = BLOCKS.registerBlock("rutile_ore", Tier2OreBlock::new, Tier2OreBlock::newProperties);
    public static final DeferredBlock<Block> SPHALERITE_ORE = BLOCKS.registerBlock("sphalerite_ore", Tier1OreBlock::new, Tier1OreBlock::newProperties);
    public static final DeferredBlock<Block> SCHEELITE_ORE = BLOCKS.registerBlock("scheelite_ore", Tier4OreBlock::new, Tier4OreBlock::newProperties);
    // Rheniite is a real, and genuinely extremely rare, rhenium mineral -- discovered in 1994 in
    // a single volcanic fumarole. Restricted rarity is the point, not a placeholder.
    public static final DeferredBlock<Block> RHENIITE_ORE = BLOCKS.registerBlock("rheniite_ore", Tier4OreBlock::new, Tier4OreBlock::newProperties);
    // Other Resources
    public static final DeferredBlock<Block> OIL_SAND = BLOCKS.registerBlock("oil_sand", OilSandBlock::new, OilSandBlock::newProperties);
    public static final DeferredBlock<Block> REFRACTORY_BRICKS = BLOCKS.registerBlock("refractory_bricks", Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS));
    // Machines
    public static final DeferredBlock<Block> IRON_FORGE_CORE = BLOCKS.registerBlock("iron_forge_core", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> STEEL_FORGE_CORE = BLOCKS.registerBlock("steel_forge_core", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> COBALT_FORGE_CORE = BLOCKS.registerBlock("cobalt_forge_core", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> TUNGSTEN_FORGE_CORE = BLOCKS.registerBlock("tungsten_forge_core", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> ARC_FURNACE_CORE = BLOCKS.registerBlock("arc_furnace_core", MetalBlock::new, MetalBlock::newProperties);
    public static final DeferredBlock<Block> CRUSHER = BLOCKS.registerBlock("crusher", CrusherBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> COKE_OVEN = BLOCKS.registerBlock("coke_oven", CokeOvenBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> THERMOELECTRIC_GENERATOR = BLOCKS.registerBlock("thermoelectric_generator", ThermoelectricGeneratorBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> FORGE_TIER1 = BLOCKS.registerBlock("forge_tier1",
            props -> new ForgeBlock(props, () -> ModTileEntityTypes.FORGE_TIER1.get(), ForgeTier1BlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> FORGE_TIER2 = BLOCKS.registerBlock("forge_tier2",
            props -> new ForgeBlock(props, () -> ModTileEntityTypes.FORGE_TIER2.get(), ForgeTier2BlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> FORGE_TIER3 = BLOCKS.registerBlock("forge_tier3",
            props -> new ForgeBlock(props, () -> ModTileEntityTypes.FORGE_TIER3.get(), ForgeTier3BlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> FORGE_TIER4 = BLOCKS.registerBlock("forge_tier4",
            props -> new ForgeBlock(props, () -> ModTileEntityTypes.FORGE_TIER4.get(), ForgeTier4BlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    // Arc Furnace -- a 5th forge tier under its own name/identity (see ArcFurnaceBlockEntity):
    // reuses the exact same ForgeBlock/AdvancedForgeBlockEntity/ForgeRecipe machinery as Tiers 3-4.
    public static final DeferredBlock<Block> ARC_FURNACE = BLOCKS.registerBlock("arc_furnace",
            props -> new ForgeBlock(props, () -> ModTileEntityTypes.ARC_FURNACE.get(), ArcFurnaceBlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> EXTRUDER = BLOCKS.registerBlock("extruder", ExtruderBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> SOLDERING_STATION = BLOCKS.registerBlock("soldering_station", SolderingStationBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> CHEMICAL_CENTRIFUGE = BLOCKS.registerBlock("chemical_centrifuge", ChemicalCentrifugeBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> CHEMICAL_REACTOR = BLOCKS.registerBlock("chemical_reactor", ChemicalReactorBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> BATTERY_BOX = BLOCKS.registerBlock("battery_box", BatteryBoxBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final DeferredBlock<Block> ELECTRIC_FURNACE = BLOCKS.registerBlock("electric_furnace", ElectricFurnaceBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    // Solar Panel -- passive FE generator, no fuel/recipe, see SolarPanelBlockEntity.
    public static final DeferredBlock<Block> SOLAR_PANEL = BLOCKS.registerBlock("solar_panel", SolarPanelBlock::new, SolarPanelBlock::newProperties);
    // Minimal in-mod logistics (Part 4/ROADMAP): an I/O Port exposes a neighboring machine's
    // energy capability with an Input/Output/Both filter; a Conduit relays energy between
    // whatever I/O Ports or raw machines sit at the ends of a connected run of Conduits. See
    // ConduitBlockEntity/IOPortBlockEntity.
    public static final DeferredBlock<Block> IO_PORT = BLOCKS.registerBlock("io_port", IOPortBlock::new, IOPortBlock::newProperties);
    public static final DeferredBlock<Block> CONDUIT = BLOCKS.registerBlock("conduit", ConduitBlock::new, ConduitBlock::newProperties);
    // Autoclave -- late-game leaching machine (ROADMAP Part 22): a sealed pressure vessel that
    // leaches a crushed ore with a lixiviant bottle into a pregnant leach solution, which the
    // Chemical Reactor then precipitates into a metal concentrate. See GUIDE.md.
    public static final DeferredBlock<Block> AUTOCLAVE = BLOCKS.registerBlock("autoclave", AutoclaveBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));

    // Block Items
    // Metal Blocks
    public static final DeferredItem<BlockItem> ALNICO_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("alnico_block", ALNICO_BLOCK);
    public static final DeferredItem<BlockItem> ALUMINUM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("aluminum_block", ALUMINUM_BLOCK);
    public static final DeferredItem<BlockItem> BRASS_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("brass_block", BRASS_BLOCK);
    public static final DeferredItem<BlockItem> BRONZE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("bronze_block", BRONZE_BLOCK);
    public static final DeferredItem<BlockItem> CHROMIUM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("chromium_block", CHROMIUM_BLOCK);
    public static final DeferredItem<BlockItem> COBALT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("cobalt_block", COBALT_BLOCK);
    public static final DeferredItem<BlockItem> COBALT_STEEL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("cobalt_steel_block", COBALT_STEEL_BLOCK);
    public static final DeferredItem<BlockItem> CONSTANTAN_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("constantan_block", CONSTANTAN_BLOCK);
    public static final DeferredItem<BlockItem> COPPER_TUNGSTEN_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("copper_tungsten_block", COPPER_TUNSTEN_BLOCK);
    public static final DeferredItem<BlockItem> ELECTRUM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("electrum_block", ELECTRUM_BLOCK);
    public static final DeferredItem<BlockItem> INVAR_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("invar_block", INVAR_BLOCK);
    public static final DeferredItem<BlockItem> KANTHAL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("kanthal_block", KANTHAL_BLOCK);
    public static final DeferredItem<BlockItem> LEAD_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("lead_block", LEAD_BLOCK);
    public static final DeferredItem<BlockItem> MANGANESE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("manganese_block", MANGANESE_BLOCK);
    public static final DeferredItem<BlockItem> RHENIUM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("rhenium_block", RHENIUM_BLOCK);
    public static final DeferredItem<BlockItem> TUNGSTEN_RHENIUM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("tungsten_rhenium_block", TUNGSTEN_RHENIUM_BLOCK);
    public static final DeferredItem<BlockItem> NICHROME_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("nichrome_block", NICHROME_BLOCK);
    public static final DeferredItem<BlockItem> NICKEL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("nickel_block", NICKEL_BLOCK);
    public static final DeferredItem<BlockItem> NIKROTHAL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("nikrothal_block", NIKROTHAL_BLOCK);
    public static final DeferredItem<BlockItem> NITINOL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("nitinol_block", NITINOL_BLOCK);
    public static final DeferredItem<BlockItem> SILVER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("silver_block", SILVER_BLOCK);
    public static final DeferredItem<BlockItem> SOLDER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("solder_block", SOLDER_BLOCK);
    public static final DeferredItem<BlockItem> STEEL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("steel_block", STEEL_BLOCK);
    public static final DeferredItem<BlockItem> STELLITE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("stellite_block", STELLITE_BLOCK);
    public static final DeferredItem<BlockItem> TIN_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("tin_block", TIN_BLOCK);
    public static final DeferredItem<BlockItem> TITANIUM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("titanium_block", TITANIUM_BLOCK);
    public static final DeferredItem<BlockItem> TUNGSTEN_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("tungsten_block", TUNGSTEN_BLOCK);
    public static final DeferredItem<BlockItem> TUNGSTEN_STEEL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("tungsten_steel_block", TUNGSTEN_STEEL_BLOCK);
    public static final DeferredItem<BlockItem> ZINC_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("zinc_block", ZINC_BLOCK);
    // Ores
    public static final DeferredItem<BlockItem> ARGENTITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("argentite_ore", ARGENTITE_ORE);
    public static final DeferredItem<BlockItem> BAUXITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("bauxite_ore", BAUXITE_ORE);
    public static final DeferredItem<BlockItem> CASSITERITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("cassiterite_ore", CASSITERITE_ORE);
    public static final DeferredItem<BlockItem> CHROMITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("chromite_ore", CHROMITE_ORE);
    public static final DeferredItem<BlockItem> COBALTITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("cobaltite_ore", COBALTITE_ORE);
    public static final DeferredItem<BlockItem> GALENA_ORE_ITEM = ITEMS.registerSimpleBlockItem("galena_ore", GALENA_ORE);
    public static final DeferredItem<BlockItem> GARNIERITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("garnierite_ore", GARNIERITE_ORE);
    public static final DeferredItem<BlockItem> LEPIDOLITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("lepidolite_ore", LEPIDOLITE_ORE);
    public static final DeferredItem<BlockItem> PYROLUSITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("pyrolusite_ore", PYROLUSITE_ORE);
    public static final DeferredItem<BlockItem> RUTILE_ORE_ITEM = ITEMS.registerSimpleBlockItem("rutile_ore", RUTILE_ORE);
    public static final DeferredItem<BlockItem> SPHALERITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("sphalerite_ore", SPHALERITE_ORE);
    public static final DeferredItem<BlockItem> SCHEELITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("scheelite_ore", SCHEELITE_ORE);
    public static final DeferredItem<BlockItem> RHENIITE_ORE_ITEM = ITEMS.registerSimpleBlockItem("rheniite_ore", RHENIITE_ORE);
    // Other Resources
    public static final DeferredItem<BlockItem> OIL_SAND_ITEM = ITEMS.registerSimpleBlockItem("oil_sand", OIL_SAND);
    public static final DeferredItem<BlockItem> REFRACTORY_BRICKS_ITEM = ITEMS.registerSimpleBlockItem("refractory_bricks", REFRACTORY_BRICKS);
    // Machines
    public static final DeferredItem<BlockItem> IRON_FORGE_CORE_ITEM = ITEMS.registerSimpleBlockItem("iron_forge_core", IRON_FORGE_CORE);
    public static final DeferredItem<BlockItem> STEEL_FORGE_CORE_ITEM = ITEMS.registerSimpleBlockItem("steel_forge_core", STEEL_FORGE_CORE);
    public static final DeferredItem<BlockItem> COBALT_FORGE_CORE_ITEM = ITEMS.registerSimpleBlockItem("cobalt_forge_core", COBALT_FORGE_CORE);
    public static final DeferredItem<BlockItem> TUNGSTEN_FORGE_CORE_ITEM = ITEMS.registerSimpleBlockItem("tungsten_forge_core", TUNGSTEN_FORGE_CORE);
    public static final DeferredItem<BlockItem> ARC_FURNACE_CORE_ITEM = ITEMS.registerSimpleBlockItem("arc_furnace_core", ARC_FURNACE_CORE);
    public static final DeferredItem<BlockItem> CRUSHER_ITEM = ITEMS.registerSimpleBlockItem("crusher", CRUSHER);
    public static final DeferredItem<BlockItem> COKE_OVEN_ITEM = ITEMS.registerSimpleBlockItem("coke_oven", COKE_OVEN);
    public static final DeferredItem<BlockItem> THERMOELECTRIC_GENERATOR_ITEM = ITEMS.registerSimpleBlockItem("thermoelectric_generator", THERMOELECTRIC_GENERATOR);
    public static final DeferredItem<BlockItem> FORGE_TIER1_ITEM = ITEMS.registerSimpleBlockItem("forge_tier1", FORGE_TIER1);
    public static final DeferredItem<BlockItem> FORGE_TIER2_ITEM = ITEMS.registerSimpleBlockItem("forge_tier2", FORGE_TIER2);
    public static final DeferredItem<BlockItem> FORGE_TIER3_ITEM = ITEMS.registerSimpleBlockItem("forge_tier3", FORGE_TIER3);
    public static final DeferredItem<BlockItem> FORGE_TIER4_ITEM = ITEMS.registerSimpleBlockItem("forge_tier4", FORGE_TIER4);
    public static final DeferredItem<BlockItem> ARC_FURNACE_ITEM = ITEMS.registerSimpleBlockItem("arc_furnace", ARC_FURNACE);
    public static final DeferredItem<BlockItem> EXTRUDER_ITEM = ITEMS.registerSimpleBlockItem("extruder", EXTRUDER);
    public static final DeferredItem<BlockItem> SOLDERING_STATION_ITEM = ITEMS.registerSimpleBlockItem("soldering_station", SOLDERING_STATION);
    public static final DeferredItem<BlockItem> CHEMICAL_CENTRIFUGE_ITEM = ITEMS.registerSimpleBlockItem("chemical_centrifuge", CHEMICAL_CENTRIFUGE);
    public static final DeferredItem<BlockItem> CHEMICAL_REACTOR_ITEM = ITEMS.registerSimpleBlockItem("chemical_reactor", CHEMICAL_REACTOR);
    public static final DeferredItem<BlockItem> BATTERY_BOX_ITEM = ITEMS.registerSimpleBlockItem("battery_box", BATTERY_BOX);
    public static final DeferredItem<BlockItem> ELECTRIC_FURNACE_ITEM = ITEMS.registerSimpleBlockItem("electric_furnace", ELECTRIC_FURNACE);
    public static final DeferredItem<BlockItem> SOLAR_PANEL_ITEM = ITEMS.registerSimpleBlockItem("solar_panel", SOLAR_PANEL);
    public static final DeferredItem<BlockItem> IO_PORT_ITEM = ITEMS.registerSimpleBlockItem("io_port", IO_PORT);
    public static final DeferredItem<BlockItem> AUTOCLAVE_ITEM = ITEMS.registerSimpleBlockItem("autoclave", AUTOCLAVE);
    public static final DeferredItem<BlockItem> CONDUIT_ITEM = ITEMS.registerSimpleBlockItem("conduit", CONDUIT);

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }

}
