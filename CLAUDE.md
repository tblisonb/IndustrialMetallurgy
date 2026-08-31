# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Industrial Metallurgy is a Minecraft Forge mod (Minecraft 1.16.4, Forge `35.0.14`, ForgeGradle 3, Java 8 source/target) that adds ore/metal processing chains: new ores, ingots, alloys, and multiblock-free processing machines (crusher, coke oven, forges, thermoelectric generator). Mod ID: `industrialmetallurgy`. Base package: `com.onlytanner.industrialmetallurgy`.

## Common commands

Use the Gradle wrapper (`./gradlew` on Linux/Mac, `gradlew.bat` on Windows) — do not rely on a system Gradle install, ForgeGradle pins its own version.

- Build the mod jar: `./gradlew build`
- Compile only: `./gradlew compileJava`
- Run the client (dev environment): `./gradlew runClient`
- Run a dedicated server (dev environment): `./gradlew runServer`
- Regenerate data-driven assets (recipes/etc. under `src/generated/resources`): `./gradlew runData` — this invokes the `data` run config, which passes `--mod industrialmetallurgy --all --output src/generated/resources/ --existing src/main/resources/`
- Clean build outputs: `./gradlew clean`
- Regenerate IDE run configs after editing `build.gradle`: `./gradlew genEclipseRuns` or `./gradlew genIntellijRuns`

There are no unit tests in this repo (no test source set) and no linter config — correctness is verified by compiling and by running the client/server in the dev environment.

`gradle.properties` pins `mcversion=1.16.4` and `jei_version=7.6.0.57` — JEI (Just Enough Items) is a compile-time/runtime dependency for recipe display.

## Architecture

This follows the standard Forge 1.16 registration and machine pattern. Everything is wired up from `IndustrialMetallurgy.java` (the `@Mod` entry point), which on construction calls, in order: `ConfigHandler` config registration, `RegistryHandler.init()`, `RecipeSerializerInit.init()`, `ModContainerTypes.init()`, `ModTileEntityTypes.init()`. Client-only screen factories are bound in `setupClient` (`FMLClientSetupEvent`), and world generation is kicked off in `setup` (`FMLCommonSetupEvent`) via `OreFeatureHandler.initModFeatures()` and `OreGenHandler.generateOres()`.

### Registration (`util/RegistryHandler.java`)

All `Item`s and `Block`s are declared as `public static final RegistryObject<...>` fields on two `DeferredRegister`s (`ITEMS`, `BLOCKS`), grouped by category with comments (metal ingots, nuggets, crushed ores, misc resources, tools, metal blocks, ore blocks, machines, block items). Block items are registered as a second pass at the bottom of the file via `BlockItemBase`, referencing the already-declared block `RegistryObject`s. When adding a new material/item, follow the existing grouping and naming convention (`snake_case` registry name matching the Java constant name in `UPPER_SNAKE_CASE`) rather than introducing a new pattern.

Other registries follow the same `DeferredRegister` + `init()` pattern in their own class:
- `init/ModTileEntityTypes.java` — tile entity types, one per machine
- `init/ModContainerTypes.java` — container (menu) types, one per machine
- `recipes/RecipeSerializerInit.java` — custom recipe types/serializers, one per machine

### The machine pattern (crusher, coke oven, forge tiers, thermoelectric generator)

Each machine block is implemented as a consistent stack of 5 classes; use the crusher as the reference implementation when adding a new machine:

1. **Block** (`blocks/CrusherBlock.java`) — extends a furnace-like `Block`, holds a `LIT` blockstate property, opens the container on right-click.
2. **TileEntity** (`tileentity/CrusherTileEntity.java`) — implements `ITickableTileEntity`, `INamedContainerProvider`, and `IEnergyStorage` (Forge Energy/FE capability). Owns a `ModItemHandler` (fixed-slot item handler, see `util/ModItemHandler.java`) for its inventory slots, holds process state (`currentSmeltTime`, `energy`, machine-specific extras like `acidLevel`), and does recipe lookup + progress + NBT read/write. `tick()` only runs server-side (`!world.isRemote`) and toggles the `LIT` state; recipe matching queries `world.getRecipeManager()` for the machine's custom `IRecipeType` rather than caching recipes at construction time (recipes can reload via datapacks). Capabilities are exposed via `getCapability` for both `CapabilityEnergy.ENERGY` and `CapabilityItemHandler.ITEM_HANDLER_CAPABILITY`.
3. **Container** (`containers/CrusherContainer.java`) — standard `Container` wiring slots to the tile entity's item handler and player inventory, syncing extra fields (energy, progress) via `IIntArray`/`FunctionalIntReferenceHolder` (see `util/`).
4. **Screen** (`client/gui/CrusherScreen.java`, client-only) — renders the GUI texture from `textures/gui/`, registered in `IndustrialMetallurgy.setupClient` via `ScreenManager.registerFactory`.
5. **Recipe** (`recipes/CrusherRecipe.java` + `CrusherRecipeBase` interface + `CrusherRecipeSerializer`) — implements `IRecipe<RecipeWrapper>`, data-driven from JSON under `src/main/resources/data/industrialmetallurgy/recipes/<machine>/`. Recipe JSON shape is minimal: `{"type": "industrialmetallurgy:<machine>", "input": {"item": "..."}, "output": {"item": "..."}}`. Adding a new recipe is just adding a JSON file — no Java changes needed.

Machines that consume/produce power do so through the vanilla Forge Energy capability (`IEnergyStorage`), not a custom energy system.

### World generation

`world/gen/feature/OreFeatureHandler.java` registers a custom ore-generation `Feature` per ore, and `world/gen/OreGenHandler.java` adds those features to biomes at startup (`FMLCommonSetupEvent`). Per-ore tunables (vein size, chance/chunk, min/max height, and an on/off toggle) live in `config/OreGenConfig.java` and are surfaced through `util/ConfigHandler.java` as a Forge common config (`industrialmetallurgy-common.toml`). New ores need matching entries in `OreGenConfig`, wiring in `OreFeatureHandler`/`OreGenHandler`, plus the standard block/item registration.

Ore tiers (`Tier1OreBlock`..`Tier4OreBlock`) encode mining-level gating (harvest tool tier); pick the tier class matching the ore's intended progression stage rather than parameterizing a single ore block class.

### Assets and data

Standard Forge resource layout — when adding a new block/item you generally need matching entries in all of:
- `src/main/resources/assets/industrialmetallurgy/blockstates/*.json` (blocks only)
- `src/main/resources/assets/industrialmetallurgy/models/{block,item}/*.json`
- `src/main/resources/assets/industrialmetallurgy/textures/{blocks,items,gui}/*.png`
- `src/main/resources/assets/industrialmetallurgy/lang/en_us.json` (translation keys, e.g. `item.industrialmetallurgy.<name>` / `block.industrialmetallurgy.<name>`)
- `src/main/resources/data/industrialmetallurgy/loot_tables/blocks/*.json` (blocks only, drop-self loot table)
- `src/main/resources/data/industrialmetallurgy/tags/{blocks,items}/*.json` and `src/main/resources/data/forge/tags/{blocks,items}/*.json` where the item/block should belong to a Forge ore/item tag (e.g. `forge:ingots/copper`)
- `src/main/resources/data/industrialmetallurgy/recipes/**/*.json` for both custom machine recipes and any vanilla crafting/smelting recipes

`src/generated/resources` (produced by `runData`, checked into git with forced LF line endings per `.gitattributes`) is merged into `sourceSets.main.resources` at build time — treat it as generated output, not something to hand-edit.
