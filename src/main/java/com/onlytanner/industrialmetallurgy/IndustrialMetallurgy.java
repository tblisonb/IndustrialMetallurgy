package com.onlytanner.industrialmetallurgy;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.init.ModDataComponents;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.util.ModCapabilities;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
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

    // Mirrors NeoForge's own vanilla chest handling (CapabilityHooks#CHEST_COMBINER_HANDLER) but
    // built on the legacy IItemHandlerModifiable types our own ModCapabilities.ITEM_HANDLER carries,
    // so a Conduit/I-O Port sees one 54-slot inventory across a double chest instead of two 27-slot
    // ones depending on which half it happens to touch.
    private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, IItemHandlerModifiable> CHEST_COMBINER =
            new DoubleBlockCombiner.Combiner<>() {
                @Override
                public IItemHandlerModifiable acceptDouble(ChestBlockEntity chest1, ChestBlockEntity chest2) {
                    return new CombinedInvWrapper(new InvWrapper(chest1), new InvWrapper(chest2));
                }

                @Override
                public IItemHandlerModifiable acceptSingle(ChestBlockEntity chest) {
                    return new InvWrapper(chest);
                }

                @Override
                public IItemHandlerModifiable acceptNone() {
                    return null;
                }
            };

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Every machine's inventory still uses the deprecated ItemStackHandler/IItemHandler
        // internally rather than the new net.neoforged.neoforge.transfer resource API, so none of
        // it is exposed through NeoForge's own (ResourceHandler-based) Capabilities.Item.BLOCK --
        // migrating every machine's storage to that paradigm is a real, separate task. Instead,
        // ModCapabilities.ITEM_HANDLER is a capability of our own carrying the plain
        // IItemHandlerModifiable every machine already has, which is all the Conduit and I/O Port
        // need to move items around (see ModCapabilities and ConduitBlockEntity).
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

        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.CRUSHER.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.COKE_OVEN.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.THERMOELECTRIC_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.FORGE_TIER1.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.FORGE_TIER2.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.FORGE_TIER3.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.FORGE_TIER4.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.ARC_FURNACE.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.EXTRUDER.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.SOLDERING_STATION.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.CHEMICAL_CENTRIFUGE.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.CHEMICAL_REACTOR.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.BATTERY_BOX.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.ELECTRIC_FURNACE.get(),
                (blockEntity, side) -> blockEntity.getInventory());
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, ModTileEntityTypes.IO_PORT.get(),
                (blockEntity, side) -> blockEntity.getItemDelegate());

        // Vanilla storage: without these, an I/O Port or Conduit touching a plain chest/hopper/etc.
        // finds nothing, since ModCapabilities.ITEM_HANDLER is only ever registered against the
        // block-entity types listed above by default. Same vanilla type list NeoForge itself wires
        // up for its own Capabilities.Item.BLOCK (see CapabilityHooks#registerVanillaProviders),
        // just built on the legacy wrapper types that match what this mod's own machines expose.
        event.registerBlock(ModCapabilities.ITEM_HANDLER,
                (level, pos, state, blockEntity, side) -> ((ChestBlock) state.getBlock()).combine(state, level, pos, true).apply(CHEST_COMBINER),
                Blocks.CHEST, Blocks.TRAPPED_CHEST);

        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.FURNACE,
                (blockEntity, side) -> new SidedInvWrapper(blockEntity, side));
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.BLAST_FURNACE,
                (blockEntity, side) -> new SidedInvWrapper(blockEntity, side));
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.SMOKER,
                (blockEntity, side) -> new SidedInvWrapper(blockEntity, side));
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.BREWING_STAND,
                (blockEntity, side) -> new SidedInvWrapper(blockEntity, side));
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.SHULKER_BOX,
                (blockEntity, side) -> new SidedInvWrapper(blockEntity, side));

        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.BARREL,
                (blockEntity, side) -> new InvWrapper(blockEntity));
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.DISPENSER,
                (blockEntity, side) -> new InvWrapper(blockEntity));
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.DROPPER,
                (blockEntity, side) -> new InvWrapper(blockEntity));
        event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BlockEntityTypes.HOPPER,
                (blockEntity, side) -> new InvWrapper(blockEntity));
    }

}
