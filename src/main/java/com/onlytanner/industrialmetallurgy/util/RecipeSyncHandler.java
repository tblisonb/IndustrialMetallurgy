package com.onlytanner.industrialmetallurgy.util;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

// Custom recipe types aren't sent to the client at all unless a mod explicitly asks for them here
// -- vanilla's own recipe sync only carries RecipePropertySets now (for search/ghost-slot use),
// not full recipe objects, and NeoForge's OnDatapackSyncEvent#sendRecipes is the opt-in that
// restores that for a given RecipeType. Without this, every custom recipe (Crusher, Coke Oven,
// Forge, Extruder, Soldering Station, Chemical Centrifuge, Chemical Reactor) is fully functional
// in-game but invisible to JEI and to any future in-mod recipe book.
@EventBusSubscriber(modid = IndustrialMetallurgy.MODID)
public class RecipeSyncHandler {

    @SubscribeEvent
    static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(
                ModRecipes.CRUSHER_TYPE.get(),
                ModRecipes.COKE_OVEN_TYPE.get(),
                ModRecipes.FORGE_TYPE.get(),
                ModRecipes.EXTRUDER_TYPE.get(),
                ModRecipes.SOLDERING_STATION_TYPE.get(),
                ModRecipes.CHEMICAL_CENTRIFUGE_TYPE.get(),
                ModRecipes.CHEMICAL_REACTOR_TYPE.get()
        );
    }

}
