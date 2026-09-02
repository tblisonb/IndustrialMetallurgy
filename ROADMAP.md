# Industrial Metallurgy — Roadmap & Item Audit

This document is a working reference, not a commitment. It has three jobs:

1. **Recipe gaps** — content that was already designed (on the 1.16.4 branch, or implied by
   items that already exist) but hasn't been ported to `26.2` yet. This is mechanical work, not
   creative work.
2. **Item audit** — every registered item/block, checked against every recipe file across both
   branches, to see what's actually load-bearing vs. what's decoration or dead weight.
3. **Ideas** — concrete ways to give real jobs to the items that don't have one, grounded in the
   same "everything should be real" philosophy the rest of the mod follows.

Numbers below come from grepping every recipe JSON on both `master`/`26.2` and `1.16.4` for
every registered item ID, so they're exact as of this writing, not estimates.

---

## Part 1 — Recipe & progression gaps — **DONE**

Everything in this section has been ported. The mod now has a complete survival progression
from ore to machine: 1618 → 1772 recipes loaded. What follows is a record of what was done and
a few real design decisions made along the way (not pure porting), for future reference.

### 1. Machine crafting recipes — all 9 machines, done
Crusher's recipe was ported verbatim from 1.16.4:

```
sps       s = conducting_element   p = steel_plate
gmg       g = gear                 m = electric_motor
pbp       b = battery_cell
```

The other 8 machines never had a recipe on either branch, so these were designed fresh. Crusher,
Extruder, Soldering Station, Chemical Centrifuge, and Chemical Reactor share that same housing
(conducting_element/gear/electric_motor/steel_plate/battery_cell) with one "specialty" ingredient
in the top-middle slot marking what the machine does — Extruder gets `tungsten_steel_ingot`
(high-pressure tooling), Soldering Station gets `solder_ingot`, Chemical Centrifuge gets
`titanium_ingot` (real centrifuge rotors are titanium), Chemical Reactor gets `refractory_brick`
(corrosion/heat lining). Coke Oven and Thermoelectric Generator got their own designs since
neither belongs to that FE-consuming family: Coke Oven is a refractory-brick-and-iron shell
around a vanilla furnace (no FE — it's solid-fuel), and the Thermoelectric Generator pairs
copper and constantan around a furnace, which is specifically the real Seebeck-effect pairing
used in actual thermocouples/thermoelectric generators.

The 4 Forge tiers all share one template — tier metal at the corners, a tier-specific "core"
block in the center, and an edge material that differs by fuel type (refractory bricks for the
solid-fuel tiers 1-2, conducting elements for the electric tiers 3-4):

```
mem       m = tier ingot
ece       e = refractory_bricks (t1/t2) or conducting_element (t3/t4)
mem       c = that tier's forge core
```

The `iron_forge_core`/`steel_forge_core`/`cobalt_forge_core`/`tungsten_forge_core` items existed
in the registry but had **never** had a recipe, on either branch — confirmed by the audit in
Part 2. They now do: 8 tier-metal ingots wrapped around a refractory brick.

### 2. Smelting — 27/27 ported
Every base metal's ore→ingot and crushed-ore→ingot path now works, ported 1:1 from 1.16.4 (same
ingredients/experience, reformatted to the current `minecraft:smelting` schema).

### 3. Basic crafting — 68/68 ported
All ingot↔nugget/block conversions (one-directional by original design — you can turn ingots
into nuggets/blocks, not back, unlike vanilla iron), the 5 burr sets, the electric motor
sub-assembly (`rotor`/`stator`/`field_coil` → `electric_motor`), refractory brick/composite, and
`conducting_element`.

**`refractory_bricks` (the block, plural) was registered for the first time.** It had a full
1.16.4 asset set (blockstate/models/loot table) and its texture had already been copied into
`26.2` at some point, but it was never added to `RegistryHandler` — so its own crafting recipe
existed as dead JSON referencing a block that didn't exist. Fixed as part of this pass.

### 4. Crusher's own processing recipes — 17/17 ported
All 13 remaining crushed-ore outputs plus `crushed_diamond` are in. The two coal/charcoal
recipes were renamed from `carbon_dust_from_*` to `crushed_coal_from_*` and their output fixed
from `industrialmetallurgy:carbon_dust` — **which was never a registered item, on either
branch** — to `industrialmetallurgy:crushed_coal`, which already exists and is exactly what
several already-ported Chemical Reactor recipes (`graphite_rod`, `hdpe_sheet`,
`tungsten_carbide_dust`) expect as an ingredient. This wasn't a stray fix — it closes a chain
this session already built without realizing the crushing step was missing.

### 5. `oil_sand` → `bitumen` — redesigned, not just fixed
Superseded mid-session: rather than having `oil_sand` drop `bitumen` directly, it now drops a
new intermediate item, `oily_sand` (silk touch keeps the block, same as `lepidolite_ore`'s
pattern), which the Chemical Centrifuge separates into `bitumen` + `minecraft:sand` +
`minecraft:clay_ball`. This is more real than a direct drop — actual oil/tar sand is a
sand-and-clay matrix bound with bitumen, separated by processing, not just "mined as fuel." No
existing art for the new item, so it currently reuses `oil_sand`'s own block texture as its icon
— flagged as a placeholder, not a final asset.

### 6. Also discovered and folded in: block loot tables — every registered block now has one
Not originally itemized as its own gap, but found while working on `oil_sand`'s loot table:
**only 12 of the 59 blocks currently registered in `26.2` had a loot table** — every ore block
and every metal storage block dropped nothing at all when broken. Ported all of them (uniform
"drops itself" pattern, except `lepidolite_ore` and `oil_sand`, both special-cased above). 1.16.4
had 60 loot table files total, but one of those was for a block that was never carried forward
into `26.2`'s registry, so 59/59 current blocks is the complete set.

### Still open (unchanged, tracked in `README.md`, out of scope for this pass)
JEI integration. World generation shipped later — see Part 9. The steel/nequitum tool lines
(plus a titanium/stellite/cobalt-steel/tungsten-steel expansion) shipped later — see Part 6.

---

## Part 2 — Item/block usage audit — **DONE** (except `heating_element`, deferred on purpose)

The audit turned up something bigger than "underutilized items": **`battery_cell` had zero
recipe producing it, on either branch, ever** — and every one of the 5 machines shipped in Part 1
requires one in its own crafting recipe (the `b` corners in the shared housing pattern). That
meant nothing in the mod was actually craftable in survival. This wasn't a Part 1 oversight, it
predates this rewrite entirely; the 1.16.4 branch has the exact same gap. Fixing it was the
priority for this pass, and it drove the rest of the design below.

