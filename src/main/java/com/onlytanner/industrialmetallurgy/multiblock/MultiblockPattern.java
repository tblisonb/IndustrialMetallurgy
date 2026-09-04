package com.onlytanner.industrialmetallurgy.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A multiblock structure, authored as block positions relative to a controller, each paired with
 * a predicate the world's block state there must satisfy (and, optionally, a human-readable name
 * for that predicate -- see {@link Cell}). Authored assuming the controller faces
 * {@link Direction#NORTH}; {@link #matches} rotates the pattern onto the controller's actual
 * horizontal facing before checking the world, reusing vanilla's own {@link Rotation}/
 * {@link BlockPos#rotate} rather than hand-rolling rotation math.
 *
 * Build one with {@link MultiblockPatternBuilder}.
 */
public final class MultiblockPattern {

    /** One structure cell: what the world state there must satisfy, and (if known) what to call it in a diagnostic message. */
    public record Cell(Predicate<BlockState> predicate, @Nullable Component name) {
    }

    /** A cell that failed to match, resolved to its actual world position. */
    public record Mismatch(BlockPos pos, @Nullable Component expectedName) {
    }

    private final Map<BlockPos, Cell> cells;

    MultiblockPattern(Map<BlockPos, Cell> cells) {
        this.cells = Map.copyOf(cells);
    }

    /** True if every cell of this pattern is satisfied in {@code level}, around a controller at {@code controllerPos} facing {@code facing}. */
    public boolean matches(Level level, BlockPos controllerPos, Direction facing) {
        return findFirstMismatch(level, controllerPos, facing).isEmpty();
    }

    /** The first cell (relative to a controller at {@code controllerPos} facing {@code facing}) that fails to match, or empty if the pattern is fully satisfied. */
    public Optional<Mismatch> findFirstMismatch(Level level, BlockPos controllerPos, Direction facing) {
        Rotation rotation = rotationFor(facing);
        for (Map.Entry<BlockPos, Cell> entry : this.cells.entrySet()) {
            BlockPos worldPos = controllerPos.offset(entry.getKey().rotate(rotation));
            if (!entry.getValue().predicate().test(level.getBlockState(worldPos))) {
                return Optional.of(new Mismatch(worldPos, entry.getValue().name()));
            }
        }
        return Optional.empty();
    }

    /** Number of cells this pattern checks (not counting the controller's own position). */
    public int size() {
        return this.cells.size();
    }

    private static Rotation rotationFor(Direction facing) {
        return switch (facing) {
            case NORTH -> Rotation.NONE;
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> throw new IllegalArgumentException("Multiblock controller facing must be horizontal, was " + facing);
        };
    }
}
