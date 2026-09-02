package com.onlytanner.industrialmetallurgy.multiblock;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

// Small library of common cell predicates for MultiblockPatternBuilder.where(...).
public final class MultiblockPredicates {

    private MultiblockPredicates() {
    }

    public static Predicate<BlockState> any() {
        return state -> true;
    }

    public static Predicate<BlockState> air() {
        return BlockState::isAir;
    }

    public static Predicate<BlockState> of(Block... blocks) {
        return state -> {
            for (Block block : blocks) {
                if (state.is(block)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static Predicate<BlockState> tag(TagKey<Block> tag) {
        return state -> state.is(tag);
    }
}
