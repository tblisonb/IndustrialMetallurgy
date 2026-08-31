package com.onlytanner.industrialmetallurgy.init;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.recipes.CrusherRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, IndustrialMetallurgy.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, IndustrialMetallurgy.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrusherRecipe>> CRUSHER_TYPE =
            RECIPE_TYPES.register("crusher", () -> new RecipeType<CrusherRecipe>() {
                @Override
                public String toString() {
                    return IndustrialMetallurgy.MODID + ":crusher";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrusherRecipe>> CRUSHER_SERIALIZER =
            RECIPE_SERIALIZERS.register("crusher", () -> new RecipeSerializer<>(
                    SingleItemRecipe.simpleMapCodec(CrusherRecipe::new),
                    SingleItemRecipe.simpleStreamCodec(CrusherRecipe::new)));

    public static void init(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

}
