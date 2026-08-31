package com.onlytanner.industrialmetallurgy.init;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.recipes.CokeOvenRecipe;
import com.onlytanner.industrialmetallurgy.recipes.CrusherRecipe;
import com.onlytanner.industrialmetallurgy.recipes.ForgeRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
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

    public static final DeferredHolder<RecipeType<?>, RecipeType<CokeOvenRecipe>> COKE_OVEN_TYPE =
            RECIPE_TYPES.register("coke_oven", () -> new RecipeType<CokeOvenRecipe>() {
                @Override
                public String toString() {
                    return IndustrialMetallurgy.MODID + ":coke_oven";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CokeOvenRecipe>> COKE_OVEN_SERIALIZER =
            RECIPE_SERIALIZERS.register("coke_oven", () -> new RecipeSerializer<>(
                    SingleItemRecipe.simpleMapCodec(CokeOvenRecipe::new),
                    SingleItemRecipe.simpleStreamCodec(CokeOvenRecipe::new)));

    public static final DeferredHolder<RecipeType<?>, RecipeType<ForgeRecipe>> FORGE_TYPE =
            RECIPE_TYPES.register("forge", () -> new RecipeType<ForgeRecipe>() {
                @Override
                public String toString() {
                    return IndustrialMetallurgy.MODID + ":forge";
                }
            });

    private static final MapCodec<ForgeRecipe> FORGE_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("input").forGetter(ForgeRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(ForgeRecipe::output),
            com.mojang.serialization.Codec.STRING.fieldOf("tier").forGetter(ForgeRecipe::tier)
    ).apply(instance, ForgeRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ForgeRecipe> FORGE_STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ForgeRecipe::input,
            ItemStackTemplate.STREAM_CODEC, ForgeRecipe::output,
            ByteBufCodecs.STRING_UTF8, ForgeRecipe::tier,
            ForgeRecipe::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgeRecipe>> FORGE_SERIALIZER =
            RECIPE_SERIALIZERS.register("forge", () -> new RecipeSerializer<>(FORGE_MAP_CODEC, FORGE_STREAM_CODEC));

    public static void init(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

}
