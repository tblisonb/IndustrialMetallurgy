package com.onlytanner.industrialmetallurgy.compat.jer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import jeresources.api.IJERPlugin;
import jeresources.api.JERPlugin;
import jeresources.api.distributions.DistributionTriangular;
import jeresources.api.drop.LootDrop;
import jeresources.api.IJERAPI;
import jeresources.api.IWorldGenRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Discovered and instantiated by JER's own classloader scan for {@code @JERPlugin}-annotated
 * classes (see {@code NeoForgePlatformHelper#injectApi} in JER's jar) -- nothing in this mod ever
 * references this class directly, so it's a true soft dependency: if JER isn't present, this
 * class is simply never loaded (and, per build.gradle, isn't even compiled).
 *
 * Reads each ore's real min/max height and rarity straight out of its own
 * {@code data/industrialmetallurgy/worldgen/{configured_feature,placed_feature}} JSON rather than
 * hand-duplicating those numbers here -- by the time JER calls {@link #receive}, the dynamic
 * registries those files eventually feed (PlacedFeature, etc.) aren't populated yet, so this reads
 * the raw JSON directly off the classpath instead of going through the registry at all.
 */
@JERPlugin
public class IndustrialMetallurgyJerPlugin implements IJERPlugin {

    // Every ore below has a matching *_ore.json / *_ore_placed.json pair. oil_sand isn't included
    // -- it's a sand-replacement feature, not an ore-tier block, and doesn't fit the block/raw-drop
    // shape the rest of this table assumes.
    private static final Map<String, Supplier<Block>> ORE_BLOCKS = new LinkedHashMap<>();
    private static final Map<String, Supplier<Item>> ORE_DROPS = new LinkedHashMap<>();

    static {
        ORE_BLOCKS.put("argentite", RegistryHandler.ARGENTITE_ORE::get);
        ORE_BLOCKS.put("bauxite", RegistryHandler.BAUXITE_ORE::get);
        ORE_BLOCKS.put("cassiterite", RegistryHandler.CASSITERITE_ORE::get);
        ORE_BLOCKS.put("chromite", RegistryHandler.CHROMITE_ORE::get);
        ORE_BLOCKS.put("cobaltite", RegistryHandler.COBALTITE_ORE::get);
        ORE_BLOCKS.put("galena", RegistryHandler.GALENA_ORE::get);
        ORE_BLOCKS.put("garnierite", RegistryHandler.GARNIERITE_ORE::get);
        ORE_BLOCKS.put("lepidolite", RegistryHandler.LEPIDOLITE_ORE::get);
        ORE_BLOCKS.put("pyrolusite", RegistryHandler.PYROLUSITE_ORE::get);
        ORE_BLOCKS.put("rutile", RegistryHandler.RUTILE_ORE::get);
        ORE_BLOCKS.put("scheelite", RegistryHandler.SCHEELITE_ORE::get);
        ORE_BLOCKS.put("sphalerite", RegistryHandler.SPHALERITE_ORE::get);
        ORE_BLOCKS.put("rheniite", RegistryHandler.RHENIITE_ORE::get);

        ORE_DROPS.put("argentite", RegistryHandler.RAW_ARGENTITE_ORE::get);
        ORE_DROPS.put("bauxite", RegistryHandler.RAW_BAUXITE_ORE::get);
        ORE_DROPS.put("cassiterite", RegistryHandler.RAW_CASSITERITE_ORE::get);
        ORE_DROPS.put("chromite", RegistryHandler.RAW_CHROMITE_ORE::get);
        ORE_DROPS.put("cobaltite", RegistryHandler.RAW_COBALTITE_ORE::get);
        ORE_DROPS.put("galena", RegistryHandler.RAW_GALENA_ORE::get);
        ORE_DROPS.put("garnierite", RegistryHandler.RAW_GARNIERITE_ORE::get);
        // Lepidolite has no separate raw-ore intermediate (see ProspectorItem) -- the ore block
        // drops the lepidolite item itself.
        ORE_DROPS.put("lepidolite", RegistryHandler.LEPIDOLITE::get);
        ORE_DROPS.put("pyrolusite", RegistryHandler.RAW_PYROLUSITE_ORE::get);
        ORE_DROPS.put("rutile", RegistryHandler.RAW_RUTILE_ORE::get);
        ORE_DROPS.put("scheelite", RegistryHandler.RAW_SCHEELITE_ORE::get);
        ORE_DROPS.put("sphalerite", RegistryHandler.RAW_SPHALERITE_ORE::get);
        ORE_DROPS.put("rheniite", RegistryHandler.RAW_RHENIITE_ORE::get);
    }

    private record OreGenInfo(int minY, int maxY, int count, int size) {
    }

    @Override
    public void receive(IJERAPI api) {
        IWorldGenRegistry worldGen = api.getWorldGenRegistry();
        for (String ore : ORE_BLOCKS.keySet()) {
            try {
                registerOre(worldGen, ore);
            } catch (Exception e) {
                IndustrialMetallurgy.LOGGER.warn("Failed to register {} ore with JER", ore, e);
            }
        }
    }

    private void registerOre(IWorldGenRegistry worldGen, String ore) {
        OreGenInfo info = readOreGenInfo(ore);
        if (info == null) {
            return;
        }
        Block block = ORE_BLOCKS.get(ore).get();
        Item drop = ORE_DROPS.get(ore).get();
        // A rough per-block density estimate (average blocks placed, divided by the chunk column
        // volume the ore can spawn across) -- JER only uses this to rank ores against each other
        // on the world-gen tab, not as an exact real-world probability.
        int heightSpan = info.maxY() - info.minY() + 1;
        float chance = (info.count() * info.size()) / (16f * 16f * heightSpan);
        worldGen.register(
                new ItemStack(block),
                new DistributionTriangular(info.minY(), info.maxY(), chance),
                new LootDrop(new ItemStack(drop)));
    }

    private static OreGenInfo readOreGenInfo(String ore) {
        JsonObject configured = readJson("data/industrialmetallurgy/worldgen/configured_feature/" + ore + "_ore.json");
        JsonObject placed = readJson("data/industrialmetallurgy/worldgen/placed_feature/" + ore + "_ore_placed.json");
        if (configured == null || placed == null) {
            return null;
        }
        int size = configured.getAsJsonObject("config").get("size").getAsInt();
        int count = 0;
        int minY = 0;
        int maxY = 0;
        for (var element : placed.getAsJsonArray("placement")) {
            JsonObject entry = element.getAsJsonObject();
            switch (entry.get("type").getAsString()) {
                case "minecraft:count" -> count = entry.get("count").getAsInt();
                case "minecraft:height_range" -> {
                    JsonObject height = entry.getAsJsonObject("height");
                    minY = height.getAsJsonObject("min_inclusive").get("absolute").getAsInt();
                    maxY = height.getAsJsonObject("max_inclusive").get("absolute").getAsInt();
                }
                default -> {
                }
            }
        }
        return new OreGenInfo(minY, maxY, count, size);
    }

    private static JsonObject readJson(String path) {
        try (InputStream stream = IndustrialMetallurgyJerPlugin.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            IndustrialMetallurgy.LOGGER.warn("Failed to read {} for JER registration", path, e);
            return null;
        }
    }

}
