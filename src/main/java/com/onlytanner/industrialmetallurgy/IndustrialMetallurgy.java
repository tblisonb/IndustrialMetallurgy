package com.onlytanner.industrialmetallurgy;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.init.ModDataComponents;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(IndustrialMetallurgy.MODID)
public class IndustrialMetallurgy {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "industrialmetallurgy";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("industrial_metallurgy", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.industrialmetallurgy"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RegistryHandler.TUNGSTEN_INGOT.get().getDefaultInstance())
            .displayItems((parameters, output) -> RegistryHandler.ITEMS.getEntries().forEach(entry -> output.accept(entry.get())))
            .build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public IndustrialMetallurgy(IEventBus modEventBus, ModContainer modContainer) {
        ModDataComponents.init(modEventBus);
        RegistryHandler.init(modEventBus);
        ModTileEntityTypes.init(modEventBus);
        ModContainerTypes.init(modEventBus);
        ModRecipes.init(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Every machine's inventory is a ModItemHandler (util.ModItemHandler), which is genuinely
        // backed by the new net.neoforged.neoforge.transfer resource-handler API -- so registering
        // it against NeoForge's own Capabilities.Item.BLOCK (rather than a capability of our own,
        // as an earlier pass here did) is all that's needed. That also means vanilla storage
        // (chest/barrel/hopper/furnace/etc.) needs no registration of ours at all: NeoForge already
        // wires all of that up against Capabilities.Item.BLOCK by default, so a Conduit/I-O Port
        // placed against a chest just works.
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.CRUSHER.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.THERMOELECTRIC_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.FORGE_TIER3.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.FORGE_TIER4.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.ARC_FURNACE.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.EXTRUDER.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.SOLDERING_STATION.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.CHEMICAL_CENTRIFUGE.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.CHEMICAL_REACTOR.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.BATTERY_BOX.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.ELECTRIC_FURNACE.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.SOLAR_PANEL.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.IO_PORT.get(),
                (blockEntity, side) -> blockEntity.getEnergyDelegate());

        // .asExternalHandler() (not the raw .getInventory()) for every machine: a Conduit/I-O
        // Port is external automation, and should only ever be able to pull from a designated
        // output slot and push into everything else -- never reach into a machine's own
        // in-progress input/raw-material slot the way the bare handler would let it. The player's
        // own GUI is unaffected, since Container/ResourceHandlerSlot talk to getInventory() directly.
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.CRUSHER.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.COKE_OVEN.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.THERMOELECTRIC_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.FORGE_TIER1.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.FORGE_TIER2.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.FORGE_TIER3.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.FORGE_TIER4.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.ARC_FURNACE.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.EXTRUDER.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.SOLDERING_STATION.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.CHEMICAL_CENTRIFUGE.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.CHEMICAL_REACTOR.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.BATTERY_BOX.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.ELECTRIC_FURNACE.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        // Was missing entirely -- the Autoclave's items have never been reachable by a Conduit,
        // I/O Port, or hopper at all, through no version of this method ever registering it.
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.AUTOCLAVE.get(),
                (blockEntity, side) -> blockEntity.getInventory().asExternalHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.IO_PORT.get(),
                (blockEntity, side) -> blockEntity.getItemDelegate());
    }

}
