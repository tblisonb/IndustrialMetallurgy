package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class ForgeTier3BlockEntity extends AdvancedForgeBlockEntity {

    public ForgeTier3BlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.FORGE_TIER3.get(), pos, state,
                50, 3000, 15, 2000,
                Set.of("iron", "steel", "cobalt"), Component.translatable("block.industrialmetallurgy.forge_tier3"),
                ModContainerTypes.FORGE_TIER3.get());
    }

}
