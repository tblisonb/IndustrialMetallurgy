package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Fells a whole tree in one swing: breaks the targeted log and flood-fills through every
// log-tagged block connected to it, capped by the socketed Chain's tier -- wood isn't hardness-
// gated in vanilla the way ore is, so a Chain's tier translates to "bigger tree in one swing"
// rather than a harvest-level gate, unlike the Drill's bits.
public class ChainsawItem extends PowerToolItem {

    private static final int FE_COST_PER_LOG = 30;
    private static final Set<Item> VALID_CHAINS = Set.of(
            RegistryHandler.STEEL_CHAIN.get(), RegistryHandler.COBALT_STEEL_CHAIN.get(),
            RegistryHandler.STELLITE_CHAIN.get(), RegistryHandler.TUNGSTEN_STEEL_CHAIN.get(),
            RegistryHandler.TUNGSTEN_RHENIUM_CHAIN.get()
    );
    private static final Map<Item, Integer> FELLING_CAP = Map.of(
            RegistryHandler.STEEL_CHAIN.get(), 16,
            RegistryHandler.COBALT_STEEL_CHAIN.get(), 16,
            RegistryHandler.STELLITE_CHAIN.get(), 32,
            RegistryHandler.TUNGSTEN_STEEL_CHAIN.get(), 32,
            RegistryHandler.TUNGSTEN_RHENIUM_CHAIN.get(), 64
    );

    public ChainsawItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isValidImplement(Item item) {
        return VALID_CHAINS.contains(item);
    }

    @Override
    protected String implementTranslationKey() {
        return "item.industrialmetallurgy.chain_generic";
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
        ItemStack chain = getImplement(itemStack);
        if (!(level instanceof ServerLevel serverLevel) || chain.isEmpty() || !state.is(BlockTags.LOGS)) {
            return true;
        }
        if (!damageImplement(itemStack, 1)) {
            return true;
        }

        int cap = FELLING_CAP.getOrDefault(chain.getItem(), 16);
        Set<BlockPos> visited = new HashSet<>();
        visited.add(pos);
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(pos);

        int felled = 1;
        while (!queue.isEmpty() && felled < cap) {
            BlockPos current = queue.poll();
            for (Direction direction : Direction.values()) {
                if (felled >= cap) {
                    break;
                }
                BlockPos neighbor = current.relative(direction);
                if (!visited.add(neighbor)) {
                    continue;
                }
                if (!serverLevel.getBlockState(neighbor).is(BlockTags.LOGS)) {
                    continue;
                }
                if (getImplement(itemStack).isEmpty() || !tryDrainEnergy(itemStack, FE_COST_PER_LOG)) {
                    return true;
                }
                serverLevel.destroyBlock(neighbor, true, owner);
                damageImplement(itemStack, 1);
                queue.add(neighbor);
                felled++;
            }
        }
        return true;
    }

}
