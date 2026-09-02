package com.onlytanner.industrialmetallurgy.client.jei;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.init.ModRecipes;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.common.Internal;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeMap;

import java.util.ArrayList;

// Registers every custom machine recipe type (Crusher, Coke Oven, Forge/Arc Furnace, Extruder,
// Soldering Station, Chemical Centrifuge, Chemical Reactor) as a JEI category, so every recipe
// added under src/main/resources/data/industrialmetallurgy/recipe/ is browsable and spot-checkable
// in-game -- the Electric Furnace (plain vanilla smelting) and Thermoelectric Generator (vanilla
// fuel values) don't get their own category since they already show up under JEI's built-in ones;
// they're just added as extra catalysts for those instead.
@JeiPlugin
public class IndustrialMetallurgyJeiPlugin implements IModPlugin {

    private static final Identifier UID = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new CrusherRecipeCategory(guiHelper),
                new CokeOvenRecipeCategory(guiHelper),
                new ForgeRecipeCategory(guiHelper),
                new ExtruderRecipeCategory(guiHelper),
                new SolderingStationRecipeCategory(guiHelper),
                new ChemicalCentrifugeRecipeCategory(guiHelper),
                new ChemicalReactorRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeMap recipeMap = Internal.getClientSyncedRecipes();
        registration.addRecipes(CrusherRecipeCategory.TYPE, new ArrayList<>(recipeMap.byType(ModRecipes.CRUSHER_TYPE.get())));
        registration.addRecipes(CokeOvenRecipeCategory.TYPE, new ArrayList<>(recipeMap.byType(ModRecipes.COKE_OVEN_TYPE.get())));
        registration.addRecipes(ForgeRecipeCategory.TYPE, new ArrayList<>(recipeMap.byType(ModRecipes.FORGE_TYPE.get())));
        registration.addRecipes(ExtruderRecipeCategory.TYPE, new ArrayList<>(recipeMap.byType(ModRecipes.EXTRUDER_TYPE.get())));
        registration.addRecipes(SolderingStationRecipeCategory.TYPE, new ArrayList<>(recipeMap.byType(ModRecipes.SOLDERING_STATION_TYPE.get())));
        registration.addRecipes(ChemicalCentrifugeRecipeCategory.TYPE, new ArrayList<>(recipeMap.byType(ModRecipes.CHEMICAL_CENTRIFUGE_TYPE.get())));
        registration.addRecipes(ChemicalReactorRecipeCategory.TYPE, new ArrayList<>(recipeMap.byType(ModRecipes.CHEMICAL_REACTOR_TYPE.get())));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(CrusherRecipeCategory.TYPE, RegistryHandler.CRUSHER.get());
        registration.addCraftingStation(CokeOvenRecipeCategory.TYPE, RegistryHandler.COKE_OVEN.get());
        registration.addCraftingStation(ForgeRecipeCategory.TYPE,
                RegistryHandler.FORGE_TIER1.get(), RegistryHandler.FORGE_TIER2.get(), RegistryHandler.FORGE_TIER3.get(),
                RegistryHandler.FORGE_TIER4.get(), RegistryHandler.ARC_FURNACE.get());
        registration.addCraftingStation(ExtruderRecipeCategory.TYPE, RegistryHandler.EXTRUDER.get());
        registration.addCraftingStation(SolderingStationRecipeCategory.TYPE, RegistryHandler.SOLDERING_STATION.get());
        registration.addCraftingStation(ChemicalCentrifugeRecipeCategory.TYPE, RegistryHandler.CHEMICAL_CENTRIFUGE.get());
        registration.addCraftingStation(ChemicalReactorRecipeCategory.TYPE, RegistryHandler.CHEMICAL_REACTOR.get());

        // Not custom recipe types -- Electric Furnace runs plain vanilla smelting, and the
        // Thermoelectric Generator burns vanilla fuel values (plus coal_coke, special-cased in
        // ThermoelectricGeneratorBlockEntity rather than data-driven) -- point at JEI's own
        // built-in categories for those instead of duplicating them.
        registration.addCraftingStation(RecipeTypes.SMELTING, RegistryHandler.ELECTRIC_FURNACE.get());
        registration.addCraftingStation(RecipeTypes.SMELTING_FUEL, RegistryHandler.THERMOELECTRIC_GENERATOR.get());
    }

}
