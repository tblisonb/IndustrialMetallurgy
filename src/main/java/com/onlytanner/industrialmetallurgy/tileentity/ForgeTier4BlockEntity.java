package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModContainerTypes;
import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class ForgeTier4BlockEntity extends AdvancedForgeBlockEntity {

    public ForgeTier4BlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.FORGE_TIER4.get(), pos, state,
                50, 3500, 20, 2250,
                Set.of("iron", "steel", "cobalt", "tungsten"), Component.translatable("block.industrialmetallurgy.forge_tier4"),
                ModContainerTypes.FORGE_TIER4.get());
    }

}
