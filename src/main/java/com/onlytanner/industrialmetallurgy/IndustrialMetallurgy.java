package com.onlytanner.industrialmetallurgy;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
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
        RegistryHandler.init(modEventBus);
        ModTileEntityTypes.init(modEventBus);
        ModContainerTypes.init(modEventBus);
        ModRecipes.init(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }

}
