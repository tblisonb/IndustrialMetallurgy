package com.onlytanner.industrialmetallurgy;

import com.onlytanner.industrialmetallurgy.client.gui.AdvancedForgeScreen;
import com.onlytanner.industrialmetallurgy.client.gui.AutoclaveScreen;
import com.onlytanner.industrialmetallurgy.client.gui.BasicForgeScreen;
import com.onlytanner.industrialmetallurgy.client.gui.BatteryBoxScreen;
import com.onlytanner.industrialmetallurgy.client.gui.ElectricFurnaceScreen;
import com.onlytanner.industrialmetallurgy.client.gui.CokeOvenScreen;
import com.onlytanner.industrialmetallurgy.client.gui.CrusherScreen;
import com.onlytanner.industrialmetallurgy.client.gui.ChemicalCentrifugeScreen;
import com.onlytanner.industrialmetallurgy.client.gui.ChemicalReactorScreen;
import com.onlytanner.industrialmetallurgy.client.gui.ExtruderScreen;
import com.onlytanner.industrialmetallurgy.client.gui.PowerToolWearDecorator;
import com.onlytanner.industrialmetallurgy.client.gui.SolderingStationScreen;
import com.onlytanner.industrialmetallurgy.client.gui.ThermoelectricGeneratorScreen;
import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = IndustrialMetallurgy.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = IndustrialMetallurgy.MODID, value = Dist.CLIENT)
public class IndustrialMetallurgyClient {

    // Every item not yet migrated to Registrate still needs an item-model generator registered:
    // NeoForge's per-mod ModelProvider requires 100% item coverage across the whole
    // industrialmetallurgy namespace the instant Registrate is used for anything at all (the same
    // "all or nothing" completeness rule that governs block blockstates -- confirmed empirically,
    // ModelProvider$ItemInfoCollector#finalizeAndValidate fails the same way ModelProvider$
    // BlockStateGeneratorCollector#validate does for blocks). BlockItems are exempt (their model
    // resolves from their block automatically); every other item gets pointed at its existing
    // hand-authored models/item/<name>.json without migrating its registration. This MUST live
    // here, not in RegistryHandler (common code) -- the callback type (RegistrateItemModelGenerator,
    // inferred from ProviderType.ITEM_MODEL) is client-only, and unlike Registrate's own
    // .blockstate()/.loot() builder methods (which defer client-type resolution via an extra
    // Supplier layer specifically so common registration code can call them safely),
    // AbstractRegistrate#addDataGenerator takes the consumer directly -- referencing it from
    // common code crashes dedicated servers with NoClassDefFoundError as soon as the class loads.
    // Drop this once every item has been migrated to Registrate for real (see the item migration
    // tasks).
    static {
        IndustrialMetallurgy.REGISTRATE.addDataGenerator(ProviderType.ITEM_MODEL, gen -> RegistryHandler.ITEMS.getEntries().stream()
                .filter(entry -> !(entry.get() instanceof BlockItem))
                .forEach(entry -> gen.createWithExistingModel(entry.get(), gen.modLoc("item/" + entry.getId().getPath()))));
    }

    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModContainerTypes.CRUSHER.get(), CrusherScreen::new);
        event.register(ModContainerTypes.COKE_OVEN.get(), CokeOvenScreen::new);
        event.register(ModContainerTypes.THERMOELECTRIC_GENERATOR.get(), ThermoelectricGeneratorScreen::new);
        event.register(ModContainerTypes.FORGE_TIER1.get(), BasicForgeScreen::new);
        event.register(ModContainerTypes.FORGE_TIER2.get(), BasicForgeScreen::new);
        event.register(ModContainerTypes.FORGE_TIER3.get(), AdvancedForgeScreen::new);
        event.register(ModContainerTypes.FORGE_TIER4.get(), AdvancedForgeScreen::new);
        event.register(ModContainerTypes.ARC_FURNACE.get(), AdvancedForgeScreen::new);
        event.register(ModContainerTypes.EXTRUDER.get(), ExtruderScreen::new);
        event.register(ModContainerTypes.SOLDERING_STATION.get(), SolderingStationScreen::new);
        event.register(ModContainerTypes.CHEMICAL_CENTRIFUGE.get(), ChemicalCentrifugeScreen::new);
        event.register(ModContainerTypes.CHEMICAL_REACTOR.get(), ChemicalReactorScreen::new);
        event.register(ModContainerTypes.BATTERY_BOX.get(), BatteryBoxScreen::new);
        event.register(ModContainerTypes.ELECTRIC_FURNACE.get(), ElectricFurnaceScreen::new);
        event.register(ModContainerTypes.AUTOCLAVE.get(), AutoclaveScreen::new);
    }

    @SubscribeEvent
    static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        PowerToolWearDecorator decorator = new PowerToolWearDecorator();
        event.register(RegistryHandler.POWER_DRILL.get(), decorator);
        event.register(RegistryHandler.CHAINSAW.get(), decorator);
        event.register(RegistryHandler.CULTIVATOR.get(), decorator);
        event.register(RegistryHandler.PROSPECTOR.get(), decorator);
    }

}