### The battery chain — bootstrap-safe, real chemistry
`battery_cell`'s fix couldn't depend on any Industrial Metallurgy machine (that's circular — you'd
need a machine to make the item that unlocks building your first machine), so the whole chain
below only uses raw ore, vanilla-furnace ingots, and vanilla items:

- **`dry_cell`** (vanilla craft): `zinc_ingot` + `manganese_ingot` + `minecraft:coal`. This is a
  real zinc-carbon dry cell — zinc can (anode), manganese dioxide cathode (pyrolusite ore *is*
  MnO₂ in real life, which is why it smelts into the same manganese this needs), carbon for the
  current collector. Gives `dry_cell` its first real job — it was never produced *or* consumed
  before this pass.
- **`battery_cell`** (vanilla craft): `dry_cell` + `lead_ingot` + `copper_ingot` → the lead-acid
  cell every machine's recipe already expects (its lang key already said "Lead-Acid Battery Cell"
  — this recipe just makes that true). This is the fix: all 5 Part-1 machines are now actually
  buildable from nothing but ore, a furnace, and a crafting table.
- **`dry_cell_bank`** / **`battery_bank`** (vanilla craft, 4x cell + a casing/wiring item): bulk
  packs of the tier below. Not on the critical path, so they didn't need to stay machine-free, but
  there was no reason to gate them either — "bank" is just bundling cells together.
- `lithium_battery_cell`/`lithium_battery_bank` are unchanged (Soldering Station, already worked).

### Battery Box — new machine, gives 4 more items a job
A 6th FE-family machine, same shared-housing pattern as Part 1 (`conducting_element`/`gear`/
`electric_motor`/`steel_plate`/`battery_cell`) with `induction_core` as its specialty ingredient
(the charging-coil framing Part 3 already suggested). Mechanically it's
`ThermoelectricGeneratorBlockEntity`'s exact burn-and-push-to-neighbors loop, but instead of
burning a vanilla fuel item it discharges a battery item for a fixed FE value (dry cell < lead-acid
< lithium, banks worth 4x their cell) — insert a battery, it drains into the box's buffer over
time and pushes out to adjacent machines exactly like the Thermoelectric Generator already does.
This is what actually *consumes* `dry_cell`, `dry_cell_bank`, and `battery_bank` (their crafting
recipes above only *produce* them), and its own crafting recipe consumes `induction_core`
(`minecraft:iron_ingot` + 2x `magnet_wire`, vanilla craft). No new recipe type needed — it's a
pure item-and-energy machine, not a recipe-processing one. Reuses the Thermoelectric Generator's
GUI texture and its `bronze_block`→`refractory_bricks` top/bottom swap for a placeholder look;
no dedicated art yet.

### The two leftover dead-ends — folded into existing recipes
- **`graphite_rod`**: `chemical_reactor.json`'s pattern lost one of its two `conducting_element`
  corners in favor of a `graphite_rod` — real reactor vessels use graphite control/moderator rods,
  and this was a one-ingredient swap on a recipe this same pass already owns.
- **`memory_wire`**: `controller_board.json` (Soldering Station) swapped one of its two
  `electrolytic_capacitor`s for `memory_wire` — a shape-memory-alloy element is a real way to build
  a self-resetting thermal-protection trip on a circuit board.

### `heating_element` — resolved in the Part 3 pass that followed
Left open at the end of this pass; picked up immediately after as the first item in Part 3 below
(Electric Furnace). See that section for the writeup.

---

## Part 3 — Ideas for giving real jobs to underutilized items

Organized by theme. Each ties back to a specific item above and to the real-world property that
justified adding that material in the first place. Seven of these (Battery Box and the two
dead-end recipe edits from Part 2, Electric Furnace, the tool/armor tier, powered tools, and the
Arc Furnace tier) are now done, and are left here with a status note rather than removed, since
the rest of the section still reads as a single set of ideas. What's left below (Solar Panel,
powered armor) is its own bigger scope of work, not blocked on anything from this pass.

### Electric Furnace — **done**
Built as a direct follow-on to Part 2 (same session, right after): a 7th FE-family machine,
same shared-housing crafting pattern as the rest with `heating_element` as its specialty
ingredient. Mechanically it's the cheapest of these ideas to build, exactly because it processes
vanilla `minecraft:smelting` recipes directly — `RecipeType.SMELTING` / `SmeltingRecipe.assemble()`
— rather than a custom recipe type, so every ore's smelting recipe from Part 1 works in it with
zero new recipe JSON. Real heating elements wear out, so `heating_element` is a slot item that
depletes over 200 smelts and then consumes itself, rather than a permanent tool like the Crusher's
burr sets. `heating_element` itself finally got a producing recipe too: `resistance_wire` (already
made from `nikrothal_ingot`, a nichrome/kanthal alloy) + `refractory_composite` — real heating
elements are resistance wire wound on an insulating former. Reuses the Coke Oven's front-lit
texture pair and the Crusher's GUI layout as placeholder art.

### Battery Box — **done, see Part 2**
Built as part of Part 2's audit cleanup: a 6th FE-family machine that discharges a battery item
(`dry_cell` through `lithium_battery_bank`) into a bulk FE buffer it then pushes to neighbors.
Uses `induction_core` as its own crafting specialty.

### Arc Furnace tier — **done, see Part 8**
Landed as a higher-yield-refining machine (5th Forge tier) that also gates the new Rhenium/
Tungsten-Rhenium chain — ended up giving `graphite_rod` its second use as planned here, just as
a superalloy electrode in the endgame alloy recipe rather than in a titanium/tungsten gate
specifically.

### Rechargeable battery-powered tools — **done, see Part 7**

### Solar panel — uses `silicon_plate`
Real photovoltaic cells are silicon. You already mine toward silicon (lepidolite → silicon →
silicon_plate for integrated circuits) — a solar panel using silicon_plate + copper/silver wire
+ glass is a legitimate real-world use, not just an IC2 reference.

### Thermoelectric upgrade — uses `constantan`
Real thermocouples and thermoelectric generators pair constantan with copper (the Seebeck
effect) — that's specifically why constantan exists as an alloy rather than being folded into
something else, and it's now literally what the Thermoelectric Generator's own crafting recipe
uses (Part 1 §1). An efficiency upgrade/component built the same way would extend that loop
further.

