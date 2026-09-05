package com.onlytanner.industrialmetallurgy.datatest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Blackbox consistency checks between what RegistryHandler/Registrate register and the JSON under
 * src/main/resources (hand-authored) and src/generated/resources (Registrate/runData output,
 * checked into git per CLAUDE.md's documented convention) -- no Minecraft client/server instance
 * needed, since DeferredRegister/Registrate entries are populated by plain static class-init, not
 * by an FML lifecycle event. Run via `./gradlew test`.
 *
 * These exist because real bugs slipped through by hand: a deepslate ore item shipped with a
 * models/item/*.json but no assets/industrialmetallurgy/items/*.json (the item-model-definition
 * every registered item needs), and newer materials had been appended to the end of their
 * RegistryHandler category instead of inserted alphabetically. The asset-existence checks here
 * would have caught the first at build time; a `runData`-based provider is the real fix for the
 * second (now adopted for blocks -- see the registrate-migration branch), since this suite
 * intentionally doesn't police *ordering*.
 *
 * Every block/item registers through Registrate (RegistryHandler no longer holds any
 * DeferredRegister entries of its own); blockNames()/itemNames() read straight from
 * IndustrialMetallurgy.REGISTRATE.
 */
class RegistryAssetConsistencyTest {

    private static final String MOD_ID = "industrialmetallurgy";
    // The FML-managed unit test environment's working directory isn't the project root (see
    // build.gradle's `test { systemProperty ... }`), so this can't just be a relative path.
    private static final Path PROJECT_DIR = Path.of(System.getProperty("industrialmetallurgy.projectDir", "."));
    private static final Path MAIN_RESOURCES = PROJECT_DIR.resolve("src/main/resources");
    private static final Path GENERATED_RESOURCES = PROJECT_DIR.resolve("src/generated/resources");
    private static final Path MAIN_ASSETS = MAIN_RESOURCES.resolve("assets/" + MOD_ID);
    private static final Path MAIN_DATA = MAIN_RESOURCES.resolve("data/" + MOD_ID);
    private static final Path VANILLA_TAGS = MAIN_RESOURCES.resolve("data/minecraft/tags");

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        // BuiltInRegistries' own <clinit> asserts this has already run (it registers "defaulted"
        // registries and checks a bootstrap flag) -- without it, simply loading RegistryHandler
        // (which references Blocks.FURNACE, SoundType, etc.) throws before a single test runs.
        Bootstrap.bootStrap();
    }

    private static List<String> blockNames() {
        return IndustrialMetallurgy.REGISTRATE.getAll(Registries.BLOCK).stream()
                .map(entry -> entry.getId().getPath())
                .sorted()
                .toList();
    }

    private static List<String> itemNames() {
        return IndustrialMetallurgy.REGISTRATE.getAll(Registries.ITEM).stream()
                .map(entry -> entry.getId().getPath())
                .sorted()
                .toList();
    }

    // A block's blockstate/model/loot may now come from either the hand-authored tree or
    // Registrate's checked-in runData output -- real content lives in exactly one of the two.
    private static boolean existsUnderAssets(String relativePath) {
        return Files.exists(MAIN_ASSETS.resolve(relativePath)) || Files.exists(GENERATED_RESOURCES.resolve("assets/" + MOD_ID).resolve(relativePath));
    }

    private static boolean existsUnderData(String relativePath) {
        return Files.exists(MAIN_DATA.resolve(relativePath)) || Files.exists(GENERATED_RESOURCES.resolve("data/" + MOD_ID).resolve(relativePath));
    }

    private static Path resolveUnderAssets(String relativePath) {
        Path generated = GENERATED_RESOURCES.resolve("assets/" + MOD_ID).resolve(relativePath);
        return Files.exists(generated) ? generated : MAIN_ASSETS.resolve(relativePath);
    }

    @Test
    void everyBlockHasABlockstateFile() {
        List<String> missing = blockNames().stream()
                .filter(name -> !existsUnderAssets("blockstates/" + name + ".json"))
                .toList();
        assertTrue(missing.isEmpty(), () -> "Blocks missing a blockstates/<name>.json (hand-authored or generated): " + missing);
    }

    @Test
    void everyBlockHasALootTable() {
        List<String> missing = blockNames().stream()
                .filter(name -> !existsUnderData("loot_table/blocks/" + name + ".json"))
                .toList();
        assertTrue(missing.isEmpty(), () -> "Blocks missing a loot_table/blocks/<name>.json (hand-authored or generated): " + missing);
    }

    @Test
    void everyBlockHasALangEntry() throws IOException {
        Set<String> keys = readLangKeys();
        List<String> missing = blockNames().stream()
                .filter(name -> !keys.contains("block." + MOD_ID + "." + name))
                .toList();
        assertTrue(missing.isEmpty(), () -> "Blocks missing a block." + MOD_ID + ".<name> lang entry: " + missing);
    }

    @Test
    void everyItemHasAnItemModelDefinition() {
        List<String> missing = itemNames().stream()
                .filter(name -> !existsUnderAssets("items/" + name + ".json"))
                .toList();
        assertTrue(missing.isEmpty(), () -> "Items missing an assets/.../items/<name>.json (item model definition, hand-authored or generated): " + missing);
    }

    @Test
    void everyItemHasALangEntry() throws IOException {
        Set<String> keys = readLangKeys();
        // A BlockItem's default translation key delegates to its block's, so "block.<mod>.<name>"
        // (same registry name as the block, this repo's universal convention) counts too.
        List<String> missing = itemNames().stream()
                .filter(name -> !keys.contains("item." + MOD_ID + "." + name) && !keys.contains("block." + MOD_ID + "." + name))
                .toList();
        assertTrue(missing.isEmpty(), () -> "Items missing an item." + MOD_ID + ".<name> (or block." + MOD_ID + ".<name>) lang entry: " + missing);
    }

    @Test
    void everyRegistrateItemHasExactlyOneCreativeTabEntry() {
        // RegistryHandler.REGISTRATE_ITEMS_IN_ORDER exists solely to keep migrated blocks'/plain
        // items' entries in the creative tab/JEI at the same relative position they held before
        // migrating off ITEMS/BLOCKS (see its own comment) -- one entry per item registered
        // through Registrate (a block's BlockItem counts as one such item, same as a directly-
        // registered plain item), no more, no less. A wrong count here means a trackBlockItem()/
        // trackItem() call was skipped, duplicated, or the field was read before being populated
        // (all real bugs caught during this migration).
        long registrateItemCount = IndustrialMetallurgy.REGISTRATE.getAll(Registries.ITEM).size();
        assertTrue(registrateItemCount > 0, "No items registered through Registrate -- did RegistryHandler fail to load?");
        assertTrue(RegistryHandler.REGISTRATE_ITEMS_IN_ORDER.size() == registrateItemCount,
                () -> "REGISTRATE_ITEMS_IN_ORDER has " + RegistryHandler.REGISTRATE_ITEMS_IN_ORDER.size()
                        + " entries but " + registrateItemCount + " items are registered through Registrate");
    }

    @Test
    void everyBlockstateModelReferenceExists() throws IOException {
        List<String> problems = new ArrayList<>();
        for (String name : blockNames()) {
            Path file = resolveUnderAssets("blockstates/" + name + ".json");
            if (Files.notExists(file)) {
                continue; // reported by everyBlockHasABlockstateFile
            }
            for (String model : collectStringsUnderKey(parse(file), "model")) {
                if (!model.startsWith(MOD_ID + ":")) {
                    continue; // vanilla parent (e.g. "block/cube_all"), not ours to validate
                }
                if (!existsUnderAssets("models/" + model.substring(MOD_ID.length() + 1) + ".json")) {
                    problems.add(name + " -> " + model);
                }
            }
        }
        assertTrue(problems.isEmpty(), () -> "Blockstate model references that don't resolve to a file: " + problems);
    }

    @Test
    void everyRecipeItemReferenceIsARegisteredItem() throws IOException {
        Set<String> registeredItems = itemNames().stream().map(name -> MOD_ID + ":" + name).collect(Collectors.toSet());
        List<String> problems = new ArrayList<>();
        Path recipeDir = MAIN_DATA.resolve("recipe");
        if (Files.exists(recipeDir)) {
            try (Stream<Path> files = Files.walk(recipeDir)) {
                for (Path file : jsonFiles(files)) {
                    for (String item : collectStringsUnderKey(parse(file), "item")) {
                        if (item.startsWith(MOD_ID + ":") && !registeredItems.contains(item)) {
                            problems.add(MAIN_RESOURCES.relativize(file) + " references unregistered item " + item);
                        }
                    }
                }
            }
        }
        assertTrue(problems.isEmpty(), () -> "Recipes referencing unregistered items: " + problems);
    }

    @Test
    void everyModTagBlockValueIsARegisteredBlock() throws IOException {
        Set<String> ids = blockNames().stream().map(name -> MOD_ID + ":" + name).collect(Collectors.toSet());
        List<String> problems = new ArrayList<>();
        checkTagValues(MAIN_DATA.resolve("tags/block"), ids, problems);
        checkTagValues(VANILLA_TAGS.resolve("block"), ids, problems);
        assertTrue(problems.isEmpty(), () -> "Block tags referencing unregistered blocks: " + problems);
    }

    @Test
    void everyModTagItemValueIsARegisteredItem() throws IOException {
        Set<String> ids = itemNames().stream().map(name -> MOD_ID + ":" + name).collect(Collectors.toSet());
        List<String> problems = new ArrayList<>();
        checkTagValues(MAIN_DATA.resolve("tags/item"), ids, problems);
        checkTagValues(VANILLA_TAGS.resolve("item"), ids, problems);
        assertTrue(problems.isEmpty(), () -> "Item tags referencing unregistered items: " + problems);
    }

    // --- helpers -------------------------------------------------------------------------

    private static void checkTagValues(Path tagsDir, Set<String> registeredIds, List<String> problems) throws IOException {
        if (Files.notExists(tagsDir)) {
            return;
        }
        try (Stream<Path> files = Files.walk(tagsDir)) {
            for (Path file : jsonFiles(files)) {
                JsonObject root = parse(file).getAsJsonObject();
                if (!root.has("values")) {
                    continue;
                }
                for (JsonElement value : root.getAsJsonArray("values")) {
                    String id = value.isJsonObject() ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();
                    if (id.startsWith("#") || !id.startsWith(MOD_ID + ":") || registeredIds.contains(id)) {
                        continue;
                    }
                    problems.add(MAIN_RESOURCES.relativize(file) + " references unregistered " + id);
                }
            }
        }
    }

    private static List<Path> jsonFiles(Stream<Path> files) {
        return files.filter(p -> p.toString().endsWith(".json")).toList();
    }

    private static JsonElement parse(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader);
        }
    }

    /** Recursively collects every string value found under any object member named {@code key}. */
    private static List<String> collectStringsUnderKey(JsonElement element, String key) {
        List<String> out = new ArrayList<>();
        collectStringsUnderKey(element, key, out);
        return out;
    }

    private static void collectStringsUnderKey(JsonElement element, String key, List<String> out) {
        if (element.isJsonObject()) {
            for (var entry : element.getAsJsonObject().entrySet()) {
                JsonElement value = entry.getValue();
                if (entry.getKey().equals(key) && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                    out.add(value.getAsString());
                }
                collectStringsUnderKey(value, key, out);
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collectStringsUnderKey(child, key, out);
            }
        }
    }

    private static Set<String> readLangKeys() throws IOException {
        Path langFile = MAIN_ASSETS.resolve("lang/en_us.json");
        try (Reader reader = Files.newBufferedReader(langFile, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            return new HashSet<>(root.keySet());
        }
    }

}
