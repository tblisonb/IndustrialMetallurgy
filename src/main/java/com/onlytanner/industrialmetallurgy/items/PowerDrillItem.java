package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

// Breaks a 3x3 plane centered on whatever's targeted, perpendicular to the player's facing --
// same fixed-size area regardless of bit tier (matching Tinkers' Construct's hammer). The
// socketed Drill Bit is what actually determines mining speed and harvest-level gating -- this
// class just delegates to whatever Tool component the bit itself carries, exactly like a plain
// pickaxe would for its own static Tool component.
public class PowerDrillItem extends PowerToolItem {

    private static final int FE_COST_PER_BLOCK = 40;
    private static final Set<Item> VALID_BITS = Set.of(
            RegistryHandler.STEEL_DRILL_BIT.get(), RegistryHandler.COBALT_STEEL_DRILL_BIT.get(),
            RegistryHandler.STELLITE_DRILL_BIT.get(), RegistryHandler.TUNGSTEN_STEEL_DRILL_BIT.get(),
            RegistryHandler.TUNGSTEN_RHENIUM_DRILL_BIT.get()
    );

    public PowerDrillItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isValidImplement(Item item) {
        return VALID_BITS.contains(item);
    }

    @Override
    protected String implementTranslationKey() {
        return "item.industrialmetallurgy.drill_bit_generic";
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
        Tool tool = getImplement(itemStack).get(DataComponents.TOOL);
        return tool != null ? tool.getMiningSpeed(state) : 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack itemStack, BlockState state) {
        Tool tool = getImplement(itemStack).get(DataComponents.TOOL);
        return tool != null && tool.isCorrectForDrops(state);
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (!(level instanceof ServerLevel serverLevel) || getImplement(itemStack).isEmpty()) {
            return true;
        }
        if (!damageImplement(itemStack, 1)) {
            return true;
        }
        if (!tryDrainEnergy(itemStack, FE_COST_PER_BLOCK)) {
            return true;
        }

        Direction axis = owner.getDirection();
        for (BlockPos extraPos : planeAround(pos, axis)) {
            if (getImplement(itemStack).isEmpty()) {
                break;
            }
            BlockState extraState = serverLevel.getBlockState(extraPos);
            if (extraState.isAir() || !isCorrectToolForDrops(itemStack, extraState)) {
                continue;
            }
            if (!tryDrainEnergy(itemStack, FE_COST_PER_BLOCK)) {
                break;
            }
            serverLevel.destroyBlock(extraPos, true, owner);
            damageImplement(itemStack, 1);
        }
        return true;
    }

    private static Iterable<BlockPos> planeAround(BlockPos center, Direction axis) {
        java.util.List<BlockPos> positions = new java.util.ArrayList<>(8);
        Direction.Axis a = axis.getAxis();
        for (int u = -1; u <= 1; u++) {
            for (int v = -1; v <= 1; v++) {
                if (u == 0 && v == 0) {
                    continue;
                }
                BlockPos offset = switch (a) {
                    case X -> center.offset(0, u, v);
                    case Y -> center.offset(u, 0, v);
                    case Z -> center.offset(u, v, 0);
                };
                positions.add(offset);
            }
        }
        return positions;
    }

}