### Powered tools — **done, see Part 7**

### Titanium/Steel/Cobalt Steel/Stellite/Tungsten Steel tools & armor — **done, see Part 6**

### Powered armor (exoskeleton) — flagged, not started
Raised alongside power tools as a "maybe" — a real follow-up idea, but its own design surface
(per-slot effects, FE drain rates, what it does differently from Nequitum armor) rather than a
small extension of Part 6 or 7.

---

## Part 6 — Tool & armor material tier — **DONE**

The 1.16.4 branch only ever had Steel and Nequitum tools (pickaxe/axe/shovel/sword/hoe), and no
armor at all, ever — so this was new design, not a port, done deliberately narrow: of the mod's
~28 metals, only 5 got tools and only 4 got armor, each for a specific real-world reason rather
than mechanically covering every material.

### Tools — 5 tiers
Steel is the 1.16.4 baseline carried forward unchanged (iron harvest level, 3x iron durability,
diamond speed/damage/enchantability). The other 4 are new:

- **Cobalt Steel** — real cobalt-alloy tool steel (e.g. M42 high-speed steel) is prized for
  keeping a sharp edge at high heat, so it's a lateral upgrade from Steel (same iron harvest gate,
  noticeably faster) rather than a harder-material tier.
- **Stellite** — a real cobalt-chromium-tungsten superalloy used for hard-facing and extreme-wear
  cutting edges (valve seats, saw tips). This is where the harvest level actually jumps to
  diamond-tier — hard enough to mine what iron/steel/cobalt steel can't.
- **Tungsten Steel** — ultra-hard, ultra-dense tool steel; the pre-capstone tier, matching
  netherite's speed/damage almost exactly (same diamond-tier harvest gate as Stellite, better raw
  stats).
- **Nequitum** — capstone, unchanged from 1.16.4, including its 0-durability quirk: an item with
  no max damage can't be damaged at all, so nequitum tools simply never break.

Repair-ingredient tags (`data/industrialmetallurgy/tags/item/*_tool_materials.json`) were added
for all 5, one per metal's ingot.

### Armor — 4 sets, not 28
Only materials whose real properties suggest actual protective gear, each earning its slot on
purpose:

- **Steel** — plain baseline, no special effect, solid protection (parallels vanilla iron).
- **Titanium** — real titanium's whole reputation is strong-but-light and corrosion-resistant →
  permanent **Resistance I** while the full set is worn.
- **Stellite** — same real heat/corrosion-resistant superalloy as its tool tier, different
  application → permanent **Fire Resistance** while the full set is worn.
- **Nequitum** — capstone, strongest raw defense (matches/beats netherite on every stat), and
  carries both bonuses at once, echoing how netherite armor stacks fire immunity on top of
  best-in-slot numbers in vanilla.

Deliberately excluded: everything else audited in Part 2/3 (electrical/heating alloys, precision-
instrument metals, soft/decorative metals, actuator wire) — see the original selection writeup for
the specific reasoning per material.

### How the set bonus actually works
`ArmorSetBonusHandler` subscribes to `PlayerTickEvent.Post` (server-side only), checks all 4
armor slots against each material's helmet/chestplate/leggings/boots items every tick, and
re-applies a short-duration `MobEffectInstance` (ambient, no particles, icon visible) whenever a
full set matches — simplest way to make a passive-while-worn bonus, no NBT tracking needed.

### API note
This Minecraft version restructured tools/armor entirely from the class-hierarchy system (
`PickaxeItem`, `SwordItem`, `IItemTier`/`ArmorMaterial` subclassing) into a data-component system:
`ToolMaterial`/`ArmorMaterial` are now records, pickaxes/swords are plain `Item`s configured via
`Item.Properties#pickaxe`/`#sword`/`#humanoidArmor`, and only axe/hoe/shovel keep dedicated
classes (for their unique right-click behavior). Verified against the actual decompiled 26.2
sources and the real NeoForge sources jar rather than assumed, the same way Electric Furnace's
vanilla-smelting-recipe reuse was verified in Part 3.

### Placeholder art
All 41 new items (icons) and 4 new armor materials (worn-armor textures via the new
`assets/industrialmetallurgy/equipment/*.json` asset system) use vanilla iron tool/armor textures
recolored per material via ImageMagick, not hand-drawn art. Correctly shaped and immediately
readable, but not final.

---

## Part 7 — Power tools — **DONE**

FE-powered hand tools with a real, distinct value proposition each — not just faster versions of
the hand tools, but tools that do something the hand tiers structurally can't. Also introduces the
mod's first per-item (not per-block) FE storage, and its first item that degrades and gets
replaced in place instead of re-crafted.

### The core idea: two live sockets, not five more item tiers
Each tool (Power Drill, Chainsaw, Cultivator) is crafted **once**. What determines its mining tier
and how much charge it has are two separate items you socket into it:

- **An implement** (Drill Bit / Chain / Cultivator Blade) — same 5-tier ladder as the hand tools
  (`ModToolMaterials`), same durability numbers, and it's genuinely what determines mining
  speed/harvest level: the tool's own `getDestroySpeed`/`isCorrectToolForDrops` just read whatever
  `Tool` data component the socketed implement carries, exactly the way a plain pickaxe reads its
  own. When it wears out it's consumed (Nequitum implements never wear out, same as the Nequitum
  hand tools), and you craft a replacement for a couple of ingots — not a whole new tool.
- **A Battery Pack** — a new, genuinely rechargeable item (unlike the mod's existing 6 battery
  items, which are one-shot fuel). Crafted from `lithium_battery_cell` specifically: lithium-ion is
  the only chemistry in the mod actually right for a cordless power tool (lead-acid/dry-cell are
  the wrong tech for that). An **Advanced Battery Pack** upgrade (Battery Pack +
  `lithium_battery_bank`) gives more capacity. Recharges by sitting in a new 6th slot on Battery
  Box (Part 2) that tops it up in place, same FE rate as the box already pushes to neighbors.

Swapping either one uses the exact click-onto-item interaction vanilla Bundles already use
(`Item#overrideOtherStackedOnMe`) — left-click a valid implement or pack onto the tool to install
it, right-click the tool with an empty cursor to pull whichever's currently in (implement first,
then battery) back out. No dedicated screen. A tooltip always shows both socket states, explicitly
flagging either one as "not installed" per your ask.

