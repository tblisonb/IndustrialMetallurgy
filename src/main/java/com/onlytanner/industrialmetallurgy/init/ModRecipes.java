package com.onlytanner.industrialmetallurgy.init;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.recipes.AutoclaveRecipe;
import com.onlytanner.industrialmetallurgy.recipes.ChemicalCentrifugeRecipe;
import com.onlytanner.industrialmetallurgy.recipes.ChemicalReactorRecipe;
import com.onlytanner.industrialmetallurgy.recipes.CokeOvenRecipe;
import com.onlytanner.industrialmetallurgy.recipes.CrusherRecipe;
import com.onlytanner.industrialmetallurgy.recipes.ExtruderRecipe;
import com.onlytanner.industrialmetallurgy.recipes.ForgeRecipe;
import com.onlytanner.industrialmetallurgy.recipes.SolderingStationRecipe;
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
import net.neoforged.neoforge.common.crafting.SizedIngredient;
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

    public static final DeferredHolder<RecipeType<?>, RecipeType<ExtruderRecipe>> EXTRUDER_TYPE =
            RECIPE_TYPES.register("extruder", () -> new RecipeType<ExtruderRecipe>() {
                @Override
                public String toString() {
                    return IndustrialMetallurgy.MODID + ":extruder";
                }
            });

    private static final MapCodec<ExtruderRecipe> EXTRUDER_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.NESTED_CODEC.fieldOf("input").forGetter(ExtruderRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(ExtruderRecipe::output)
    ).apply(instance, ExtruderRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ExtruderRecipe> EXTRUDER_STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC, ExtruderRecipe::input,
            ItemStackTemplate.STREAM_CODEC, ExtruderRecipe::output,
            ExtruderRecipe::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtruderRecipe>> EXTRUDER_SERIALIZER =
            RECIPE_SERIALIZERS.register("extruder", () -> new RecipeSerializer<>(EXTRUDER_MAP_CODEC, EXTRUDER_STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<SolderingStationRecipe>> SOLDERING_STATION_TYPE =
            RECIPE_TYPES.register("soldering_station", () -> new RecipeType<SolderingStationRecipe>() {
                @Override
                public String toString() {
                    return IndustrialMetallurgy.MODID + ":soldering_station";
                }
            });

    private static final MapCodec<SolderingStationRecipe> SOLDERING_STATION_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("input").forGetter(SolderingStationRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(SolderingStationRecipe::output)
    ).apply(instance, SolderingStationRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, SolderingStationRecipe> SOLDERING_STATION_STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), SolderingStationRecipe::input,
            ItemStackTemplate.STREAM_CODEC, SolderingStationRecipe::output,
            SolderingStationRecipe::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SolderingStationRecipe>> SOLDERING_STATION_SERIALIZER =
            RECIPE_SERIALIZERS.register("soldering_station", () -> new RecipeSerializer<>(SOLDERING_STATION_MAP_CODEC, SOLDERING_STATION_STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<ChemicalCentrifugeRecipe>> CHEMICAL_CENTRIFUGE_TYPE =
            RECIPE_TYPES.register("chemical_centrifuge", () -> new RecipeType<ChemicalCentrifugeRecipe>() {
                @Override
                public String toString() {
                    return IndustrialMetallurgy.MODID + ":chemical_centrifuge";
                }
            });

    private static final MapCodec<ChemicalCentrifugeRecipe> CHEMICAL_CENTRIFUGE_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(ChemicalCentrifugeRecipe::input),
            SizedIngredient.NESTED_CODEC.optionalFieldOf("bottle").forGetter(ChemicalCentrifugeRecipe::bottle),
            ItemStackTemplate.CODEC.listOf().fieldOf("output").forGetter(ChemicalCentrifugeRecipe::output)
    ).apply(instance, ChemicalCentrifugeRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ChemicalCentrifugeRecipe> CHEMICAL_CENTRIFUGE_STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ChemicalCentrifugeRecipe::input,
            ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC), ChemicalCentrifugeRecipe::bottle,
            ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()), ChemicalCentrifugeRecipe::output,
            ChemicalCentrifugeRecipe::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChemicalCentrifugeRecipe>> CHEMICAL_CENTRIFUGE_SERIALIZER =
            RECIPE_SERIALIZERS.register("chemical_centrifuge", () -> new RecipeSerializer<>(CHEMICAL_CENTRIFUGE_MAP_CODEC, CHEMICAL_CENTRIFUGE_STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<ChemicalReactorRecipe>> CHEMICAL_REACTOR_TYPE =
            RECIPE_TYPES.register("chemical_reactor", () -> new RecipeType<ChemicalReactorRecipe>() {
                @Override
                public String toString() {
                    return IndustrialMetallurgy.MODID + ":chemical_reactor";
                }
            });

    private static final MapCodec<ChemicalReactorRecipe> CHEMICAL_REACTOR_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("input").forGetter(ChemicalReactorRecipe::input),
            ItemStackTemplate.CODEC.listOf().fieldOf("output").forGetter(ChemicalReactorRecipe::output)
    ).apply(instance, ChemicalReactorRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ChemicalReactorRecipe> CHEMICAL_REACTOR_STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ChemicalReactorRecipe::input,
            ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()), ChemicalReactorRecipe::output,
            ChemicalReactorRecipe::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChemicalReactorRecipe>> CHEMICAL_REACTOR_SERIALIZER =
            RECIPE_SERIALIZERS.register("chemical_reactor", () -> new RecipeSerializer<>(CHEMICAL_REACTOR_MAP_CODEC, CHEMICAL_REACTOR_STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<AutoclaveRecipe>> AUTOCLAVE_TYPE =
            RECIPE_TYPES.register("autoclave", () -> new RecipeType<AutoclaveRecipe>() {
                @Override
                public String toString() {
                    return IndustrialMetallurgy.MODID + ":autoclave";
                }
            });

    private static final MapCodec<AutoclaveRecipe> AUTOCLAVE_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("input").forGetter(AutoclaveRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(AutoclaveRecipe::output)
    ).apply(instance, AutoclaveRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AutoclaveRecipe> AUTOCLAVE_STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), AutoclaveRecipe::input,
            ItemStackTemplate.STREAM_CODEC, AutoclaveRecipe::output,
            AutoclaveRecipe::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AutoclaveRecipe>> AUTOCLAVE_SERIALIZER =
            RECIPE_SERIALIZERS.register("autoclave", () -> new RecipeSerializer<>(AUTOCLAVE_MAP_CODEC, AUTOCLAVE_STREAM_CODEC));

    public static void init(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

}
