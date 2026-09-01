# Industrial Metallurgy

A Minecraft mod that adds an ore-to-alloy metal processing chain: new ores, over two dozen
metals and alloys (ingots/nuggets/blocks), and a line of multiblock-free processing machines
to refine and combine them, feeding into electronics- and chemistry-flavored crafting chains.

Mod ID: `industrialmetallurgy`.

## What it adds

- **13 ores** across 4 mining-tool tiers (argentite, bauxite, cassiterite, chromite, cobaltite,
  cuprite, galena, garnierite, lepidolite, pyrolusite, rutile, scheelite, sphalerite), plus oil
  sand as a bitumen source.
- **~27 metals and alloys** (brass, bronze, steel, stellite, invar, nichrome, nikrothal, kanthal,
  constantan, electrum, cobalt steel, tungsten steel, copper tungsten, alnico, solder, nequitum,
  and the base metals that feed them), each with an ingot, nugget, and storage block.
- Intermediate materials for two downstream crafting chains: **electronics** (conducting
  elements, printed circuit boards, integrated circuits, capacitors, batteries, controller
  boards, electric motors) and **chemistry** (sulfur, phosphorus, arsenic, sulfuric acid,
  ethylene glycol, HDPE, ceramic fiber, welding flux, fertilizer, and more), built from ore
  byproducts most other mods discard.
- **9 processing machines**, each Forge Energy (FE)-powered:
  - **Crusher** — breaks ores into crushed ore (better yield than a furnace) using consumable
    burr sets; an optional sulfuric acid bottle boosts output.
  - **Coke Oven** — cokes coal/charcoal into coal coke, a hotter fuel used by the higher forge
    tiers.
  - **Forge** (4 tiers: Iron/Steel solid-fuel, Cobalt/Tungsten electric) — combines multiple
    ingredients into alloy ingots once hot enough; each tier gates which alloy recipes it can run.
  - **Thermoelectric Generator** — burns fuel to generate FE for the other machines.
  - **Extruder** — reshapes ingots into plates, foil, wire, gears, and magnets.
  - **Soldering Station** — assembles electronics from a 3x3 grid of components plus solder wire.
  - **Chemical Centrifuge** — spins a single input apart into up to 3 byproducts.
  - **Chemical Reactor** — combines 3 reagents into 1-2 chemical products.

## Project status

The mod is mid-rewrite from Minecraft 1.16.4 / Forge to Minecraft `26.2` / NeoForge; `master`
tracks the NeoForge rewrite (the pre-rewrite 1.16.4 Forge codebase is preserved on the
[`1.16.4`](../../tree/1.16.4) branch). See `CLAUDE.md` for the current architecture.

**Ported to 26.2:**
- Full item and block registry (metals, ores, misc resources).
- All 9 processing machines above, each with a working block/tile-entity/GUI/recipe stack on
  the modern data-component, transfer-API-energy, `ValueInput`/`ValueOutput` APIs.

Since that initial rewrite: recipe/progression gaps closed, a full tool tier (Steel/Cobalt
Steel/Stellite/Tungsten Steel/Tungsten-Rhenium) and 4-set armor line, FE-powered hand tools
(Power Drill/Chainsaw/Cultivator) with swappable bit/battery sockets, Battery Box, Electric
Furnace, and Arc Furnace (a higher-yield/faster 5th Forge tier). Nequitum was replaced outright
with Tungsten-Rhenium, a real alloy, closing out the mod's one remaining "unobtainium." Ore world
generation is wired up. See `ROADMAP.md` for the full history and current design notes.

**Not yet ported:**
- **JEI integration** — present on the 1.16.4 branch, not yet reconnected.
- **Real art** — almost every raw ore block and base-metal ingot/nugget/block in the mod has no
  texture at all yet (not just "a handful"); machines and most processed/intermediate items reuse
  recolored placeholder art rather than final textures. A first procedural pass on ore-block
  textures is underway (`ROADMAP.md` Part 9).

## Building & running

Use the Gradle wrapper — don't rely on a system Gradle install, NeoGradle pins its own version.

- Build the mod jar: `./gradlew build`
- Compile only: `./gradlew compileJava`
- Run the client (dev environment): `./gradlew runClient`
- Run a dedicated server (dev environment): `./gradlew runServer`

If your IDE is missing libraries or something looks broken, `./gradlew --refresh-dependencies`
refreshes the local cache; `./gradlew clean` resets build outputs without touching your code.

### Mapping names

This project uses Mojang's official mappings for methods and fields in the Minecraft codebase.
These names are covered by a specific license — see the mapping file itself, or the reference
copy at https://github.com/NeoForged/NeoForm/blob/main/Mojang.md.

### Additional resources

- Community documentation: https://docs.neoforged.net/
- NeoForged Discord: https://discord.neoforged.net/
