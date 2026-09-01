package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.Set;

// Tills a 3x3 (perpendicular to the clicked face, like a real HoeItem's single-block till) and
// plants a seed from the player's inventory into each freshly-tilled block in the same action.
// Tilling isn't hardness-gated in vanilla, so unlike the Drill, the socketed Cultivator Blade's
// tier only affects its own durability, not what it can till.
public class CultivatorItem extends PowerToolItem {

    private static final int FE_COST_PER_BLOCK = 20;
    private static final Set<Item> VALID_BLADES = Set.of(
            RegistryHandler.STEEL_CULTIVATOR_BLADE.get(), RegistryHandler.COBALT_STEEL_CULTIVATOR_BLADE.get(),
            RegistryHandler.STELLITE_CULTIVATOR_BLADE.get(), RegistryHandler.TUNGSTEN_STEEL_CULTIVATOR_BLADE.get(),
            RegistryHandler.TUNGSTEN_RHENIUM_CULTIVATOR_BLADE.get()
    );

    public CultivatorItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isValidImplement(Item item) {
        return VALID_BLADES.contains(item);
    }

    @Override
    protected String implementTranslationKey() {
        return "item.industrialmetallurgy.cultivator_blade_generic";
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.isClientSide() || player == null) {
            return InteractionResult.PASS;
        }
        ItemStack tool = context.getItemInHand();
        ItemStack blade = getImplement(tool);
        if (blade.isEmpty()) {
            return InteractionResult.PASS;
        }

        Direction face = context.getClickedFace();
        BlockPos center = context.getClickedPos();
        boolean tilledAny = false;
        for (BlockPos pos : planeAround(center, face)) {
            if (getImplement(tool).isEmpty()) {
                break;
            }
            BlockState current = level.getBlockState(pos);
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false);
            UseOnContext posContext = new UseOnContext(player, context.getHand(), hit);
            BlockState tilled = current.getToolModifiedState(posContext, ItemAbilities.HOE_TILL, false);
            if (tilled == null) {
                continue;
            }
            if (!tryDrainEnergy(tool, FE_COST_PER_BLOCK)) {
                break;
            }
            level.setBlock(pos, tilled, 11);
            level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            damageImplement(tool, 1);
            tilledAny = true;
            plantSeed(level, player, pos.above());
        }
        return tilledAny ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private static void plantSeed(Level level, Player player, BlockPos pos) {
        if (!level.getBlockState(pos).isAir()) {
            return;
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CropBlock cropBlock) {
                level.setBlock(pos, cropBlock.defaultBlockState(), 11);
                stack.shrink(1);
                return;
            }
        }
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
        Tool tool = getImplement(itemStack).get(DataComponents.TOOL);
        return tool != null ? tool.getMiningSpeed(state) : 1.0F;
    }

    private static Iterable<BlockPos> planeAround(BlockPos center, Direction face) {
        java.util.List<BlockPos> positions = new java.util.ArrayList<>(9);
        Direction.Axis a = face.getAxis();
        for (int u = -1; u <= 1; u++) {
            for (int v = -1; v <= 1; v++) {
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
