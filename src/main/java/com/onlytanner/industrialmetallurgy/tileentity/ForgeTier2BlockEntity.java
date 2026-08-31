package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class ForgeTier2BlockEntity extends BasicForgeBlockEntity {

    public ForgeTier2BlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.FORGE_TIER2.get(), pos, state,
                35, 2500, 8, 1500,
                Set.of("iron", "steel"), Component.translatable("block.industrialmetallurgy.forge_tier2"),
                ModContainerTypes.FORGE_TIER2.get());
    }

}
