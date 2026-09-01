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

## Part 1 — Recipe & progression gaps

These aren't new ideas, they're unfinished porting work — content the 1.16.4 branch already
had that never made it to `26.2`. Ordered by how much they block actually playing the mod:

### 1. You currently can't survival-craft *any* machine
Crusher, Coke Oven, Thermoelectric Generator, and all 4 Forge tiers have zero crafting recipe
in `26.2` — they only exist via creative mode right now. On 1.16.4 this was **already true for
Coke Oven, Thermoelectric Generator, and all 4 forges** (they never had a recipe, even there) —
but the **Crusher did**, and it's a good example of why this matters:

```
sps       s = conducting_element   p = steel_plate
gmg       g = gear                 m = electric_motor
pbp       b = battery_cell
```

That recipe alone means the `electric_motor` assembly line (rotor + stator + field_coil + gear,
plus `battery_cell`) isn't optional polish — it's a hard prerequisite for building the *first*
machine in the mod. Porting `crusher.json` means porting that whole chain with it. Extending the
same treatment (a real recipe, ideally scaling in ingredient tier) to Coke Oven, Thermoelectric
Generator, and the 4 Forges is genuinely new design work, not a straight port, since 1.16.4 never
defined them.

### 2. Smelting is entirely missing (0 / 27 recipes)
Every base metal's ore→ingot and crushed-ore→ingot furnace recipes are unported. Concretely,
right now: mining aluminum/chromium/cobalt/copper/lead/manganese/nickel/silver/tin/titanium/
tungsten/zinc ore and putting it in a furnace does nothing. This is the most foundational gap —
nothing downstream of raw ore works without it.

### 3. Basic crafting is entirely missing (0 / ~100 recipes)
Ingot↔nugget↔block conversions for all ~27 metals/alloys, the 5 burr sets, and the electric
motor sub-assembly (`rotor`, `stator`, `field_coil` → `electric_motor`) all need porting. None
of this is design work — it's the exact same recipes 1.16.4 already had, just needing the new
`crafting_shaped`/`crafting_shapeless` JSON format.

### 4. The Crusher itself is only 1/16 recipes ported
Only `crushed_argentite_ore` exists. Missing: `crushed_bauxite_ore`, `crushed_cassiterite_ore`,
`crushed_chromite_ore`, `crushed_cobaltite_ore`, `crushed_cuprite_ore`, `crushed_diamond`,
`crushed_galena_ore`, `crushed_garnierite_ore`, `crushed_gold_ore`, `crushed_iron_ore`,
`crushed_pyrolusite_ore`, `crushed_rutile_ore`, `crushed_scheelite_ore`,
`crushed_sphalerite_ore`, and the two carbon-dust-from-coal/charcoal recipes. Combined with gap
#2, this means **10 of the 14 crushed ores currently have nowhere to go** even once produced.

### 5. `oil_sand` → `bitumen` has no path at all, in either branch
This one isn't a porting gap, it's a real hole: `oil_sand`'s loot table just drops itself, and no
recipe (crusher or otherwise) has ever turned it into `bitumen`, even though the README already
describes oil sand as "a bitumen source." Compare `lepidolite_ore`, which does this correctly —
silk touch gives the ore block, a normal break gives the `lepidolite` item directly via loot
table (no recipe needed). Oil sand should probably work the same way (silk touch → block,
otherwise → `bitumen`, maybe with a fortune-scaled roll for partial yield).

### 6. World generation, tools, JEI
Already tracked in `README.md`'s status section — ores don't spawn, the steel/nequitum tool
lines are deferred, JEI isn't reconnected. Not repeating detail here.

---

## Part 2 — Item/block usage audit

**Genuinely unused — zero appearances in any recipe, on either branch, ever:**

| Item | Notes |
|---|---|
| `dry_cell` | No recipe produces *or* consumes it. The "starter tier" of the battery progression was apparently never finished even conceptually. |
| `dry_cell_bank` | Same — never produced, never consumed. |
| `battery_bank` | `battery_cell` is at least used once (the Crusher recipe, see Part 1 §1); `battery_bank` never appears anywhere. |
| `heating_element` | Never produced or consumed. Nichrome/kanthal/nikrothal exist specifically to feed this item and nothing currently does. |
| `induction_core` | Never produced or consumed. |

**Produced, but never consumed by anything** (a recipe outputs them, nothing eats them):

| Item | Produced by | Notes |
|---|---|---|
| `graphite_rod` | Chemical Reactor (`crushed_coal` + `sulfuric_acid_bottle` + `coal_coke`) | Real graphite rods/electrodes are used in high-temperature metallurgy (aluminum smelting, arc furnaces) and as nuclear moderator/control rods. Currently a dead end. |
| `memory_wire` | Extruder (from `nitinol_ingot`) | Real nitinol wire is used for actuators and thermostats. Currently a dead end. |

**Blocked only by Part 1's porting gaps** (these *do* have a defined use, it's just not wired up
yet — not a design gap): every metal's nugget/block forms, all 5 burr sets, `electric_motor` +
`rotor`/`stator`/`field_coil`/`gear` (motor assembly + Crusher recipe), `refractory_brick`/
`refractory_composite` (2 recipe appearances each on 1.16.4), `titanium_ingot`/`titanium_block`
(feeds `nitinol_ingot` plus its own storage forms), `tungsten_carbide_dust` (feeds the tungsten
carbide burr set).

**Fine as-is, not a gap:** `lepidolite_ore` looked unused by the same grep, but that's because it
correctly drops the `lepidolite` item directly via loot table (silk touch gives the block,
fortune scales a normal break) rather than going through a recipe — same pattern `oil_sand`
should probably use for `bitumen` (Part 1 §5).

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
behind it — uses three currently-dead items and gives titanium a reason to feel like a distinct
tier rather than "steel but renamed."

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
something else. An efficiency upgrade/component for the existing Thermoelectric Generator built
from constantan would close a loop the mod has already half-opened.

### Powered tools — uses `electric_motor`, `gear`, battery items, `memory_wire`
An electric drill/chainsaw line, separate from the plain steel/nequitum hand tools, rechargeable
from the battery chain above. `memory_wire`'s real-world actuator use fits naturally as a
self-resetting trigger/latch component — e.g. an auto-return mechanism, or (tying into Part 4)
a shutter/valve actuator for pipes.

### Titanium tools/armor tier
Titanium currently only feeds `nitinol_ingot`. Real titanium's whole reputation is "strong but
light" — a natural slot between steel and nequitum tools/armor, using a material you already mine
toward instead of adding something new.

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