### What each tool actually does
- **Power Drill** — breaks a fixed 3x3 plane (perpendicular to your facing) around whatever you
  mine, capped by nothing but the socketed bit's own harvest-level gate — same fixed size at every
  tier, matching Tinkers' Construct's hammer, so tier is purely "what can this bit cut," not "how
  big an area."
- **Chainsaw** — fells a whole tree in one swing: breaks the targeted log and flood-fills through
  every connected log block. Wood isn't hardness-gated in vanilla, so a Chain's tier doesn't gate
  anything — instead it raises the felling cap (16 logs at Steel/Cobalt Steel, 32 at
  Stellite/Tungsten Steel, 64 at Nequitum), which is the one place a power tool's tier changes area
  instead of just gate/durability.
- **Cultivator** — tills a 3x3 and plants a seed from your inventory into each freshly-tilled block
  in the same action, reusing NeoForge's own `getToolModifiedState`/`ItemAbilities.HOE_TILL` so it
  respects whatever any other mod's blocks define as tillable, not just vanilla dirt/grass.

Every extra block beyond the one you'd get for free costs FE, drawn straight from the socketed
Battery Pack; running out just means the tool falls back to single-block behavior rather than
becoming completely unusable.

### Implementation notes
- Two new mod-registered `DataComponentType`s (`socketed_bit`, `socketed_battery`) reusing
  `ItemContainerContents` — the same value type vanilla's shulker boxes use — sized to one slot,
  purely to reuse its existing codec rather than writing a new one. A third
  (`stored_energy: Integer`) backs the Battery Pack's own charge.
- Deliberately skipped `Capabilities.Energy.ITEM` (the formal item-energy capability system) even
  though it exists in this version and was the original plan — it requires bridging the old
  `IItemHandlerModifiable` slots every machine in this mod already uses with a newer
  `ItemAccess`/`ResourceHandler` API that's never been touched anywhere else in the codebase, for
  no real benefit here: Battery Box and the power tools are the only two things that ever need to
  read a Battery Pack's charge, and they can both just read/write the `stored_energy` component
  directly. Worth revisiting only if a reason to interop with something external ever comes up.
- Also fixed a real bug found while wiring this up: Battery Box and Electric Furnace's energy
  capability was never registered in `IndustrialMetallurgy#registerCapabilities` — meaning neither
  could actually receive FE pushed from a neighboring block. For Electric Furnace specifically that
  meant it had no working power source at all since it shipped in Part 3.

### Placeholder art
All 20 new items (15 implements + 2 battery packs + 3 tool bodies) reuse vanilla iron tool/nugget/
ingot shapes recolored per material, same approach as Parts 3 and 6. The 15 implements in
particular all reuse the same nugget silhouette regardless of type (bit vs. chain vs. blade look
identical within a material tier) — the roughest placeholder in the mod right now, flagged for
whenever real art happens.

---

## Part 8 — Arc Furnace, and replacing Nequitum with Tungsten-Rhenium — **DONE**

This came out of finding an old (10/5/2020) design document for the mod and cross-checking it
against what's actually built (see that conversation for the full diff) — it independently named
"Arc Furnace" as a planned machine, matching the "Blast/Arc Furnace" idea already sitting in Part
3. Building it surfaced the mod's one remaining "unobtainium": Nequitum, the only material in the
mod that was never grounded in something real. Both got resolved together, since the endgame
alloy is what actually justifies the Arc Furnace's existence.

