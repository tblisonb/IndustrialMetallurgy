package com.onlytanner.industrialmetallurgy.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Fluent builder for a {@link MultiblockPattern}, authored as a stack of aisles (Y layers,
 * bottom to top). Each aisle is one or more equal-length rows (Z, front to back); each
 * character in a row is one X column. Symbols are bound to predicates with {@link #where};
 * a space always means "don't care, no check performed here". Exactly one cell must carry the
 * controller symbol (default {@code 'C'}, see {@link #controllerSymbol}) -- every other cell's
 * offset is computed relative to it, and the whole pattern is later matched against the world
 * rotated onto wherever the actual controller block is facing.
 *
 * <pre>{@code
 * MultiblockPattern pattern = new MultiblockPatternBuilder()
 *         .aisle("###", "#C#", "###")   // floor layer, controller in the middle
 *         .aisle("# #", "# #", "# #")   // walls, open front/back
 *         .aisle("###", "###", "###")   // roof
 *         .where('#', Blocks.BRICKS)
 *         .build();
 * }</pre>
 */
public final class MultiblockPatternBuilder {

    private final List<String[]> aisles = new ArrayList<>();
    private final Map<Character, MultiblockPattern.Cell> symbols = new HashMap<>();
    private char controllerSymbol = 'C';

    public MultiblockPatternBuilder aisle(String... rows) {
        if (rows.length == 0) {
            throw new IllegalArgumentException("An aisle needs at least one row");
        }
        int width = rows[0].length();
        for (String row : rows) {
            if (row.length() != width) {
                throw new IllegalArgumentException("All rows in an aisle must be the same length: " + String.join(",", rows));
            }
        }
        this.aisles.add(rows);
        return this;
    }

    /** Binds a symbol to a single required block, using that block's own name in mismatch diagnostics (see {@link MultiblockPattern#findFirstMismatch}). */
    public MultiblockPatternBuilder where(char symbol, Block block) {
        return where(symbol, MultiblockPredicates.of(block), block.getName());
    }

    /** Binds a symbol to an arbitrary predicate, with no name available for mismatch diagnostics. */
    public MultiblockPatternBuilder where(char symbol, Predicate<BlockState> predicate) {
        return where(symbol, predicate, null);
    }

    public MultiblockPatternBuilder where(char symbol, Predicate<BlockState> predicate, Component name) {
        this.symbols.put(symbol, new MultiblockPattern.Cell(predicate, name));
        return this;
    }

    public MultiblockPatternBuilder controllerSymbol(char symbol) {
        this.controllerSymbol = symbol;
        return this;
    }

    public MultiblockPattern build() {
        if (this.aisles.isEmpty()) {
            throw new IllegalStateException("Multiblock pattern has no aisles");
        }

        BlockPos controllerOffset = findController();

        Map<BlockPos, MultiblockPattern.Cell> cells = new HashMap<>();
        for (int y = 0; y < this.aisles.size(); y++) {
            String[] rows = this.aisles.get(y);
            for (int z = 0; z < rows.length; z++) {
                String row = rows[z];
                for (int x = 0; x < row.length(); x++) {
                    char symbol = row.charAt(x);
                    if (symbol == ' ') {
                        continue;
                    }
                    MultiblockPattern.Cell cell = symbol == this.controllerSymbol
                            ? this.symbols.getOrDefault(symbol, new MultiblockPattern.Cell(MultiblockPredicates.any(), null))
                            : this.symbols.get(symbol);
                    if (cell == null) {
                        throw new IllegalStateException("No predicate registered for symbol '" + symbol + "' -- call where('" + symbol + "', ...)");
                    }
                    cells.put(new BlockPos(x, y, z).subtract(controllerOffset), cell);
                }
            }
        }
        return new MultiblockPattern(cells);
    }

    private BlockPos findController() {
        BlockPos found = null;
        for (int y = 0; y < this.aisles.size(); y++) {
            String[] rows = this.aisles.get(y);
            for (int z = 0; z < rows.length; z++) {
                int x = rows[z].indexOf(this.controllerSymbol);
                if (x < 0) {
                    continue;
                }
                if (found != null) {
                    throw new IllegalStateException("Multiblock pattern has more than one controller symbol '" + this.controllerSymbol + "'");
                }
                found = new BlockPos(x, y, z);
            }
        }
        if (found == null) {
            throw new IllegalStateException("Multiblock pattern never places its controller symbol '" + this.controllerSymbol + "'");
        }
        return found;
    }
}
