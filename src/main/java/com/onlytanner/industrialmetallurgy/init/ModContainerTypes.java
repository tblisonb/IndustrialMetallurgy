package com.onlytanner.industrialmetallurgy.init;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.containers.CokeOvenContainer;
import com.onlytanner.industrialmetallurgy.containers.CrusherContainer;
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

    public static void init(IEventBus modEventBus) {
        CONTAINER_TYPES.register(modEventBus);
    }

}