### Arc Furnace — a 5th Forge tier, reusing the existing machinery entirely
Mechanically this is `AdvancedForgeBlockEntity`/`ForgeRecipe` (the same system Forge Tiers 3-4
already run on) under a new name and identity, not a new recipe type or a parallel system:
`ArcFurnaceBlockEntity` just accepts one more tier string (`"arc"`, cumulative with
iron/steel/cobalt/tungsten like every tier before it) and runs hotter and faster than every other
tier (35-tick process time vs. the uniform 50 everywhere else; 4200° max temperature). Its own
crafting recipe follows the established forge-family template exactly (shared housing +
`arc_furnace_core`, itself built the same way every other tier's core is).

Its actual job, per your steer, is **higher-yield refining first, endgame alloy second**:
- **Higher-yield refining**: 14 new `tier: "arc"` recipes — one per existing base metal — that
  turn 2 crushed ore into 3 ingots, a real 50% yield improvement over the Electric Furnace's 1:1.
  This is useful the moment you build an Arc Furnace, independent of anything else below, and
  keeps the Crusher relevant at the endgame (the bonus applies to *crushed* ore specifically, not
  raw ore — real arc furnaces process refined/prepared feedstock, not raw rock).
- **Endgame gate**: Rhenium can only be smelted here at all (see below) — real rhenium has the
  2nd-highest melting point of any element, so "needs the hottest furnace" isn't a arbitrary gate.

### Nequitum → Tungsten-Rhenium: a full replacement, not a relabel
Real tungsten-rhenium alloy is used for rocket nozzles and high-temperature thermocouples, which
makes it a natural, *accurate* successor to Tungsten Steel rather than an arbitrary "endgame
metal" swap. The chain:

- **Rheniite** — the ore. A real (and genuinely, extremely rare — discovered in 1994 in a single
  volcanic fumarole) rhenium mineral. Tier-4 rarity, matching Scheelite/Lepidolite.
  `crushed_rheniite_ore` via the Crusher, same as every other ore.
- **Rhenium** — smelted from crushed Rheniite, Arc Furnace only, no vanilla-furnace or
  Electric-Furnace path at all (unlike every other base metal) — its melting point is the reason
  this material needed a reason for the Arc Furnace to exist beyond yield bonuses.
- **Tungsten-Rhenium** — `tungsten_steel_ingot` + `rhenium_ingot` + `graphite_rod` (a second real
  use for graphite as a superalloy-processing electrode, beyond its Part 2 control-rod job),
  Arc Furnace only (`tier: "arc"`). This is the new capstone: same tool/armor role Nequitum had
  (5th tool tier, 4th armor set, the burr set, the power-tool implements), same 0-durability
  "never breaks" quirk, but every step of getting there is now a real material and a real
  metallurgical reason to need the hottest furnace in the mod.

Full replacement, not a reskin: `nequitum_*` registrations, recipes, tags, lang entries, and
textures were removed outright and replaced with `tungsten_rhenium_*` equivalents (58 files
renamed, contents updated, not just relabeled) — reasonable for a solo project with no live saves
to preserve compatibility for.

### Placeholder art, and a gap this surfaced
Arc Furnace reuses the Tungsten Forge's existing (real, complete) texture set, recolored — same
approach as every machine this session. Rheniite ore, Rhenium/Tungsten-Rhenium's ingot/nugget/
block forms, and the Arc Furnace Core needed *no* new placeholder art, because — discovered while
doing this pass — **no raw ore block or metal ingot/nugget/block in the entire mod has real
textures or models**. Only the processed/intermediate items and the machines themselves do. This
is a much bigger, pre-existing gap than "some placeholder textures" (README's current framing)
suggests; worth its own line in Part 5 if a real art pass ever happens.

---

## Part 9 — World generation, a full texture pass, and retiring Cuprite/Sulfur — **DONE**

Three threads: getting ores to actually spawn (flagged as an open gap since Part 1), a first
real attempt at giving new items/blocks art instead of tinted placeholders, and removing two
materials that vanilla itself has since made redundant.

### World gen: rebuilt, not ported
The 1.16.4 world-gen system (`OreGenConfig`/`OreGenHandler`/`OreFeatureHandler`) never existed in
`26.2` at all — not broken, just never carried over. Worse, it couldn't have been ported as-is:
it worked by reflecting into `BiomeGenerationSettings`'s private field at runtime
(`ObfuscationReflectionHelper.setPrivateValue`) to inject features after the fact, `Biome.Category`
(its whole biome-gating mechanism) doesn't exist anymore, and the Overworld height range grew from
0–256 to -64–320, making every old height number meaningless as a direct port.

What it's rebuilt on instead is real infrastructure that didn't exist in 1.16.4: NeoForge's
`neoforge:add_features` biome modifier — a plain datapack JSON that adds placed features to
biomes matching a tag, no reflection, no runtime hackery. Vanilla's own ore generation is fully
data-driven the same way (`worldgen/configured_feature` + `worldgen/placed_feature`), so this
mod's ores now use the exact same building blocks vanilla iron/gold/diamond do.

**Biome assignments** come from the 2020 design doc's original single-biome-per-ore pairings, not
the shipped 1.16.4 code's broadened multi-category version (Bauxite was originally "Jungle only";
the code that shipped also allowed Extreme Hills and Ocean). The tighter version was picked on
purpose — it matches "ore gating gives you an excuse to explore" better than the loosened one:

| Biome (tag or ID) | Ores |
|---|---|
| `minecraft:plains` | Argentite (silver), Sphalerite (zinc) |
| `#minecraft:is_jungle` | Bauxite (aluminum), Cassiterite (tin) |
| `minecraft:desert` | Rutile (titanium) |
| `#minecraft:is_forest` | Galena (lead), Garnierite (nickel) |
| `minecraft:swamp` / `mangrove_swamp` | Pyrolusite (manganese) |
| `#minecraft:is_nether` | Chromite (chromium), Cobaltite (cobalt) |
| `#minecraft:is_end` | Lepidolite (lithium), Scheelite (tungsten) |
| `minecraft:basalt_deltas` only | **Rheniite (rhenium)** — deliberately narrower than "all Nether": the real mineral has only ever been found in one volcanic fumarole, so a single specific Nether biome (not the whole `is_nether` tag every other Nether ore uses) is the point. |
| `#minecraft:is_ocean` | `oil_sand`/bitumen — not a metal, but the same `minecraft:ore`-type feature works fine targeting sand instead of stone, placed near the sea floor (Y 30–60). |

**Heights are new, not scaled from the old numbers**, and deliberately correlate with each ore's
existing harvest tier rather than being ad hoc the way the 1.16.4 config was: Tier 1 (Bauxite,
Cassiterite, Garnierite, Sphalerite) gets the widest, shallowest band (Y -16 to 80) and
the biggest veins; Tier 2 (Argentite, Galena, Pyrolusite, Rutile) sits deeper and rarer (Y -48 to
48); Tier 3's two Nether ores and Tier 4's three rarest ores get progressively smaller veins.
Every vein uses vanilla's own `trapezoid` height distribution (weighted toward the middle of its
band, not flat) — same convention real ore veins use.

**Deliberately dropped**: the old per-ore on/off config toggle. NeoForge's biome modifier system
is pure datapack — there's no clean way for a `ModConfigSpec` boolean to gate a static JSON file
without extra plumbing that didn't seem worth building for a toggle nobody had asked to keep.
Vein size and height are datapack-only now too, matching how most modern NeoForge mods handle ore
gen (tunable by overriding the JSON in a datapack, not a live in-game config).

**Verification note**: confirmed clean via `compileJava`/`build`/`runServer` (zero errors, and the
dedicated server's own "Preparing spawn area: 100%" succeeding is itself a real signal — a broken
`configured_feature`/`placed_feature` reference fails loudly at chunk generation, not silently).
What I *can't* verify without a client is whether the rarity/vein-size numbers actually feel
right in play — that's a judgment call that needs eyes on real generated terrain, not log output.

### Texture pass: all 13 ore blocks done
First real (non-placeholder) art pass: procedurally-generated ore block textures for every
remaining ore, using the same stone-base-plus-speckled-cluster technique vanilla's own ore
textures use, with palettes drawn from each mineral's real color rather than picked arbitrarily.
Started as a 3-ore demo (Rheniite, Scheelite, Rutile), published as an artifact for review, then
extended to the other 10 (Argentite, Bauxite, Cassiterite, Chromite, Cobaltite, Galena,
Garnierite, Lepidolite, Pyrolusite, Sphalerite) the same way once approved. `argentite_ore` had a
pre-existing blockstate/model set from before this pass and was left untouched; the other 12
ores got a matching blockstate + block model + item model (`cube_all` template, same convention
`refractory_bricks` already used). Explicitly not final art — a reasonable procedural
placeholder, meant to be hand-edited in Aseprite/Paint.NET rather than shipped as-is long-term.

### Retiring Cuprite/copper and raw Sulfur — vanilla caught up
Vanilla 26.2 now ships its own full copper tier (ingot/nugget/block, plus tools and armor) and a
"Sulfur Caves" biome/block family, making two of this mod's original materials pure duplicates of
things the base game now provides. Both were removed outright rather than kept as redundant
alternate paths:

