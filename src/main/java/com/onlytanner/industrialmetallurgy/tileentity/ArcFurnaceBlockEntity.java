package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

// The endgame forge tier: real electric arc furnaces are used for high-purity refining (higher
// yield than a conventional furnace, see the bonus-yield tier="arc" recipes) as well as
// specialty steelmaking -- both roles land here. Faster than every other tier (35 vs. the
// uniform 50 everywhere else) and runs hotter, matching real arc furnaces' actual advantage over
// resistance heating.
public class ArcFurnaceBlockEntity extends AdvancedForgeBlockEntity {

    public ArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.ARC_FURNACE.get(), pos, state,
                35, 4200, 30, 2500,
                Set.of("iron", "steel", "cobalt", "tungsten", "arc"), Component.translatable("block.industrialmetallurgy.arc_furnace"),
                ModContainerTypes.ARC_FURNACE.get());
    }

}
