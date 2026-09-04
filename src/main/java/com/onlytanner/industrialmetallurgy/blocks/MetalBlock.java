package com.onlytanner.industrialmetallurgy.blocks;

import com.onlytanner.industrialmetallurgy.tileentity.ArcFurnaceBlockEntity;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class MetalBlock extends Block {

    public MetalBlock(Properties properties) {
        super(properties);
    }

    public static BlockBehaviour.Properties newProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0f, 6.0f)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    // Every other metal block in the mod is purely decorative/storage and this check is a no-op
    // for them -- only Tungsten Steel and Tungsten-Rhenium Blocks ever bother searching nearby for
    // a formed Arc Furnace to open, since those are the only two blocks its shell is built from.
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (this == RegistryHandler.TUNGSTEN_STEEL_BLOCK.get() || this == RegistryHandler.TUNGSTEN_RHENIUM_BLOCK.get()) {
            return ArcFurnaceBlockEntity.tryOpenFromShellClick(level, pos, player);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

}
