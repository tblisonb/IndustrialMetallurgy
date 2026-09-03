# Industrial Metallurgy

A Minecraft mod that adds an ore-to-alloy metal processing chain: new ores, over two dozen
metals and alloys (ingots/nuggets/blocks), and a line of multiblock-free processing machines
to refine and combine them, feeding into electronics- and chemistry-flavored crafting chains.

Mod ID: `industrialmetallurgy`.

## What it adds

- **13 ores** across 4 mining-tool tiers (argentite, bauxite, cassiterite, chromite, cobaltite,
  galena, garnierite, lepidolite, pyrolusite, rheniite, rutile, scheelite, sphalerite), plus oil
  sand as a bitumen source. Every ore favors one real-world-plausible biome and mining-tool tier,
  so finding a metal is also an excuse to go explore.
- **~28 metals and alloys** (brass, bronze, steel, stellite, invar, nichrome, nikrothal, kanthal,
  constantan, electrum, cobalt steel, tungsten steel, tungsten-rhenium, copper tungsten, alnico,
  solder, and the base metals that feed them), each with an ingot, nugget, and storage block, and
  each alloy chosen for a real metallurgical property rather than an arbitrary tier gate.
- **5 tool tiers** (Steel, Cobalt Steel, Stellite, Tungsten Steel, Tungsten-Rhenium) and **4
  armor sets** (Steel, Titanium, Stellite, Tungsten-Rhenium), plus **4 FE-powered hand tools**
  (Power Drill, Chainsaw, Cultivator, and Prospector — a handheld ore magnetometer), each crafted
  once and configured via two swappable sockets (a tiered implement, and a rechargeable Battery
  Pack) instead of needing five more tool tiers apiece.
- Intermediate materials for two downstream crafting chains: **electronics** (conducting
  elements, printed circuit boards, integrated circuits, capacitors, batteries, controller
  boards, electric motors) and **chemistry** (sulfur, phosphorus, arsenic, sulfuric acid,
  ethylene glycol, HDPE, ceramic fiber, welding flux, fertilizer, and more), built from ore
  byproducts most other mods discard.
- **11 processing machines**, each Forge Energy (FE)-powered unless noted:
  - **Crusher** — breaks ores into crushed ore (better yield than a furnace) using consumable
    burr sets; an optional sulfuric acid bottle boosts output.
  - **Coke Oven** — solid-fuel; cokes coal/charcoal into coal coke, a hotter fuel used by the
    higher forge tiers.
  - **Forge** (4 tiers: Iron/Steel solid-fuel, Cobalt/Tungsten electric) plus the **Arc Furnace**
    (a 5th tier) — combine multiple ingredients into alloy ingots once hot enough; the Arc
    Furnace also refines crushed ore into ingots at a 50% yield bonus, and is the only place
    Rhenium can be smelted.
  - **Electric Furnace** — runs ordinary vanilla smelting recipes on FE instead of fuel.
  - **Autoclave** — leaches a crushed ore with a lixiviant (an acid, alkaline, or cyanide
    solution, matched to that ore's real chemistry) into a metal-bearing solution; paired with
    the Chemical Reactor's precipitation step, this is a third, late-game yield tier — 2x the
    crushed-ore baseline.
  - **Thermoelectric Generator** — burns fuel to generate FE via a real copper/constantan
    thermocouple pairing; upgradeable with an installed Thermoelectric Coupling.
  - **Solar Panel** — passive FE generation from an unobstructed daytime sky view, no fuel or
    recipe.
  - **Battery Box** — discharges a battery item into a shared FE buffer, and recharges Battery
    Packs.
  - **Extruder** — reshapes ingots into plates, foil, wire, gears, and magnets.
  - **Soldering Station** — assembles electronics from a 3x3 grid of components plus solder wire.
  - **Chemical Centrifuge** — spins a single input apart into up to 3 byproducts.
  - **Chemical Reactor** — combines up to 3 reagents into 1-2 chemical products, including
    leach-solution precipitation.
- **Minimal in-mod logistics** — one universal Conduit + I/O Port pair moves both FE and items
  between machines and vanilla storage (chests, hoppers, furnaces, etc.), no per-resource pipe
  types.
- **World generation** — every ore spawns in a specific biome and height band, gated by mining
  tier.
- **An in-game guide book** (craft: a book + an iron nugget) explaining the mod, with `GUIDE.md`
  as its human-readable source text.
- **JEI integration** — every custom machine recipe type is browsable in Just Enough Items.

## Project status

The mod is a from-scratch rewrite from Minecraft 1.16.4 / Forge to Minecraft `26.2` / NeoForge;
`master` tracks the rewrite (the pre-rewrite 1.16.4 Forge codebase is preserved on the
[`1.16.4`](../../tree/1.16.4) branch). See `CLAUDE.md` for the current architecture, `ROADMAP.md`
for what's still open, and `GUIDE.md` for a player-facing walkthrough.

Everything listed above is implemented, data-driven, and verified compiling/building/loading on
a dedicated server. It has not yet been confirmed end-to-end against a real graphical client (no
display is available in this development environment) — treat anything visual (icon readability,
GUI layout, worn-armor fit, on-screen text) as compiled-correct but not eyes-verified until
played. Placeholder/procedurally-generated art still covers a meaningful fraction of the mod's
items and textures; see `ROADMAP.md` if you want to help push any of it toward final art.

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
