package com.onlytanner.industrialmetallurgy.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class OilSandBlock extends Block {

    public OilSandBlock(Properties properties) {
        super(properties);
    }

    public static BlockBehaviour.Properties newProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.SAND)
                .strength(2.0f, 2.0f)
                .sound(SoundType.SAND);
    }

}
