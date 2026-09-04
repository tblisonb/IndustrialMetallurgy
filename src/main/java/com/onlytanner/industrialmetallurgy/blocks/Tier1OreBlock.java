package com.onlytanner.industrialmetallurgy.blocks;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class Tier1OreBlock extends DropExperienceBlock {

    public Tier1OreBlock(BlockBehaviour.Properties properties) {
        super(UniformInt.of(0, 0), properties);
    }

    public static BlockBehaviour.Properties newProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0f, 4.0f)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();
    }

    // Matches vanilla's stone-ore/deepslate-ore pairing (e.g. iron_ore/deepslate_iron_ore):
    // +1.5 hardness, same blast resistance, deepslate map color/sound.
    public static BlockBehaviour.Properties newDeepslateProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.DEEPSLATE)
                .strength(4.5f, 4.0f)
                .sound(SoundType.DEEPSLATE)
                .requiresCorrectToolForDrops();
    }

}
