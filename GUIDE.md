# The Metallurgist's Companion

This is the source text for Industrial Metallurgy's in-game guide book (crafted from a
`minecraft:book` + an `minecraft:iron_nugget`, see `items/GuideBookItem.java`). It's written and
maintained here first; `GuideBookContent.java` is a direct, page-for-page translation of this
document, generated from it (see `tools/guide_book/` for the one-time conversion script) rather
than hand-duplicated, so the two shouldn't drift apart as long as edits are made here and
re-run through the script.

Each `<!-- page -->` marker below is a deliberate in-book page break — the vanilla book UI this
guide renders through has a hard limit of roughly 256 characters (about 14 lines) per page, so
page boundaries are chosen by hand, and pages are kept short and punchy rather than dense.
`##` headings are guide categories (the book's table-of-contents entries); `###` headings are
individual articles within a category.

---

## Getting Started

### The Core Loop

Nearly everything here follows one chain: ore becomes crushed ore, crushed ore becomes an
ingot — sometimes several, alloyed together — and ingots become tools, armor, or the machines
that do the next step for you.
<!-- page -->
There's no single "right" order to build things in. But there is a first machine.

### Your First Machine

That first machine is the **Crusher**. It turns raw ore into crushed ore at a better yield than
smelting ore directly — nearly everything downstream wants crushed ore eventually.
<!-- page -->
Every powered machine (Crusher included) shares one crafting shape: a
`conducting_element`/`gear`/`electric_motor`/`steel_plate`/`battery_cell` housing, plus one
"specialty" part that determines what the machine actually does.
<!-- page -->
None of those sub-parts need a machine to make — they're craftable by hand from ore, a furnace,
and a crafting table. Your first machine is never blocked on a machine you don't have yet.

### Powering Up

Every powered machine runs on Forge Energy (FE), the standard most tech mods share.
<!-- page -->
Your earliest FE source is the **Thermoelectric Generator** — it burns furnace fuel and pairs
copper with constantan, a real thermocouple, to produce power the way a real one does.
<!-- page -->
For portable or buffered power, build a **Battery Box**. It discharges battery items — from a
dry cell up to a lithium-ion pack — into a shared FE buffer, and pushes that power outward.

---

## Ores & Exploration

### Reading the Land

Every ore here favors one specific biome, on purpose. It turns "go find more ore" into "go
explore somewhere new," instead of "strip-mine downward until something turns up."
<!-- page -->
Plains hides Argentite (silver) and Sphalerite (zinc). Jungles hide Bauxite (aluminum) and
Cassiterite (tin). Deserts hide Rutile (titanium).
<!-- page -->
Forests hide Galena (lead) and Garnierite (nickel). Swamps hide Pyrolusite (manganese). The
Nether and the End each hide two more, and the ocean floor hides oil sand.

### Ore Tiers

Ores are gated by tier too, matching real mining-tool progression: Tier 1 sits shallow and
common, Tier 2 a little deeper and rarer, on up to Tier 4.
<!-- page -->
Rheniite — the rarest ore in the mod — only spawns in Basalt Deltas, not the whole Nether. Real
rhenium has only ever been found in a single volcanic fumarole.
<!-- page -->
Higher tiers need a better pickaxe (or a Power Drill with a higher-tier bit) to even harvest.
Tier gates both where you'll find a metal, and what you need in hand to mine it.

### The Prospector

Once you know roughly where an ore should be, the **Prospector** finds the exact vein. Socket a
sample — a raw or crushed ore item, or raw Lepidolite — then right-click to sweep the area.
<!-- page -->
It reports distance, direction, and whether the ore's above or below you. A scan costs charge
from a socketed battery, so it's for confirming a hunch, not wall-hacking a whole mountain.
<!-- page -->
If nothing's in range, that's real information too — you're probably in the wrong biome, or the
wrong dimension, for whatever sample you loaded.

---

## Alloys & Materials

### Why Real Alloys

Every alloy here is a real one, chosen for the real property that makes it useful.
<!-- page -->
If two metals are combined, it's because real metallurgists combine them for a reason — usually
why the result has the role it does in-game.

### Structural & Tool Alloys

Steel is the baseline: iron-tier harvesting, well ahead of iron on durability. Cobalt Steel
trades a little durability for real cobalt tool-steel's edge-retention at high heat.
<!-- page -->
Stellite is a cobalt-chromium-tungsten superalloy, hard enough to jump the harvest gate to
diamond-tier. Tungsten Steel is denser still, nearly matching netherite.
<!-- page -->
Tungsten-Rhenium — the capstone — is what real rocket nozzles are made from. It never wears
out, the same way a tool this over-engineered basically wouldn't.

### Electrical & Heat Alloys

Brass and Bronze are the classic copper alloys, used wherever a machine needs a durable part.
Constantan exists to pair with copper — a real thermocouple pairing.
<!-- page -->
Nichrome, Nikrothal, and Kanthal are real resistance-wire alloys. They're what
`resistance_wire`, and downstream the Electric Furnace's `heating_element`, are built from.

---

## Processing Machines

### Crusher

Turns raw ore into crushed ore at a better yield than smelting it directly, using a socketed
burr set that wears down with use.
<!-- page -->
Burr sets come in five tiers — Brass, Steel, Chromium, Tungsten Carbide, then Tungsten-Rhenium,
which never wears out at all.

### Coke Oven

A solid-fuel machine — no FE needed — that cokes coal or charcoal into `coal_coke`, a real
industrial fuel-refining step.
<!-- page -->
It also houses a vanilla furnace internally, so it doubles as an early smelter needing no power
at all.

### The Forge Line

Forge Tiers 1 through 4, plus the Arc Furnace as a 5th tier, are the same machine at heart:
multi-ingredient alloy recipes gated by tier, so a Tier 3 recipe needs a Tier 3+ forge.
<!-- page -->
Early tiers burn solid fuel; Tier 3 and up run on FE instead. The Arc Furnace refines crushed
ore into ingots at a real 50% yield bonus over the Electric Furnace.
<!-- page -->
It's also the *only* place Rhenium can be smelted — real rhenium's melting point is the
second-highest of any element, so it needed the hottest furnace in the mod to exist at all.

### Electric Furnace

Runs ordinary vanilla smelting recipes on FE instead of fuel — every ore's existing smelting
recipe just works here for free.
<!-- page -->
It needs a `heating_element` installed, a slot item that wears out over roughly 200 smelts, the
same way a real furnace's heating coil eventually needs replacing.

### Extruder

Shapes ingots into plates and draws them into wire — real cold-forming processes, not just a
recipe distinction. Plates feed casings and structural parts; wire feeds anything electrical.

### Soldering Station

Where electronics get assembled: capacitors, circuit boards, integrated circuits, and the
lithium battery cells that power every Power Tool's battery pack.

### Chemical Centrifuge & Chemical Reactor

The Centrifuge separates things apart — crushed ore into its metal plus real byproducts (sulfur,
arsenic, and more depending on the ore), oily sand into bitumen, sand, and clay.
<!-- page -->
The Reactor does the opposite: it synthesizes reagents into acids, plastics, and other processed
chemicals. Between the two, this is the mod's actual chemistry set.

---

## Power

### Forge Energy Basics

Every powered machine here speaks Forge Energy (FE), the standard most tech mods share — a
cable or item that moves FE for another mod moves it for this one too.

### Thermoelectric Generator

Your earliest FE source: it burns furnace fuel, and its recipe pairs copper with constantan — a
real thermocouple, and exactly why constantan exists as its own alloy here.

### Battery Box & Batteries

Discharges a battery item into a shared FE buffer and pushes that power outward, the same role a
battery bank fills off-grid.
<!-- page -->
It's also where a Power Tool's Battery Pack recharges — set a drained pack in its charging slot
and it tops back up at the same rate the box feeds neighboring machines.

---

## Tools, Armor & Power Tools

### Hand Tools & Armor

Five tool tiers (Steel through Tungsten-Rhenium) and four armor sets (Steel, Titanium, Stellite,
Tungsten-Rhenium), each earning its slot for a real reason.
<!-- page -->
Titanium armor gives Resistance for being strong-but-light. Stellite gives Fire Resistance for
being heat-resistant. Tungsten-Rhenium, the capstone, carries both at once.

### Power Tools

Power Drill, Chainsaw, and Cultivator are each crafted once, sharing two live sockets instead of
needing five more tool tiers: an implement that sets what they do, and a Battery Pack that
powers them.
<!-- page -->
Left-click a valid item onto the tool to install it; right-click with an empty hand to pull it
back out. The Prospector reuses that same socket system for a sample instead of an implement.

---

## The Endgame

### Arc Furnace & Rhenium

The Arc Furnace is a 5th Forge tier, and the only furnace hot enough to smelt Rhenium at all —
real rhenium has the second-highest melting point of any element.
<!-- page -->
Rheniite, its ore, was discovered in a single volcanic fumarole in 1994 — also why it's the
rarest, most narrowly-gated ore in the mod.

### Tungsten-Rhenium

The mod's capstone material: Tungsten Steel alloyed with Rhenium, using a graphite rod as a
real superalloy-processing electrode.
<!-- page -->
It's what real rocket nozzles and high-temperature thermocouples are made from — and like every
Tungsten-Rhenium tool and armor piece, it never wears out.
