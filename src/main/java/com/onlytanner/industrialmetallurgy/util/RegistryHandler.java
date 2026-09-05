package com.onlytanner.industrialmetallurgy.util;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.blocks.*;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier1BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier2BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier3BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier4BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ArcFurnaceBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.IOPortBlockEntity;
import com.onlytanner.industrialmetallurgy.items.BatteryPackItem;
import com.onlytanner.industrialmetallurgy.items.ChainsawItem;
import com.onlytanner.industrialmetallurgy.items.CultivatorItem;
import com.onlytanner.industrialmetallurgy.items.GuideBookItem;
import com.onlytanner.industrialmetallurgy.items.PowerDrillItem;
import com.onlytanner.industrialmetallurgy.items.ProspectorItem;
import com.mojang.math.Quadrant;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.core.Direction;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RegistryHandler {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IndustrialMetallurgy.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(IndustrialMetallurgy.MODID);

    // Every block's BlockItem is registered inline (see .simpleItem() in registerBlock/etc. below)
    // rather than in a separate "Block Items" section like the rest of RegistryHandler's items --
    // Registrate's BlockBuilder#simpleItem() ties item creation directly to the block, and already
    // suppresses the item's own duplicate-of-the-block's-name lang entry (see BlockBuilder.java:
    // "The item will have no lang entry, since it would duplicate the block's"). Since these items
    // no longer show up in ITEMS.getEntries() (the DeferredRegister IndustrialMetallurgy.TAB's
    // displayItems iterates), this list preserves their creative-tab/JEI position -- same relative
    // order as the old "Block Items" section, appended right after ITEMS' own entries. Must be
    // declared before any block field below (registerBlock/etc. populate it as a side effect of
    // each block's own field initializer, and static fields initialize in textual order).
    public static final List<Supplier<Item>> BLOCK_ITEMS_IN_ORDER = new ArrayList<>();

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
    public static final DeferredItem<Item> NICHROME_INGOT = ITEMS.registerSimpleItem("nichrome_ingot");
    public static final DeferredItem<Item> NICKEL_INGOT = ITEMS.registerSimpleItem("nickel_ingot");
    public static final DeferredItem<Item> NIKROTHAL_INGOT = ITEMS.registerSimpleItem("nikrothal_ingot");
    public static final DeferredItem<Item> NITINOL_INGOT = ITEMS.registerSimpleItem("nitinol_ingot");
    // Rhenium is only smeltable in the Arc Furnace (real rhenium has the 2nd-highest melting
    // point of any element) -- see forge/rhenium_ingot.json.
    public static final DeferredItem<Item> RHENIUM_INGOT = ITEMS.registerSimpleItem("rhenium_ingot");
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerSimpleItem("silver_ingot");
    public static final DeferredItem<Item> SOLDER_INGOT = ITEMS.registerSimpleItem("solder_ingot");
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.registerSimpleItem("steel_ingot");
    public static final DeferredItem<Item> STELLITE_INGOT = ITEMS.registerSimpleItem("stellite_ingot");
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.registerSimpleItem("tin_ingot");
    public static final DeferredItem<Item> TITANIUM_INGOT = ITEMS.registerSimpleItem("titanium_ingot");
    public static final DeferredItem<Item> TUNGSTEN_INGOT = ITEMS.registerSimpleItem("tungsten_ingot");
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_INGOT = ITEMS.registerSimpleItem("tungsten_rhenium_ingot");
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
    public static final DeferredItem<Item> NICHROME_NUGGET = ITEMS.registerSimpleItem("nichrome_nugget");
    public static final DeferredItem<Item> NICKEL_NUGGET = ITEMS.registerSimpleItem("nickel_nugget");
    public static final DeferredItem<Item> NIKROTHAL_NUGGET = ITEMS.registerSimpleItem("nikrothal_nugget");
    public static final DeferredItem<Item> NITINOL_NUGGET = ITEMS.registerSimpleItem("nitinol_nugget");
    public static final DeferredItem<Item> RHENIUM_NUGGET = ITEMS.registerSimpleItem("rhenium_nugget");
    public static final DeferredItem<Item> SILVER_NUGGET = ITEMS.registerSimpleItem("silver_nugget");
    public static final DeferredItem<Item> SOLDER_NUGGET = ITEMS.registerSimpleItem("solder_nugget");
    public static final DeferredItem<Item> STEEL_NUGGET = ITEMS.registerSimpleItem("steel_nugget");
    public static final DeferredItem<Item> STELLITE_NUGGET = ITEMS.registerSimpleItem("stellite_nugget");
    public static final DeferredItem<Item> TIN_NUGGET = ITEMS.registerSimpleItem("tin_nugget");
    public static final DeferredItem<Item> TITANIUM_NUGGET = ITEMS.registerSimpleItem("titanium_nugget");
    public static final DeferredItem<Item> TUNGSTEN_NUGGET = ITEMS.registerSimpleItem("tungsten_nugget");
    public static final DeferredItem<Item> TUNGSTEN_RHENIUM_NUGGET = ITEMS.registerSimpleItem("tungsten_rhenium_nugget");
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
    public static final DeferredItem<Item> RAW_RHENIITE_ORE = ITEMS.registerSimpleItem("raw_rheniite_ore");
    public static final DeferredItem<Item> RAW_RUTILE_ORE = ITEMS.registerSimpleItem("raw_rutile_ore");
    public static final DeferredItem<Item> RAW_SCHEELITE_ORE = ITEMS.registerSimpleItem("raw_scheelite_ore");
    public static final DeferredItem<Item> RAW_SPHALERITE_ORE = ITEMS.registerSimpleItem("raw_sphalerite_ore");
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
    public static final DeferredItem<Item> CRUSHED_SCHEELITE_ORE = ITEMS.registerSimpleItem("crushed_scheelite_ore");
    public static final DeferredItem<Item> CRUSHED_SPHALERITE_ORE = ITEMS.registerSimpleItem("crushed_sphalerite_ore");
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

    // Wrench -- toggles the I/O Port's Input/Output/Both mode (see IOPortBlock); a plain
    // right-click with anything else just reports the port's current mode instead of changing it.
    public static final DeferredItem<Item> WRENCH = ITEMS.registerItem("wrench", props -> new Item(props.stacksTo(1)), UnaryOperator.identity());

    // Guide book -- opens a static, vanilla-styled book UI (GuideBookContent, client package)
    // explaining the mod. Content is authored in GUIDE.md and generated into GuideBookData.java;
    // see tools/guide_book/gen_guide_data.py.
    public static final DeferredItem<GuideBookItem> GUIDE_BOOK = ITEMS.registerItem("guide_book", props -> new GuideBookItem(props.stacksTo(1)), UnaryOperator.identity());

    // Blocks -- registered through Registrate (see IndustrialMetallurgy.REGISTRATE), a full-mod
    // migration in progress (registrate-migration branch): every block below except Conduit/I-O
    // Port (still on BLOCKS -- their multipart blockstates are bespoke follow-up work) now gets
    // its blockstate/model/loot generated by `./gradlew runData` instead of hand-authored JSON.
    // Lang stays 100% hand-authored for now (see registerBlock's LANG no-op below) since
    // Registrate's lang provider writes a whole, non-merging file that would otherwise clobber
    // the hand-authored one the moment it's non-empty.
    // Metal Blocks
    public static final BlockEntry<Block> ALNICO_BLOCK = registerBlock("alnico_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> ALUMINUM_BLOCK = registerBlock("aluminum_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> BRASS_BLOCK = registerBlock("brass_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> BRONZE_BLOCK = registerBlock("bronze_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> CHROMIUM_BLOCK = registerBlock("chromium_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> COBALT_BLOCK = registerBlock("cobalt_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> COBALT_STEEL_BLOCK = registerBlock("cobalt_steel_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> CONSTANTAN_BLOCK = registerBlock("constantan_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> COPPER_TUNSTEN_BLOCK = registerBlock("copper_tungsten_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> ELECTRUM_BLOCK = registerBlock("electrum_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> INVAR_BLOCK = registerBlock("invar_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> KANTHAL_BLOCK = registerBlock("kanthal_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> LEAD_BLOCK = registerBlock("lead_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> MANGANESE_BLOCK = registerBlock("manganese_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> NICHROME_BLOCK = registerBlock("nichrome_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> NICKEL_BLOCK = registerBlock("nickel_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> NIKROTHAL_BLOCK = registerBlock("nikrothal_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> NITINOL_BLOCK = registerBlock("nitinol_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> RHENIUM_BLOCK = registerBlock("rhenium_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> SILVER_BLOCK = registerBlock("silver_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> SOLDER_BLOCK = registerBlock("solder_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> STEEL_BLOCK = registerBlock("steel_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> STELLITE_BLOCK = registerBlock("stellite_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> TIN_BLOCK = registerBlock("tin_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> TITANIUM_BLOCK = registerBlock("titanium_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> TUNGSTEN_BLOCK = registerBlock("tungsten_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> TUNGSTEN_RHENIUM_BLOCK = registerBlock("tungsten_rhenium_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> TUNGSTEN_STEEL_BLOCK = registerBlock("tungsten_steel_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> ZINC_BLOCK = registerBlock("zinc_block", p -> new MetalBlock(p), MetalBlock::newProperties);
    // Ores -- each overworld ore's height range (-48 to 48) crosses the deepslate transition, so
    // it gets a deepslate variant sitting right next to it here, matching vanilla's iron_ore/
    // deepslate_iron_ore pairing (see the configured_feature JSON for the actual dual-target
    // world-gen wiring). Nether/End ores (chromite/cobaltite/rheniite/lepidolite/scheelite) never
    // generate in stone at all, so they have no deepslate counterpart.
    public static final BlockEntry<Block> ARGENTITE_ORE = registerOreBlock("argentite_ore", p -> new Tier2OreBlock(p), Tier2OreBlock::newProperties, RAW_ARGENTITE_ORE);
    public static final BlockEntry<Block> DEEPSLATE_ARGENTITE_ORE = registerOreBlock("deepslate_argentite_ore", p -> new Tier2OreBlock(p), Tier2OreBlock::newDeepslateProperties, RAW_ARGENTITE_ORE);
    public static final BlockEntry<Block> BAUXITE_ORE = registerOreBlock("bauxite_ore", p -> new Tier1OreBlock(p), Tier1OreBlock::newProperties, RAW_BAUXITE_ORE);
    public static final BlockEntry<Block> DEEPSLATE_BAUXITE_ORE = registerOreBlock("deepslate_bauxite_ore", p -> new Tier1OreBlock(p), Tier1OreBlock::newDeepslateProperties, RAW_BAUXITE_ORE);
    public static final BlockEntry<Block> CASSITERITE_ORE = registerOreBlock("cassiterite_ore", p -> new Tier1OreBlock(p), Tier1OreBlock::newProperties, RAW_CASSITERITE_ORE);
    public static final BlockEntry<Block> DEEPSLATE_CASSITERITE_ORE = registerOreBlock("deepslate_cassiterite_ore", p -> new Tier1OreBlock(p), Tier1OreBlock::newDeepslateProperties, RAW_CASSITERITE_ORE);
    public static final BlockEntry<Block> CHROMITE_ORE = registerOreBlock("chromite_ore", p -> new Tier3OreBlock(p), Tier3OreBlock::newProperties, RAW_CHROMITE_ORE);
    public static final BlockEntry<Block> COBALTITE_ORE = registerOreBlock("cobaltite_ore", p -> new Tier3OreBlock(p), Tier3OreBlock::newProperties, RAW_COBALTITE_ORE);
    public static final BlockEntry<Block> GALENA_ORE = registerOreBlock("galena_ore", p -> new Tier2OreBlock(p), Tier2OreBlock::newProperties, RAW_GALENA_ORE);
    public static final BlockEntry<Block> DEEPSLATE_GALENA_ORE = registerOreBlock("deepslate_galena_ore", p -> new Tier2OreBlock(p), Tier2OreBlock::newDeepslateProperties, RAW_GALENA_ORE);
    public static final BlockEntry<Block> GARNIERITE_ORE = registerOreBlock("garnierite_ore", p -> new Tier1OreBlock(p), Tier1OreBlock::newProperties, RAW_GARNIERITE_ORE);
    public static final BlockEntry<Block> DEEPSLATE_GARNIERITE_ORE = registerOreBlock("deepslate_garnierite_ore", p -> new Tier1OreBlock(p), Tier1OreBlock::newDeepslateProperties, RAW_GARNIERITE_ORE);
    // Lepidolite drops the raw mineral item directly (not a raw_lepidolite_ore item) -- it skips
    // the Crusher entirely, see LEPIDOLITE above.
    public static final BlockEntry<Block> LEPIDOLITE_ORE = registerOreBlock("lepidolite_ore", p -> new Tier4OreBlock(p), Tier4OreBlock::newProperties, LEPIDOLITE);
    public static final BlockEntry<Block> PYROLUSITE_ORE = registerOreBlock("pyrolusite_ore", p -> new Tier2OreBlock(p), Tier2OreBlock::newProperties, RAW_PYROLUSITE_ORE);
    public static final BlockEntry<Block> DEEPSLATE_PYROLUSITE_ORE = registerOreBlock("deepslate_pyrolusite_ore", p -> new Tier2OreBlock(p), Tier2OreBlock::newDeepslateProperties, RAW_PYROLUSITE_ORE);
    // Rheniite is a real, and genuinely extremely rare, rhenium mineral -- discovered in 1994 in
    // a single volcanic fumarole. Restricted rarity is the point, not a placeholder.
    public static final BlockEntry<Block> RHENIITE_ORE = registerOreBlock("rheniite_ore", p -> new Tier4OreBlock(p), Tier4OreBlock::newProperties, RAW_RHENIITE_ORE);
    public static final BlockEntry<Block> RUTILE_ORE = registerOreBlock("rutile_ore", p -> new Tier2OreBlock(p), Tier2OreBlock::newProperties, RAW_RUTILE_ORE);
    public static final BlockEntry<Block> DEEPSLATE_RUTILE_ORE = registerOreBlock("deepslate_rutile_ore", p -> new Tier2OreBlock(p), Tier2OreBlock::newDeepslateProperties, RAW_RUTILE_ORE);
    public static final BlockEntry<Block> SCHEELITE_ORE = registerOreBlock("scheelite_ore", p -> new Tier4OreBlock(p), Tier4OreBlock::newProperties, RAW_SCHEELITE_ORE);
    public static final BlockEntry<Block> SPHALERITE_ORE = registerOreBlock("sphalerite_ore", p -> new Tier1OreBlock(p), Tier1OreBlock::newProperties, RAW_SPHALERITE_ORE);
    public static final BlockEntry<Block> DEEPSLATE_SPHALERITE_ORE = registerOreBlock("deepslate_sphalerite_ore", p -> new Tier1OreBlock(p), Tier1OreBlock::newDeepslateProperties, RAW_SPHALERITE_ORE);
    // Other Resources
    public static final BlockEntry<Block> OIL_SAND = registerOreBlock("oil_sand", p -> new OilSandBlock(p), OilSandBlock::newProperties, OILY_SAND);
    public static final BlockEntry<Block> REFRACTORY_BRICKS = registerBlock("refractory_bricks", p -> new Block(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS));
    // Machines
    public static final BlockEntry<Block> IRON_FORGE_CORE = registerBlock("iron_forge_core", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> STEEL_FORGE_CORE = registerBlock("steel_forge_core", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> COBALT_FORGE_CORE = registerBlock("cobalt_forge_core", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> TUNGSTEN_FORGE_CORE = registerBlock("tungsten_forge_core", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> ARC_FURNACE_CORE = registerBlock("arc_furnace_core", p -> new MetalBlock(p), MetalBlock::newProperties);
    public static final BlockEntry<Block> CRUSHER = registerFacingBlock("crusher", p -> new CrusherBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> COKE_OVEN = registerFacingLitBlock("coke_oven", p -> new CokeOvenBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> THERMOELECTRIC_GENERATOR = registerFacingLitBlock("thermoelectric_generator", p -> new ThermoelectricGeneratorBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> FORGE_TIER1 = registerFacingLitBlock("forge_tier1",
            p -> new ForgeBlock(p, () -> ModTileEntityTypes.FORGE_TIER1.get(), ForgeTier1BlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> FORGE_TIER2 = registerFacingLitBlock("forge_tier2",
            p -> new ForgeBlock(p, () -> ModTileEntityTypes.FORGE_TIER2.get(), ForgeTier2BlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> FORGE_TIER3 = registerFacingLitBlock("forge_tier3",
            p -> new ForgeBlock(p, () -> ModTileEntityTypes.FORGE_TIER3.get(), ForgeTier3BlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> FORGE_TIER4 = registerFacingLitBlock("forge_tier4",
            p -> new ForgeBlock(p, () -> ModTileEntityTypes.FORGE_TIER4.get(), ForgeTier4BlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    // Arc Furnace -- a 5th forge tier under its own name/identity (see ArcFurnaceBlockEntity):
    // reuses the exact same ForgeBlock/AdvancedForgeBlockEntity/ForgeRecipe machinery as Tiers 3-4.
    public static final BlockEntry<Block> ARC_FURNACE = registerFacingLitBlock("arc_furnace",
            p -> new ForgeBlock(p, () -> ModTileEntityTypes.ARC_FURNACE.get(), ArcFurnaceBlockEntity::new),
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> EXTRUDER = registerFacingBlock("extruder", p -> new ExtruderBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> SOLDERING_STATION = registerFacingBlock("soldering_station", p -> new SolderingStationBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> CHEMICAL_CENTRIFUGE = registerFacingBlock("chemical_centrifuge", p -> new ChemicalCentrifugeBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> CHEMICAL_REACTOR = registerFacingBlock("chemical_reactor", p -> new ChemicalReactorBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> BATTERY_BOX = registerFacingLitBlock("battery_box", p -> new BatteryBoxBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockEntry<Block> ELECTRIC_FURNACE = registerFacingLitBlock("electric_furnace", p -> new ElectricFurnaceBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    // Solar Panel -- passive FE generator, no fuel/recipe, see SolarPanelBlockEntity.
    public static final BlockEntry<Block> SOLAR_PANEL = registerBlock("solar_panel", p -> new SolarPanelBlock(p), SolarPanelBlock::newProperties);
    // Minimal in-mod logistics (Part 4/ROADMAP): an I/O Port exposes a neighboring machine's
    // energy capability with an Input/Output/Both filter; a Conduit relays energy between
    // whatever I/O Ports or raw machines sit at the ends of a connected run of Conduits. See
    // ConduitBlockEntity/IOPortBlockEntity. Both use a bespoke multipart blockstate (see
    // registerIoPort/registerConduit below) since they're the only two blocks in the mod whose
    // rendering is built from independently-toggled part models rather than a single model per
    // property combination.
    public static final BlockEntry<Block> IO_PORT = registerIoPort();
    public static final BlockEntry<Block> CONDUIT = registerConduit();
    // Autoclave -- late-game leaching machine (ROADMAP Part 22): a sealed pressure vessel that
    // leaches a crushed ore with a lixiviant bottle into a pregnant leach solution, which the
    // Chemical Reactor then precipitates into a metal concentrate. See GUIDE.md.
    public static final BlockEntry<Block> AUTOCLAVE = registerFacingBlock("autoclave", p -> new AutoclaveBlock(p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));

    private static void trackBlockItem(BlockEntry<Block> block) {
        BLOCK_ITEMS_IN_ORDER.add(() -> block.get().asItem());
    }

    // Registers a block through Registrate with its default cube_all blockstate and drop-self
    // loot (both from BlockBuilder.create()'s own defaults -- see Registrate's BlockBuilder.java),
    // suppressing the also-defaulted lang generation (see the Blocks section header comment).
    private static BlockEntry<Block> registerBlock(String name, NonNullFunction<BlockBehaviour.Properties, Block> factory, Supplier<BlockBehaviour.Properties> properties) {
        BlockEntry<Block> block = IndustrialMetallurgy.REGISTRATE.<Block>block(name, factory)
                .properties(p -> properties.get())
                .simpleItem()
                .setData(ProviderType.LANG, (ctx, prov) -> {})
                .register();
        trackBlockItem(block);
        return block;
    }

    // Registers an ore-drop-pattern block (silk touch keeps the block, otherwise a fortune-boosted
    // raw drop item -- BlockLootSubProvider#createOreDrop) through Registrate, keeping the default
    // cube_all blockstate. Used for ore blocks and oil_sand (whose "raw drop" is oily_sand).
    private static BlockEntry<Block> registerOreBlock(String name, NonNullFunction<BlockBehaviour.Properties, Block> factory,
            Supplier<BlockBehaviour.Properties> properties, DeferredItem<Item> drop) {
        BlockEntry<Block> block = IndustrialMetallurgy.REGISTRATE.<Block>block(name, factory)
                .properties(p -> properties.get())
                .simpleItem()
                .loot((prov, b) -> prov.add(b, prov.createOreDrop(b, drop.get())))
                .setData(ProviderType.LANG, (ctx, prov) -> {})
                .register();
        trackBlockItem(block);
        return block;
    }

    // Registers a furnace-like block whose blockstate is just 4 horizontal-facing rotations of a
    // single existing model (industrialmetallurgy:block/<name>) -- crusher, extruder, soldering
    // station, chemical centrifuge/reactor, autoclave. Reuses the existing hand-authored model
    // file rather than regenerating one, so only the blockstate JSON moves to datagen.
    private static BlockEntry<Block> registerFacingBlock(String name, NonNullFunction<BlockBehaviour.Properties, Block> factory, Supplier<BlockBehaviour.Properties> properties) {
        BlockEntry<Block> block = IndustrialMetallurgy.REGISTRATE.<Block>block(name, factory)
                .properties(p -> properties.get())
                .simpleItem()
                .blockstate(() -> (ctx, prov) -> prov.generateHorizontalBlock(ctx.getEntry(), BlockModelGenerators.plainVariant(prov.modLoc("block/" + name))))
                .setData(ProviderType.LANG, (ctx, prov) -> {})
                .register();
        trackBlockItem(block);
        return block;
    }

    // Registers a furnace-like block whose blockstate is LIT (selects between the existing
    // industrialmetallurgy:block/<name> and block/lit_<name> models) crossed with 4
    // horizontal-facing rotations -- matches vanilla's own furnace/smoker/blast_furnace pattern
    // (BlockModelGenerators#createFurnace), but references the existing hand-authored models
    // instead of regenerating new ones from a texture mapping.
    private static BlockEntry<Block> registerFacingLitBlock(String name, NonNullFunction<BlockBehaviour.Properties, Block> factory, Supplier<BlockBehaviour.Properties> properties) {
        BlockEntry<Block> block = IndustrialMetallurgy.REGISTRATE.<Block>block(name, factory)
                .properties(p -> properties.get())
                .simpleItem()
                .blockstate(() -> (ctx, prov) -> prov.blockStateOutput.accept(
                        MultiVariantGenerator.dispatch(ctx.getEntry())
                                .with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT,
                                        BlockModelGenerators.plainVariant(prov.modLoc("block/lit_" + name)),
                                        BlockModelGenerators.plainVariant(prov.modLoc("block/" + name))))
                                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING)))
                .setData(ProviderType.LANG, (ctx, prov) -> {})
                .register();
        trackBlockItem(block);
        return block;
    }

    // Adds the 6 direction-arm multipart entries shared by Conduit and I/O Port (see both blocks'
    // blockstates/*.json multipart arrays): an unconditional core is added separately by each
    // caller, this only covers "when north/east/south/west/up/down is true, apply an arm model".
    // The horizontal arm model is the same industrialmetallurgy:block/conduit_arm_horizontal file
    // rotated 0/90/180/270 by Y_ROT for north/east/south/west, matching the hand-authored JSON.
    private static MultiPartGenerator addConduitArms(MultiPartGenerator generator, RegistrateBlockModelGenerator prov) {
        MultiVariant armHorizontal = BlockModelGenerators.plainVariant(prov.modLoc("block/conduit_arm_horizontal"));
        return generator
                .with(new ConditionBuilder().term(BlockStateProperties.NORTH, true), armHorizontal)
                .with(new ConditionBuilder().term(BlockStateProperties.EAST, true), armHorizontal.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                .with(new ConditionBuilder().term(BlockStateProperties.SOUTH, true), armHorizontal.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                .with(new ConditionBuilder().term(BlockStateProperties.WEST, true), armHorizontal.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                .with(new ConditionBuilder().term(BlockStateProperties.UP, true), BlockModelGenerators.plainVariant(prov.modLoc("block/conduit_arm_up")))
                .with(new ConditionBuilder().term(BlockStateProperties.DOWN, true), BlockModelGenerators.plainVariant(prov.modLoc("block/conduit_arm_down")));
    }

    private static BlockEntry<Block> registerConduit() {
        BlockEntry<Block> block = IndustrialMetallurgy.REGISTRATE.<Block>block("conduit", p -> new ConduitBlock(p))
                .properties(p -> ConduitBlock.newProperties())
                .simpleItem()
                .blockstate(() -> (ctx, prov) -> prov.blockStateOutput.accept(
                        addConduitArms(
                                MultiPartGenerator.multiPart(ctx.getEntry())
                                        .with(BlockModelGenerators.plainVariant(prov.modLoc("block/conduit_core"))),
                                prov)))
                .setData(ProviderType.LANG, (ctx, prov) -> {})
                .register();
        trackBlockItem(block);
        return block;
    }

    private static BlockEntry<Block> registerIoPort() {
        BlockEntry<Block> block = IndustrialMetallurgy.REGISTRATE.<Block>block("io_port", p -> new IOPortBlock(p))
                .properties(p -> IOPortBlock.newProperties())
                .simpleItem()
                .blockstate(() -> (ctx, prov) -> {
                    MultiPartGenerator generator = MultiPartGenerator.multiPart(ctx.getEntry())
                            .with(new ConditionBuilder().term(IOPortBlock.MODE, IOPortBlockEntity.Mode.INPUT), BlockModelGenerators.plainVariant(prov.modLoc("block/io_port_input_core")))
                            .with(new ConditionBuilder().term(IOPortBlock.MODE, IOPortBlockEntity.Mode.OUTPUT), BlockModelGenerators.plainVariant(prov.modLoc("block/io_port_output_core")))
                            .with(new ConditionBuilder().term(IOPortBlock.MODE, IOPortBlockEntity.Mode.BOTH), BlockModelGenerators.plainVariant(prov.modLoc("block/io_port_both_core")))
                            .with(new ConditionBuilder().term(IOPortBlock.MODE, IOPortBlockEntity.Mode.DISABLED), BlockModelGenerators.plainVariant(prov.modLoc("block/io_port_disabled_core")));
                    generator = addConduitArms(generator, prov);
                    MultiVariant hostHorizontal = BlockModelGenerators.plainVariant(prov.modLoc("block/io_port_host_horizontal"));
                    generator = generator
                            .with(new ConditionBuilder().term(IOPortBlock.FACING, Direction.NORTH), hostHorizontal)
                            .with(new ConditionBuilder().term(IOPortBlock.FACING, Direction.SOUTH), hostHorizontal.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .with(new ConditionBuilder().term(IOPortBlock.FACING, Direction.EAST), hostHorizontal.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .with(new ConditionBuilder().term(IOPortBlock.FACING, Direction.WEST), hostHorizontal.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .with(new ConditionBuilder().term(IOPortBlock.FACING, Direction.UP), BlockModelGenerators.plainVariant(prov.modLoc("block/io_port_host_down")))
                            .with(new ConditionBuilder().term(IOPortBlock.FACING, Direction.DOWN), BlockModelGenerators.plainVariant(prov.modLoc("block/io_port_host_up")));
                    prov.blockStateOutput.accept(generator);
                })
                .setData(ProviderType.LANG, (ctx, prov) -> {})
                .register();
        trackBlockItem(block);
        return block;
    }

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }

}