- **Cuprite ore / `crushed_cuprite_ore` / `industrialmetallurgy:copper_ingot` / `copper_nugget` /
  `copper_block`** — deleted from the registry, along with their crafting/smelting/loot-table/
  world-gen JSON. Every recipe across the mod that consumed or produced the mod's own copper items
  (the Thermoelectric Generator, Battery Cell, Copper Plate, Brass/Bronze/Constantan/
  Copper-Tungsten/Solder ingots, the Integrated Circuit, Magnet Wire) was repointed to
  `minecraft:copper_ingot`/`minecraft:copper_nugget` instead — the recipes themselves are
  unchanged, just sourced from vanilla now. Desert world gen (previously Cuprite + Rutile) now
  only places Rutile.
- **`industrialmetallurgy:sulfur`** — deleted the same way; every recipe that consumed it
  (Gunpowder, Sulfuric Acid Bottle, and both Argentite/Cobaltite chemical-centrifuge byproduct
  recipes) now consumes `minecraft:sulfur` instead. Per your explicit note, the recipes themselves
  stayed — only the item source changed, since sulfur is still a real byproduct of those ores and
  still a real gunpowder/sulfuric-acid ingredient in this mod's chemistry chain.

No config toggle or migration path was needed for either removal — same reasoning as the dropped
per-ore world-gen toggle earlier in this Part: no live saves to preserve compatibility for.

---

## Part 10 — Prospector: a 4th power tool with no vanilla equivalent — **DONE**

Picked over powered armor and multiblocks as the next step because it's small, reuses the Part 7
socket machinery directly, and it's the one idea of the three that actively serves the mod's
stated design pillar (ore gating as "an excuse to go explore," Part 9) rather than just adding
another tier of gear.

### What it does
A handheld ore magnetometer. Insert a crushed-ore item (or, for Lepidolite specifically — the one
mod ore with no crushed intermediate, since it drops its raw form via silk touch instead of going
through the Crusher — the raw `lepidolite` item) into the tool's socket to calibrate what it looks
for, same idea as calibrating a real geophysical instrument against a reference sample. Right-click
to sweep a 33×33×33 cube centered on the player (skipping unloaded chunks, so it never forces new
terrain generation just to scan it) and report the nearest match: distance, an 8-point compass
bearing, and whether it's above/below/level with you, via an action-bar message. A short built-in
use-cooldown (`Item.Properties#useCooldown`) stops it being spammed like a repeating radar rather
than an occasional check-in.

### Reused, not rebuilt
`ProspectorItem` extends `PowerToolItem` (Part 7) directly — its "implement" socket becomes the
sample slot, and its battery socket/FE-drain machinery (`tryDrainEnergy`) is used unchanged. The
one thing that had to change: the base class's tooltip assumes the socketed item has durability
("X/Y uses left"), which is meaningless for a sample that's never consumed, so `ProspectorItem`
overrides `appendHoverText` with a `tooltip.industrialmetallurgy.sample_installed` line instead of
calling `super`. No new `DataComponentType`s were needed at all.

### The sample→target mapping, and why it's intentionally honest
`ProspectorItem` maps each valid sample item to the actual block(s) it should find — the mod's own
12 crushed-ore items to their own ore block, `crushed_gold_ore`/`crushed_iron_ore` to vanilla's
ore *and* its deepslate variant (gold also checks Nether gold ore), `lepidolite` to
`lepidolite_ore`. There's deliberately no dimension-awareness beyond what's actually loaded and in
range: scanning for Chromite/Cobaltite/Rheniite (Nether-only) or Lepidolite/Scheelite (End-only)
from the Overworld just reports nothing found, which is true — it isn't special-cased, it falls
out naturally from the world-gen work in Part 9.

### Crafting and cost
Same shared 3×3 power-tool-body pattern as Drill/Chainsaw/Cultivator (`conducting_element`/
`gear`/`electric_motor`/`steel_plate` frame), with `alnico_ingot` as the specialty center
ingredient — Alnico (aluminum-nickel-cobalt) is a real magnet alloy, and magnetometers are
literally built around one, so this is a more direct real-world fit than any of the other three
tools' specialty ingredients. A scan costs 4000 FE (25 scans per base Battery Pack charge, 75 per
Advanced), a flat per-action cost rather than the other tools' per-block cost, since a scan is one
indivisible action regardless of what it finds.

### Placeholder art
One new item icon: vanilla's spyglass texture (itself a scanning instrument, unlike the other
three tools' recolored pickaxe/axe/hoe shapes) recolored from warm brass/wood to steel-and-copper
with a bright cyan "active scan" lens, via the same exact-RGBA-remap technique the ore textures
(Part 9) used. Not final art.

---

## Part 11 — In-game guide book, and `GUIDE.md` as its source of truth — **DONE**

You asked for a companion guide to the mod, open on whether there was a standard way to do this.
The real standard is Patchouli — checked its actual release feed rather than assuming, and its
most recent build (2026-07-10) only ships NeoForge support through MC 26.1.x, not 26.2, so it's
not usable here yet. Built a custom guide book instead, on your explicit go-ahead.

### One virtual book, built entirely from vanilla parts
`BookViewScreen` — the same screen a written book opens — turns out to take its content as a
plain `List<Component>` via `BookViewScreen.BookAccess`, completely decoupled from any actual
`ItemStack`'s book data. That meant the whole guide could be one large virtual book (57 pages:
1 cover, 7 category index pages, 49 entry pages) built straight from static content, reusing
vanilla's book texture, page-turn buttons, and click-to-jump page links
(`ClickEvent.ChangePage`) for free — no new art, no new screen-rendering code at all.
`GuideBookItem` (craft: `minecraft:book` + `minecraft:iron_nugget`, so it needs nothing from
this mod to obtain) opens it the same way vanilla's own `WrittenBookItem` opens a book, but
skips the sign/edit machinery entirely since the content is static Java data, not per-item NBT.

### `GUIDE.md` is the source; the Java is generated from it
Per your ask, the full writeup is also a standalone, human-readable Markdown document
(`GUIDE.md`) rather than existing only as in-game text. It's the actual source of truth, not a
duplicate: `##`/`###` headings become the book's categories/entries, and a hand-placed
`<!-- page -->` marker is where you want an in-book page break. `tools/guide_book/
gen_guide_data.py` parses that structure and generates `GuideBookData.java` (the raw
category/entry/page-text data) — the page-layout logic (table of contents, page-link wiring)
lives separately in `GuideBookContent.java`, hand-written once since it doesn't change when the
content does. Editing the guide going forward means editing `GUIDE.md` and re-running the
script, not hand-touching generated Java.

