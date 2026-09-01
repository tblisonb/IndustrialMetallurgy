package com.onlytanner.industrialmetallurgy.init;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.containers.AdvancedForgeContainer;
import com.onlytanner.industrialmetallurgy.containers.BasicForgeContainer;
import com.onlytanner.industrialmetallurgy.containers.CokeOvenContainer;
import com.onlytanner.industrialmetallurgy.containers.CrusherContainer;
import com.onlytanner.industrialmetallurgy.containers.ChemicalCentrifugeContainer;
import com.onlytanner.industrialmetallurgy.containers.ChemicalReactorContainer;
import com.onlytanner.industrialmetallurgy.containers.ExtruderContainer;
import com.onlytanner.industrialmetallurgy.containers.SolderingStationContainer;
import com.onlytanner.industrialmetallurgy.containers.ThermoelectricGeneratorContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModContainerTypes {

    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(Registries.MENU, IndustrialMetallurgy.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CrusherContainer>> CRUSHER =
            CONTAINER_TYPES.register("crusher", () -> IMenuTypeExtension.create(CrusherContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CokeOvenContainer>> COKE_OVEN =
            CONTAINER_TYPES.register("coke_oven", () -> IMenuTypeExtension.create(CokeOvenContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ThermoelectricGeneratorContainer>> THERMOELECTRIC_GENERATOR =
            CONTAINER_TYPES.register("thermoelectric_generator", () -> IMenuTypeExtension.create(ThermoelectricGeneratorContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BasicForgeContainer>> FORGE_TIER1 =
            CONTAINER_TYPES.register("forge_tier1", () -> IMenuTypeExtension.create(BasicForgeContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BasicForgeContainer>> FORGE_TIER2 =
            CONTAINER_TYPES.register("forge_tier2", () -> IMenuTypeExtension.create(BasicForgeContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AdvancedForgeContainer>> FORGE_TIER3 =
            CONTAINER_TYPES.register("forge_tier3", () -> IMenuTypeExtension.create(AdvancedForgeContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AdvancedForgeContainer>> FORGE_TIER4 =
            CONTAINER_TYPES.register("forge_tier4", () -> IMenuTypeExtension.create(AdvancedForgeContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ExtruderContainer>> EXTRUDER =
            CONTAINER_TYPES.register("extruder", () -> IMenuTypeExtension.create(ExtruderContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SolderingStationContainer>> SOLDERING_STATION =
            CONTAINER_TYPES.register("soldering_station", () -> IMenuTypeExtension.create(SolderingStationContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ChemicalCentrifugeContainer>> CHEMICAL_CENTRIFUGE =
            CONTAINER_TYPES.register("chemical_centrifuge", () -> IMenuTypeExtension.create(ChemicalCentrifugeContainer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ChemicalReactorContainer>> CHEMICAL_REACTOR =
            CONTAINER_TYPES.register("chemical_reactor", () -> IMenuTypeExtension.create(ChemicalReactorContainer::new));

    public static void init(IEventBus modEventBus) {
        CONTAINER_TYPES.register(modEventBus);
    }

}
