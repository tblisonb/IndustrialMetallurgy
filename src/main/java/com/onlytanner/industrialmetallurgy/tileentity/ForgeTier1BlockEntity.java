package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class ForgeTier1BlockEntity extends BasicForgeBlockEntity {

    public ForgeTier1BlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.FORGE_TIER1.get(), pos, state,
                50, 2000, 5, 1500,
                Set.of("iron"), Component.translatable("block.industrialmetallurgy.forge_tier1"),
                ModContainerTypes.FORGE_TIER1.get());
    }

}
