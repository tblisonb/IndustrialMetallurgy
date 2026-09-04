# Industrial Metallurgy — Roadmap

This document tracks what **isn't** built yet: open design questions, unimplemented content, and
future ideas. It is not a changelog or a design-decision archive — completed work isn't
documented here once it ships. For that:

- **`README.md`** — the current, up-to-date feature list.
- **`GUIDE.md`** — the player-facing explanation of how everything works, sourced into the
  in-game guide book.
- **`git log`** — the actual history: what changed, when, and why. Every past "Part" of this
  mod's development, including the reasoning behind specific design calls, lives in commit
  messages rather than in this file, since it's already versioned there and this file would just
  be duplicating it.

When an item below gets built, move what's genuinely still worth knowing into `README.md`/
`GUIDE.md` and delete it from here rather than marking it "done" in place.

---

## Open ideas

1. **A real fluid system.** Liquids are currently represented as one-shot bottle items
   (`sulfuric_acid_bottle`, the Autoclave's leach-solution bottles, `ethylene_glycol_bottle`,
   etc.), not NeoForge fluid capability (tanks, `IFluidHandler`/`FluidStack`). The Autoclave
   (crushed ore → leach solution → concentrate) is the first genuinely fluid-shaped process in
   the mod, which is a real reason to eventually build this — previously there was no
   fluid-producing machine to justify it at all. Comparable in scope to the item-transfer-API
   migration. The Conduit/I-O Port pair would extend to carry a third resource the same way it
   already carries energy and items, rather than needing a dedicated Fluid Conduit block.

2. **Extend Autoclave leaching to Chromite and Scheelite.** Left out of the initial leaching
   chain — chromite's real route is alkaline roasting and scheelite's is alkaline autoclave
   digestion, both less clean fits than the 7 ores already covered, and neither was asked for.
   Revisit if there's appetite to close out the full ore roster.

3. **Find a use for Calcium Sulfate.** A real byproduct of the Autoclave/Chemical Reactor
   leaching chain (gypsum, from neutralizing a sulfate leach solution with calcium oxide), not
   consumed by anything yet. Two ideas on the table: decorative blocks (real gypsum is the basis
   of plaster/drywall), or a future Cultivator fertilizer input.

4. **Convert Forge Tiers 1-4 to the multiblock framework.** The Arc Furnace is converted — a
   3x3x3 Tungsten Steel Block shell around a Tungsten-Rhenium Block core, with the existing
   single-block Arc Furnace as the controller on the front face. Tiers 1-4 are still plain
   single-block machines. Extending the same idea to them means real per-tier design work —
   would each tier want its own shell material/shape (scaling structure size with tier, say), or
   share one — deliberately left open rather than decided as a side effect of doing the Arc
   Furnace first.

5. **More energy-generating machines.** Currently just the Solar Panel and Thermoelectric
   Generator. Intentionally left fuzzy — worth a real research/brainstorm pass on what's actually
   missing before designing anything.

6. **Powered armor (exoskeleton).** A follow-up to the power tool line (Power Drill/Chainsaw/
   Cultivator/Prospector) — an FE-powered armor set, distinct from Tungsten-Rhenium's passive set
   bonus. Its own design surface (per-slot effects, FE drain rates, what makes it meaningfully
   different from just another armor tier) — not started.
