package com.onlytanner.industrialmetallurgy;

import com.onlytanner.industrialmetallurgy.client.gui.CokeOvenScreen;
import com.onlytanner.industrialmetallurgy.client.gui.CrusherScreen;
import com.onlytanner.industrialmetallurgy.client.gui.ThermoelectricGeneratorScreen;
import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = IndustrialMetallurgy.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = IndustrialMetallurgy.MODID, value = Dist.CLIENT)
public class IndustrialMetallurgyClient {

    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModContainerTypes.CRUSHER.get(), CrusherScreen::new);
        event.register(ModContainerTypes.COKE_OVEN.get(), CokeOvenScreen::new);
        event.register(ModContainerTypes.THERMOELECTRIC_GENERATOR.get(), ThermoelectricGeneratorScreen::new);
    }

}