### The real constraint: vanilla's own per-page limit
`BookViewScreen` renders at most 14 lines per page and silently drops anything past that — no
error, just truncated text — which lines up with the real vanilla written-book page cap of
roughly 256 characters. Every page in `GUIDE.md` was written and then verified (via a scratch
simulation of the actual page-assembly logic, including the title header on an entry's first
page and the "back to contents" link on its last) to stay under that, with real margin rather
than right up against it, since this can't be visually confirmed without a client.

### What's still unverified
Compiling, building, and a dedicated-server run all confirm the recipe/data load cleanly (1857
recipes, zero errors). What none of that confirms is how the book actually *looks* — real font
wrapping, whether the bold/italic/code inline styling (`MarkdownText`, a small hand-written
`**bold**`/`*italic*`/`` `code` `` parser, not a general Markdown engine) reads well, and whether
57 pages feels like a good length to page through. That needs real client eyes.

---

## Part 12 — Recovering the 1.16.4 asset backlog — **DONE**

You noticed the mod felt visually thinner than you remembered, and you were right: the 26.2 port
carried over the *data* (registry entries, recipes, loot tables — every block/item has worked
since its respective porting Part) but a large fraction of the *client* assets — blockstates,
block/item models, and textures — never made the jump. Compiling and running a dedicated server
never caught this, because a server doesn't load blockstates, models, or item icons at all; only
a real client does, and this environment has never had one.

### Scope of the gap
A systematic pass (comparing every `RegistryHandler` entry's asset wiring against what actually
exists under `assets/industrialmetallurgy/`) found:
- **126 plain items** (every ingot/nugget/plate/wire/dust/burr-set etc. besides a handful already
  ported) had no `items/<name>.json` or `models/item/<name>.json` at all — meaning until now they
  would have rendered as the missing-texture checkerboard in-game.
- **32 blocks** (all 29 metal blocks, plus the Iron/Steel/Cobalt/Tungsten Forge Cores and Oil
  Sand) were missing their blockstate/block-model/item-model trio entirely — registered,
  craftable, and droppable, but with no visual form.
- **4 blocks** (the four Forge Core tiers) were also missing their `lang` display name.

### The fix: restore, don't redraw
The `origin/1.16.4` branch still has real, hand-drawn textures for nearly all of this — 1.16.4
used `textures/blocks/`/`textures/items/` (plural), 26.2 uses `textures/block/`/`textures/item/`
(singular), which is exactly why a name-for-name comparison hadn't been done before. For every
missing item/block whose name still exists in the current registry, its old texture was pulled
via `git show origin/1.16.4:<path>` and its blockstate/model JSON regenerated from the current
codebase's own established convention (verified byte-for-byte uniform across all 158 recovered
entries: plain items are `item/generated` + a single `layer0` texture; blocks are
`block/cube_all` + a single `all` texture, with the item form parenting the block model). Items
whose old name no longer maps to anything (`cuprite_ore`, `copper_*`, raw `sulfur`, `nequitum_*`
— all deliberately removed in Parts 8–9) were left alone.

Textures that already exist in 26.2 with *deliberately different* art from 1.16.4 — the 13
procedural ore textures (Part 9), the 9 machine GUI/block textures, and the 5-tier tool textures
(Part 6, hue-coded per real alloy) — were left untouched; a byte-level hash comparison confirmed
these are intentional replacements, not stand-ins, so restoring the 1.16.4 originals over them
would have been a regression.

### The genuine gap: Tungsten-Rhenium family + Arc Furnace Core
9 entries have no 1.16.4 equivalent because they didn't exist in 1.16.4 at all — Rhenium and
Tungsten-Rhenium (Part 8) and the Arc Furnace Core (Part 8) are new since the port. These got
fresh placeholder art using the same hue-shift recoloring technique already established in this
codebase (vanilla `iron_ingot`/`iron_nugget`/`iron_block` as the base shape, HSV hue-shifted):
Rhenium's family got a pale steel-blue tint matching its ore's flecks (`rheniite_ore`), and
Tungsten-Rhenium's family reuses the purple already established by its own tool tier
(`tungsten_rhenium_axe` etc.) so the ingot/nugget/block/burr-set read as the same metal. The Arc
Furnace Core reuses the other forge cores' frame-plus-core template, retinted to the Arc
Furnace's own dark-red accent.

### What's still unverified
Same caveat as every other asset change this session: compiling, building, and a dedicated-server
run all confirm clean data load (1857 recipes, zero errors, zero warnings referencing this mod),
and every generated JSON file matches an already-established, presumed-correct sibling pattern
byte-for-byte. None of that confirms how any of this actually **looks** in a real client — texture
alignment, whether the recolor hues read well at 16px, whether `cube_all` is the right model for
every one of these (it matches what 1.16.4 itself used, so this should be safe, but it's still
unverified). That needs real client eyes.

---

## Part 13 — Held-item icon fix, Arc Furnace GUI fix, and JEI integration — **DONE**

Three follow-ups from actually placing/holding the Part 12 blocks in a real client for the first
time this session.

### Held-item icons needed a second file, not just the block model
`models/item/<name>.json` alone renders the block correctly when placed (via the blockstate), but
this MC version also requires an explicit `assets/industrialmetallurgy/items/<name>.json` client
item definition for the *held/inventory* icon -- there's no fallback from one to the other. Every
block that already worked (argentite_ore, the forges, crusher, coke oven, thermoelectric
generator, arc furnace) happened to already have this file; the 32 blocks restored in Part 12
didn't, and neither did 19 pre-existing ore/machine blocks or 3 plain items (`guide_book`,
`oily_sand`, `prospector`) that had silently had the same bug since their own original ports.
Fixed for all 57 at once.

### Arc Furnace's GUI title covering its own energy bar
`AdvancedForgeScreen` (shared by Forge Tier 3, Tier 4, and Arc Furnace) never got the inventory
label repositioning `CrusherScreen` already carries for the exact same reason -- its energy bar
runs from y=8 to y=78, which the default "Inventory" label position (y=72) sits right on top of.
Same one-line fix as Crusher's.

### JEI integration
Added `mezz.jei:jei-26.2-neoforge` (version `30.29.0.199`, confirmed the correct build for this
NeoForge/MC version via Modrinth) as a `localRuntime`/`compileOnly` dependency and a real
`IModPlugin` (`client/jei/`) registering all 7 custom recipe types -- Crusher, Coke Oven,
Forge/Arc Furnace (one shared category, tier-gated, tier drawn on each recipe), Extruder,
Soldering Station, Chemical Centrifuge, Chemical Reactor -- as JEI recipe categories with plain
functional slot-grid layouts (no custom GUI art; this is a spot-check tool, not polish). Electric
Furnace and Thermoelectric Generator aren't custom types (plain vanilla smelting / vanilla fuel
values), so they're just registered as extra catalysts on JEI's own built-in `SMELTING`/
`SMELTING_FUEL` categories instead of duplicating them.

