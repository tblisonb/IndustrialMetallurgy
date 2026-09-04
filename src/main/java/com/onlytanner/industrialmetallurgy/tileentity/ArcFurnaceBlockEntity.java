package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.blocks.ForgeBlock;
import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.multiblock.MultiblockPattern;
import com.onlytanner.industrialmetallurgy.multiblock.MultiblockPatternBuilder;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Set;

// The endgame forge tier: real electric arc furnaces are used for high-purity refining (higher
// yield than a conventional furnace, see the bonus-yield tier="arc" recipes) as well as
// specialty steelmaking -- both roles land here. Faster than every other tier (35 vs. the
// uniform 50 everywhere else) and runs hotter, matching real arc furnaces' actual advantage over
// resistance heating. The mod's first real multiblock: a 3x3x3 shell of Tungsten Steel Blocks
// around a Tungsten-Rhenium Block core, with this controller sitting on the front face -- see
// GUIDE.md and ROADMAP.md for the design writeup.
public class ArcFurnaceBlockEntity extends AdvancedForgeBlockEntity {

    // Authored against Direction.NORTH; MultiblockPattern rotates it onto this block's actual
    // facing at check time. Built lazily (not a static-final eagerly initialized at class-load
    // time) so the RegistryHandler.*.get() calls below can't possibly run before registration --
    // this class only loads on first real ArcFurnaceBlockEntity construction, which is already
    // well after mod init, but there's no reason to rely on that ordering when a lazy getter is
    // just as simple.
    private static MultiblockPattern shellPattern;

    public ArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.ARC_FURNACE.get(), pos, state,
                35, 4200, 30, 2500,
                Set.of("iron", "steel", "cobalt", "tungsten", "arc"), Component.translatable("block.industrialmetallurgy.arc_furnace"),
                ModContainerTypes.ARC_FURNACE.get());
    }

    @Nullable
    @Override
    protected MultiblockPattern getMultiblockPattern() {
        return shellPattern();
    }

    /**
     * Called from {@link com.onlytanner.industrialmetallurgy.blocks.MetalBlock#useWithoutItem} when
     * a Tungsten Steel or Tungsten-Rhenium Block is right-clicked -- searches the small volume
     * around it for a formed Arc Furnace whose shell actually includes this position, and opens
     * that furnace's GUI if one's found. A bounded, click-triggered scan (5x5x5 positions, only
     * for these two block types), not a per-tick or world-wide search, and player-triggered
     * one-off cost is fine even though most clicks on these blocks (used all over the mod as
     * plain decorative/storage blocks) will find nothing.
     */
    public static InteractionResult tryOpenFromShellClick(Level level, BlockPos clickedPos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        for (BlockPos candidate : BlockPos.betweenClosed(clickedPos.offset(-2, -2, -2), clickedPos.offset(2, 2, 2))) {
            if (level.getBlockEntity(candidate) instanceof ArcFurnaceBlockEntity furnace
                    && furnace.isFormed()
                    && shellPattern().containsWorldPos(candidate, furnace.getBlockState().getValue(ForgeBlock.FACING), clickedPos)) {
                player.openMenu(furnace, candidate);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    private static MultiblockPattern shellPattern() {
        if (shellPattern == null) {
            shellPattern = new MultiblockPatternBuilder()
                    // Bottom layer -- solid Tungsten Steel Block floor.
                    .aisle(
                            "SSS",
                            "SSS",
                            "SSS")
                    // Middle layer -- the controller (this block) sits on the front row, flanked
                    // by shell; the Tungsten-Rhenium Block core sits directly behind it, at the
                    // structure's true geometric center.
                    .aisle(
                            "SCS",
                            "SKS",
                            "SSS")
                    // Top layer -- solid Tungsten Steel Block roof.
                    .aisle(
                            "SSS",
                            "SSS",
                            "SSS")
                    .where('S', RegistryHandler.TUNGSTEN_STEEL_BLOCK.get())
                    .where('K', RegistryHandler.TUNGSTEN_RHENIUM_BLOCK.get())
                    .build();
        }
        return shellPattern;
    }

}
