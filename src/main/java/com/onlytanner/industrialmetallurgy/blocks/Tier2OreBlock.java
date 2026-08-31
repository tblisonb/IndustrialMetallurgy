package com.onlytanner.industrialmetallurgy.blocks;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class Tier2OreBlock extends DropExperienceBlock {

    public Tier2OreBlock(BlockBehaviour.Properties properties) {
        super(UniformInt.of(0, 0), properties);
    }

    public static BlockBehaviour.Properties newProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(5.0f, 6.0f)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();
    }

}
