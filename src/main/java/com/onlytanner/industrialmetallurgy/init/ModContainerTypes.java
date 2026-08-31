package com.onlytanner.industrialmetallurgy.init;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.containers.CrusherContainer;
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

    public static void init(IEventBus modEventBus) {
        CONTAINER_TYPES.register(modEventBus);
    }

}
