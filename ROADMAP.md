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
World generation (ores don't spawn), the steel/nequitum tool lines, and JEI integration.

---

## Part 2 — Item/block usage audit

**Genuinely unused — zero appearances in any recipe, on either branch, ever:**

| Item | Notes |
|---|---|
| `dry_cell` | No recipe produces *or* consumes it. The "starter tier" of the battery progression was apparently never finished even conceptually. |
| `dry_cell_bank` | Same — never produced, never consumed. |
| `battery_bank` | `battery_cell` is now used (the Crusher recipe, ported in Part 1 §1); `battery_bank` still never appears anywhere. |
| `heating_element` | Never produced or consumed. Nichrome/kanthal/nikrothal exist specifically to feed this item and nothing currently does. |
| `induction_core` | Never produced or consumed. |

**Produced, but never consumed by anything** (a recipe outputs them, nothing eats them):

| Item | Produced by | Notes |
|---|---|---|
| `graphite_rod` | Chemical Reactor (`crushed_coal` + `sulfuric_acid_bottle` + `coal_coke`) | Real graphite rods/electrodes are used in high-temperature metallurgy (aluminum smelting, arc furnaces) and as nuclear moderator/control rods. Currently a dead end. |
| `memory_wire` | Extruder (from `nitinol_ingot`) | Real nitinol wire is used for actuators and thermostats. Currently a dead end. |

These 7 items are the real remaining design gap — everything else the original audit flagged as
"blocked only by porting gaps" (every metal's nugget/block forms, all 5 burr sets,
`electric_motor` + `rotor`/`stator`/`field_coil`/`gear`, `refractory_brick`/`refractory_composite`,
`titanium_ingot`/`titanium_block`, `tungsten_carbide_dust`, all 4 forge-core items) is now wired
up per Part 1 and can be removed from the gap list.

---

## Part 3 — Ideas for giving real jobs to underutilized items

Organized by theme. Each ties back to a specific item above and to the real-world property that
justified adding that material in the first place.

### Electric Furnace / Induction Kiln — uses `heating_element`
Nichrome, kanthal, and nikrothal are real resistance-heating alloys — that's specifically why
they exist as three separate alloys rather than one generic "wire." An FE-powered furnace/kiln
that replaces the Coke Oven's solid fuel with a nichrome (or higher-tier kanthal/nikrothal)
heating element as a wearing component would finally give those three alloys — and
`heating_element` itself — their reason to exist, and it's a natural "upgrade path" alongside the
Coke Oven rather than a replacement for it.

### Blast/Arc Furnace tier — uses `refractory_brick`, `refractory_composite`, `graphite_rod`
Real aluminum smelting uses graphite anodes; real high-temperature furnace linings use
refractory brick. Titanium and tungsten realistically need hotter processing than iron/steel do.
A 5th Forge tier (or a distinct "Blast Furnace" machine) that requires refractory brick as
casing and consumes graphite rods as a wearing electrode — gating titanium/tungsten-heavy alloys
behind it — uses the one currently-dead item (`graphite_rod`) and gives titanium a reason to feel
like a distinct tier rather than "steel but renamed."

### Battery Box + rechargeable tools — uses `dry_cell`, `dry_cell_bank`, `battery_bank`, `induction_core`
Right now FE can't be stored or moved anywhere except inside a machine's internal buffer. A
Battery Box (dry cell → dry cell bank → battery bank, mirroring the lithium chain that already
exists and works) fixes that, and gives the entire unused low-tier battery progression a job.
`induction_core` fits naturally as the charging-coil component inside the box (real wireless/
contact charging uses induction coils) — pick whichever framing fits the block's model.

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

### Powered tools — uses `electric_motor`, `gear`, battery items, `memory_wire`
An electric drill/chainsaw line, separate from the plain steel/nequitum hand tools, rechargeable
from the battery chain above. `memory_wire`'s real-world actuator use fits naturally as a
self-resetting trigger/latch component — e.g. an auto-return mechanism, or (tying into Part 4)
a shutter/valve actuator for pipes.

### Titanium tools/armor tier
Titanium now feeds `nitinol_ingot` and the Chemical Centrifuge's specialty component, but real
titanium's whole reputation is "strong but light" — still a natural slot between steel and
nequitum tools/armor, using a material you already mine toward instead of adding something new.

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

1. **Nequitum's fate.** Every other material in the mod is real; nequitum is the one deliberate
   "unobtainium." If it should be replaced with something grounded, a few real candidates that
   would keep the "endgame capstone metal" flavor without duplicating IC2's iridium:
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

3. **`oily_sand`'s texture** is a placeholder (reused from the `oil_sand` block). Needs real art
   whenever that's convenient.
