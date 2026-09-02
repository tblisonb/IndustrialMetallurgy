package com.onlytanner.industrialmetallurgy.items.guide;

import java.util.List;

// Generated from GUIDE.md by tools/guide_book/gen_guide_data.py -- edit GUIDE.md and
// re-run that script rather than hand-editing this file; changes made here directly
// will be overwritten the next time it runs.
public final class GuideBookData {

    public static final List<GuideCategory> CATEGORIES = List.of(
        new GuideCategory("Getting Started", List.of(
            new GuideEntry("The Core Loop", List.of(
                    "Nearly everything here follows one chain: ore becomes crushed ore, crushed ore becomes an ingot — sometimes several, alloyed together — and ingots become tools, armor, or the machines that do the next step for you.",
                    "There's no single \"right\" order to build things in. But there is a first machine."
            )),
            new GuideEntry("Your First Machine", List.of(
                    "That first machine is the **Crusher**. It turns raw ore into crushed ore at a better yield than smelting ore directly — nearly everything downstream wants crushed ore eventually.",
                    "Every powered machine (Crusher included) shares one crafting shape: a `conducting_element`/`gear`/`electric_motor`/`steel_plate`/`battery_cell` housing, plus one \"specialty\" part that determines what the machine actually does.",
                    "None of those sub-parts need a machine to make — they're craftable by hand from ore, a furnace, and a crafting table. Your first machine is never blocked on a machine you don't have yet."
            )),
            new GuideEntry("Powering Up", List.of(
                    "Every powered machine runs on Forge Energy (FE), the standard most tech mods share.",
                    "Your earliest FE source is the **Thermoelectric Generator** — it burns furnace fuel and pairs copper with constantan, a real thermocouple, to produce power the way a real one does.",
                    "For portable or buffered power, build a **Battery Box**. It discharges battery items — from a dry cell up to a lithium-ion pack — into a shared FE buffer, and pushes that power outward."
            ))
        )),
        new GuideCategory("Ores & Exploration", List.of(
            new GuideEntry("Reading the Land", List.of(
                    "Every ore here favors one specific biome, on purpose. It turns \"go find more ore\" into \"go explore somewhere new,\" instead of \"strip-mine downward until something turns up.\"",
                    "Plains hides Argentite (silver) and Sphalerite (zinc). Jungles hide Bauxite (aluminum) and Cassiterite (tin). Deserts hide Rutile (titanium).",
                    "Forests hide Galena (lead) and Garnierite (nickel). Swamps hide Pyrolusite (manganese). The Nether and the End each hide two more, and the ocean floor hides oil sand."
            )),
            new GuideEntry("Ore Tiers", List.of(
                    "Ores are gated by tier too, matching real mining-tool progression: Tier 1 sits shallow and common, Tier 2 a little deeper and rarer, on up to Tier 4.",
                    "Rheniite — the rarest ore in the mod — only spawns in Basalt Deltas, not the whole Nether. Real rhenium has only ever been found in a single volcanic fumarole.",
                    "Higher tiers need a better pickaxe (or a Power Drill with a higher-tier bit) to even harvest. Tier gates both where you'll find a metal, and what you need in hand to mine it."
            )),
            new GuideEntry("The Prospector", List.of(
                    "Once you know roughly where an ore should be, the **Prospector** finds the exact vein. Socket a sample — a raw or crushed ore item, or raw Lepidolite — then right-click to sweep the area.",
                    "It reports distance, direction, and whether the ore's above or below you. A scan costs charge from a socketed battery, so it's for confirming a hunch, not wall-hacking a whole mountain.",
                    "If nothing's in range, that's real information too — you're probably in the wrong biome, or the wrong dimension, for whatever sample you loaded."
            ))
        )),
        new GuideCategory("Alloys & Materials", List.of(
            new GuideEntry("Why Real Alloys", List.of(
                    "Every alloy here is a real one, chosen for the real property that makes it useful.",
                    "If two metals are combined, it's because real metallurgists combine them for a reason — usually why the result has the role it does in-game."
            )),
            new GuideEntry("Structural & Tool Alloys", List.of(
                    "Steel is the baseline: iron-tier harvesting, well ahead of iron on durability. Cobalt Steel trades a little durability for real cobalt tool-steel's edge-retention at high heat.",
                    "Stellite is a cobalt-chromium-tungsten superalloy, hard enough to jump the harvest gate to diamond-tier. Tungsten Steel is denser still, nearly matching netherite.",
                    "Tungsten-Rhenium — the capstone — is what real rocket nozzles are made from. It never wears out, the same way a tool this over-engineered basically wouldn't."
            )),
            new GuideEntry("Electrical & Heat Alloys", List.of(
                    "Brass and Bronze are the classic copper alloys, used wherever a machine needs a durable part. Constantan exists to pair with copper — a real thermocouple pairing.",
                    "Nichrome, Nikrothal, and Kanthal are real resistance-wire alloys. They're what `resistance_wire`, and downstream the Electric Furnace's `heating_element`, are built from."
            ))
        )),
        new GuideCategory("Processing Machines", List.of(
            new GuideEntry("Crusher", List.of(
                    "Turns raw ore into crushed ore at a better yield than smelting it directly, using a socketed burr set that wears down with use.",
                    "Burr sets come in five tiers — Brass, Steel, Chromium, Tungsten Carbide, then Tungsten-Rhenium, which never wears out at all."
            )),
            new GuideEntry("Coke Oven", List.of(
                    "A solid-fuel machine — no FE needed — that cokes coal or charcoal into `coal_coke`, a real industrial fuel-refining step.",
                    "It also houses a vanilla furnace internally, so it doubles as an early smelter needing no power at all."
            )),
            new GuideEntry("The Forge Line", List.of(
                    "Forge Tiers 1 through 4, plus the Arc Furnace as a 5th tier, are the same machine at heart: multi-ingredient alloy recipes gated by tier, so a Tier 3 recipe needs a Tier 3+ forge.",
                    "Early tiers burn solid fuel; Tier 3 and up run on FE instead. The Arc Furnace refines crushed ore into ingots at a real 50% yield bonus over the Electric Furnace.",
                    "It's also the *only* place Rhenium can be smelted — real rhenium's melting point is the second-highest of any element, so it needed the hottest furnace in the mod to exist at all."
            )),
            new GuideEntry("Electric Furnace", List.of(
                    "Runs ordinary vanilla smelting recipes on FE instead of fuel — every ore's existing smelting recipe just works here for free.",
                    "It needs a `heating_element` installed, a slot item that wears out over roughly 200 smelts, the same way a real furnace's heating coil eventually needs replacing."
            )),
            new GuideEntry("Extruder", List.of(
                    "Shapes ingots into plates and draws them into wire — real cold-forming processes, not just a recipe distinction. Plates feed casings and structural parts; wire feeds anything electrical."
            )),
            new GuideEntry("Soldering Station", List.of(
                    "Where electronics get assembled: capacitors, circuit boards, integrated circuits, and the lithium battery cells that power every Power Tool's battery pack."
            )),
            new GuideEntry("Chemical Centrifuge & Chemical Reactor", List.of(
                    "The Centrifuge separates things apart — crushed ore into its metal plus real byproducts (sulfur, arsenic, and more depending on the ore), oily sand into bitumen, sand, and clay.",
                    "The Reactor does the opposite: it synthesizes reagents into acids, plastics, and other processed chemicals. Between the two, this is the mod's actual chemistry set."
            ))
        )),
        new GuideCategory("Power", List.of(
            new GuideEntry("Forge Energy Basics", List.of(
                    "Every powered machine here speaks Forge Energy (FE), the standard most tech mods share — a cable or item that moves FE for another mod moves it for this one too."
            )),
            new GuideEntry("Thermoelectric Generator", List.of(
                    "Your earliest FE source: it burns furnace fuel, and its recipe pairs copper with constantan — a real thermocouple, and exactly why constantan exists as its own alloy here."
            )),
            new GuideEntry("Battery Box & Batteries", List.of(
                    "Discharges a battery item into a shared FE buffer and pushes that power outward, the same role a battery bank fills off-grid.",
                    "It's also where a Power Tool's Battery Pack recharges — set a drained pack in its charging slot and it tops back up at the same rate the box feeds neighboring machines."
            ))
        )),
        new GuideCategory("Tools, Armor & Power Tools", List.of(
            new GuideEntry("Hand Tools & Armor", List.of(
                    "Five tool tiers (Steel through Tungsten-Rhenium) and four armor sets (Steel, Titanium, Stellite, Tungsten-Rhenium), each earning its slot for a real reason.",
                    "Titanium armor gives Resistance for being strong-but-light. Stellite gives Fire Resistance for being heat-resistant. Tungsten-Rhenium, the capstone, carries both at once."
            )),
            new GuideEntry("Power Tools", List.of(
                    "Power Drill, Chainsaw, and Cultivator are each crafted once, sharing two live sockets instead of needing five more tool tiers: an implement that sets what they do, and a Battery Pack that powers them.",
                    "Left-click a valid item onto the tool to install it; right-click with an empty hand to pull it back out. The Prospector reuses that same socket system for a sample instead of an implement."
            ))
        )),
        new GuideCategory("The Endgame", List.of(
            new GuideEntry("Arc Furnace & Rhenium", List.of(
                    "The Arc Furnace is a 5th Forge tier, and the only furnace hot enough to smelt Rhenium at all — real rhenium has the second-highest melting point of any element.",
                    "Rheniite, its ore, was discovered in a single volcanic fumarole in 1994 — also why it's the rarest, most narrowly-gated ore in the mod."
            )),
            new GuideEntry("Tungsten-Rhenium", List.of(
                    "The mod's capstone material: Tungsten Steel alloyed with Rhenium, using a graphite rod as a real superalloy-processing electrode.",
                    "It's what real rocket nozzles and high-temperature thermocouples are made from — and like every Tungsten-Rhenium tool and armor piece, it never wears out."
            ))
        ))
    );

    private GuideBookData() {}

}
