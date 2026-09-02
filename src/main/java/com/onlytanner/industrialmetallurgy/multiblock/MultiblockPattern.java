package com.onlytanner.industrialmetallurgy.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Predicate;

/**
 * A multiblock structure, authored as block positions relative to a controller block, each
 * paired with a predicate the world's block state there must satisfy. Authored assuming the
 * controller faces {@link Direction#NORTH}; {@link #matches} rotates the pattern onto the
 * controller's actual horizontal facing before checking the world, reusing vanilla's own
 * {@link Rotation}/{@link BlockPos#rotate} rather than hand-rolling rotation math.
 *
 * Build one with {@link MultiblockPatternBuilder}.
 */
public final class MultiblockPattern {

    private final Map<BlockPos, Predicate<BlockState>> cells;

    MultiblockPattern(Map<BlockPos, Predicate<BlockState>> cells) {
        this.cells = Map.copyOf(cells);
    }

    /** True if every cell of this pattern is satisfied in {@code level}, around a controller at {@code controllerPos} facing {@code facing}. */
    public boolean matches(Level level, BlockPos controllerPos, Direction facing) {
        return findFirstMismatch(level, controllerPos, facing) == null;
    }

    /** The first world position (relative to a controller at {@code controllerPos} facing {@code facing}) that fails to match, or null if the pattern is fully satisfied. */
    public BlockPos findFirstMismatch(Level level, BlockPos controllerPos, Direction facing) {
        Rotation rotation = rotationFor(facing);
        for (Map.Entry<BlockPos, Predicate<BlockState>> entry : this.cells.entrySet()) {
            BlockPos worldPos = controllerPos.offset(entry.getKey().rotate(rotation));
            if (!entry.getValue().test(level.getBlockState(worldPos))) {
                return worldPos;
            }
        }
        return null;
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