**The one non-obvious part**: this MC version stopped syncing full recipe objects to the client at
all (`ClientboundUpdateRecipesPacket` only carries `RecipePropertySet`s now, for search/ghost-slot
purposes) -- there is no `Level.getRecipeManager()` anymore client-side. Even JEI's own built-in
vanilla categories don't use one; they read back out of JEI's *own* client-synced `RecipeMap`
(`mezz.jei.common.Internal.getClientSyncedRecipes()`), which is the only place a full `RecipeHolder`
list actually exists on the client. There's no public API for this yet, so this plugin does
exactly what JEI's own `VanillaPlugin` does internally: filter that map by `RecipeType` and hand
the results to `IRecipeRegistration.addRecipes`. Confirmed via decompiling `VanillaPlugin` itself
that this is genuinely how JEI, including its own bundled categories, gets recipe data now.

### Follow-up: the categories existed but every one was empty
You confirmed this directly: vanilla recipes (crafting, furnace) showed up in JEI fine, but none
of this mod's own machine recipes did -- e.g. no Crusher recipe for crushed ores at all. Tracked
down by adding a temporary diagnostic (dumping `Internal.getClientSyncedRecipes()`'s contents and
size) and a temporary `--quickPlaySingleplayer` launch arg so a headless run could actually reach
a loaded world, not just the main menu (`registerRecipes` doesn't run until then, since it needs
that synced map). The finding: the synced `RecipeMap` had 1768 entries where the server had 1857
-- a 89-recipe gap that lined up exactly with this mod's total custom-recipe count, and a
class-name scan of the synced map's contents confirmed **zero** of our recipes were in it at all.

The cause: this MC version's client recipe sync isn't automatic for custom recipe types anymore --
`OnDatapackSyncEvent#sendRecipes(RecipeType<?>...)` is an explicit per-mod opt-in NeoForge added to
restore what vanilla's own stripped-down sync no longer carries. Nothing in this mod ever called
it, so all 7 custom recipe types were fully functional in-game but simply never reached the client
at all -- not a JEI-specific problem, and it would have equally broken a future in-mod recipe book.
Fixed with one new class, `util/RecipeSyncHandler`, subscribing to that event and listing all 7
types. Confirmed via the same diagnostic re-run: synced recipe count went from 1768 to the full
1857, Crusher and Forge (previously 0 each) came back at 17 and 31.

### What's actually been verified vs. still unverified
This is the first point in the whole 26.2 rewrite where a real client was ever launched (headless,
via `xvfb-run`, since this environment still has no display), and -- thanks to the
`--quickPlaySingleplayer` trick above -- the first time one actually reached a loaded world rather
than just the main menu. That's how the `OnDatapackSyncEvent` bug above was caught and confirmed
fixed with real data (recipe counts before/after), not a guess. What's still unverified: the
Crusher/Forge recipe *counts* are now confirmed correct, but the JEI category *pages themselves*
-- whether the slot-grid layouts look reasonable, whether the Arc Furnace GUI fix actually clears
the label, whether the recipes are easy to actually find/browse -- still need a real person
looking at the screen.

---

## Part 4 — Basic in-mod logistics (per your last message)

You described wanting something minimal rather than depending on another mod: a machine-side
I/O block, and a plain conduit that connects them. Rough shape for that:

- **I/O port** — a small block (maybe one per side of a machine, or a single block adjacent to a
  machine that exposes its item/energy/fluid capability to the network) that can be configured
  input-only, output-only, or both.
- **Conduit/pipe block** — connects two I/O ports and moves whatever they agree to move. Doesn't
  need to be smart routing on day one — even "round-robin between connected outputs" is enough
  to be useful, matching how BuildCraft's earliest pipes worked before gates/facades existed.
- **Item vs. fluid vs. energy** could realistically be three different conduit types, or one
  conduit type that just moves whatever `IItemHandler`/fluid-handler/energy-handler capability
  it finds on both ends — the latter is less flavorful but a lot less code to maintain.

This also directly answers the fluids-vs-bottles question from before: if a conduit just moves
items, bottled chemicals (`sulfuric_acid_bottle`, `ethylene_bottle`, etc.) work through it with
zero extra effort. Migrating to real fluids would only be worth it if you specifically want tank
blocks, or want a chemical to move without needing a glass bottle as a middleman — worth deciding
once, since it's a bigger commitment than the pipe system itself.

---

## Part 5 — Open questions

1. **Nequitum's fate — resolved, see Part 8.** Replaced outright with Tungsten-Rhenium (Rhenium
   ore/ingot via a new Arc Furnace, alloyed with Tungsten Steel). Every material in the mod is now
   real; nequitum is gone, not just relabeled. Original notes on the options considered, kept for
   history:
   - **Rhenium** — one of the rarest elements in Earth's crust, and specifically the metal used
     in real jet-turbine superalloys for extreme-heat tolerance. Ties in cleanly with the Blast
     Furnace idea above (Part 3) as the *reason* you'd ever need one.
   - **Osmium** — the densest naturally-occurring element, real hard-wearing alloy/contact use.
   - **Rhodium** — genuinely rarer and more expensive than platinum in real life; pure rarity
     flavor if that's the property you want to lean on instead of heat resistance.
   
   Rhenium is my pick if you want the replacement to *justify* an endgame machine rather than
   just be an expensive tool material.

2. **How far to take the logistics system.** Part 4 sketches the minimum viable version; whether
   it grows filters/sorting/multiple conduit types later is worth leaving open rather than
   deciding now.

3. **Placeholder art still owed — mostly resolved, see Part 12.** `oily_sand`, Battery Box, and
   Electric Furnace all turned out to already have real art; the actual gap was ~160 items/blocks
   whose *wiring* (blockstate/model files), not art, never made it over from 1.16.4 — fixed in
   Part 12 by restoring the 1.16.4 originals. One real gap remains: the 4 armor materials
   (Steel/Titanium/Stellite/Tungsten-Rhenium) have no worn-armor layer texture
   (`textures/models/armor/*.png`) in either branch — equipped armor currently renders on the
   player model with no texture at all. This is a distinct asset from the item icon (which each
   armor piece already has) and hasn't been made in either era of the mod.
