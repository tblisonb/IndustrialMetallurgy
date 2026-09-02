package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.init.ModDataComponents;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// A handheld ore magnetometer, not a mining tool -- no vanilla item does this. Reuses
// PowerToolItem's "implement" socket as a calibration-sample slot instead of a wearable bit:
// insert a crushed-ore item (or, for Lepidolite -- which has no crushed intermediate, see
// RegistryHandler -- the raw lepidolite item) to tune what the scan looks for, same real-world
// idea as calibrating a geophysical instrument against a reference sample. Right-click to sweep a
// cube around the player; battery-drained per sweep, sample never consumes like a real reference
// standard wouldn't.
public class ProspectorItem extends PowerToolItem {

    private static final int FE_COST_PER_SCAN = 4000;
    private static final int RADIUS = 16;

    private static final Map<Item, List<Block>> SAMPLE_TARGETS = buildSampleTargets();

    private static Map<Item, List<Block>> buildSampleTargets() {
        Map<Item, List<Block>> map = new HashMap<>();
        map.put(RegistryHandler.CRUSHED_ARGENTITE_ORE.get(), List.of(RegistryHandler.ARGENTITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_BAUXITE_ORE.get(), List.of(RegistryHandler.BAUXITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_CASSITERITE_ORE.get(), List.of(RegistryHandler.CASSITERITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_CHROMITE_ORE.get(), List.of(RegistryHandler.CHROMITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_COBALTITE_ORE.get(), List.of(RegistryHandler.COBALTITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_GALENA_ORE.get(), List.of(RegistryHandler.GALENA_ORE.get()));
        map.put(RegistryHandler.CRUSHED_GARNIERITE_ORE.get(), List.of(RegistryHandler.GARNIERITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_PYROLUSITE_ORE.get(), List.of(RegistryHandler.PYROLUSITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_RHENIITE_ORE.get(), List.of(RegistryHandler.RHENIITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_RUTILE_ORE.get(), List.of(RegistryHandler.RUTILE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_SPHALERITE_ORE.get(), List.of(RegistryHandler.SPHALERITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_SCHEELITE_ORE.get(), List.of(RegistryHandler.SCHEELITE_ORE.get()));
        map.put(RegistryHandler.LEPIDOLITE.get(), List.of(RegistryHandler.LEPIDOLITE_ORE.get()));
        map.put(RegistryHandler.CRUSHED_GOLD_ORE.get(), List.of(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE));
        map.put(RegistryHandler.CRUSHED_IRON_ORE.get(), List.of(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE));
        // Raw ore drops (Part 14) work as samples too -- you'll have one of these in hand before
        // you ever touch a Crusher, so the Prospector shouldn't require the crushed form.
        map.put(RegistryHandler.RAW_ARGENTITE_ORE.get(), List.of(RegistryHandler.ARGENTITE_ORE.get()));
        map.put(RegistryHandler.RAW_BAUXITE_ORE.get(), List.of(RegistryHandler.BAUXITE_ORE.get()));
        map.put(RegistryHandler.RAW_CASSITERITE_ORE.get(), List.of(RegistryHandler.CASSITERITE_ORE.get()));
        map.put(RegistryHandler.RAW_CHROMITE_ORE.get(), List.of(RegistryHandler.CHROMITE_ORE.get()));
        map.put(RegistryHandler.RAW_COBALTITE_ORE.get(), List.of(RegistryHandler.COBALTITE_ORE.get()));
        map.put(RegistryHandler.RAW_GALENA_ORE.get(), List.of(RegistryHandler.GALENA_ORE.get()));
        map.put(RegistryHandler.RAW_GARNIERITE_ORE.get(), List.of(RegistryHandler.GARNIERITE_ORE.get()));
        map.put(RegistryHandler.RAW_PYROLUSITE_ORE.get(), List.of(RegistryHandler.PYROLUSITE_ORE.get()));
        map.put(RegistryHandler.RAW_RHENIITE_ORE.get(), List.of(RegistryHandler.RHENIITE_ORE.get()));
        map.put(RegistryHandler.RAW_RUTILE_ORE.get(), List.of(RegistryHandler.RUTILE_ORE.get()));
        map.put(RegistryHandler.RAW_SPHALERITE_ORE.get(), List.of(RegistryHandler.SPHALERITE_ORE.get()));
        map.put(RegistryHandler.RAW_SCHEELITE_ORE.get(), List.of(RegistryHandler.SCHEELITE_ORE.get()));
        return Map.copyOf(map);
    }

    public ProspectorItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isValidImplement(Item item) {
        return SAMPLE_TARGETS.containsKey(item);
    }

    @Override
    protected String implementTranslationKey() {
        return "item.industrialmetallurgy.ore_sample_generic";
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        ItemStack tool = player.getItemInHand(hand);
        ItemStack sample = getImplement(tool);
        if (sample.isEmpty()) {
            return InteractionResult.PASS;
        }
        List<Block> targets = SAMPLE_TARGETS.get(sample.getItem());
        if (targets == null || targets.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!tryDrainEnergy(tool, FE_COST_PER_SCAN)) {
            serverPlayer.sendOverlayMessage(Component.translatable("message.industrialmetallurgy.prospector_no_charge")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        BlockPos origin = player.blockPosition();
        BlockPos nearest = findNearest(serverLevel, origin, targets);
        Component targetName = targets.get(0).getName();

        if (nearest == null) {
            serverPlayer.sendOverlayMessage(Component.translatable("message.industrialmetallurgy.prospector_not_found", targetName)
                    .withStyle(ChatFormatting.GRAY));
            serverLevel.playSound(null, origin, SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.6F, 1.4F);
            return InteractionResult.SUCCESS;
        }

        int dx = nearest.getX() - origin.getX();
        int dy = nearest.getY() - origin.getY();
        int dz = nearest.getZ() - origin.getZ();
        int distance = (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
        String location = locationDescription(dx, dy, dz);
        Component foundName = serverLevel.getBlockState(nearest).getBlock().getName();

        serverPlayer.sendOverlayMessage(Component.translatable("message.industrialmetallurgy.prospector_found",
                        foundName, distance, location)
                .withStyle(ChatFormatting.AQUA));
        serverLevel.playSound(null, origin, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.6F, 1.6F);
        return InteractionResult.SUCCESS;
    }

    // Nearest-match sweep of a cube around `origin`, skipping anything outside the world's build
    // height or not currently loaded (never forces new chunk generation just to scan it).
    private static BlockPos findNearest(ServerLevel level, BlockPos origin, List<Block> targets) {
        BlockPos nearest = null;
        long nearestDistSq = Long.MAX_VALUE;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    cursor.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!level.isInWorldBounds(cursor) || !level.isLoaded(cursor)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(cursor);
                    if (!targets.contains(state.getBlock())) {
                        continue;
                    }
                    long distSq = (long) x * x + (long) y * y + (long) z * z;
                    if (distSq < nearestDistSq) {
                        nearestDistSq = distSq;
                        nearest = cursor.immutable();
                    }
                }
            }
        }
        return nearest;
    }

    private static String locationDescription(int dx, int dy, int dz) {
        String vertical = dy > 2 ? "above you" : dy < -2 ? "below you" : "at your level";
        if (dx == 0 && dz == 0) {
            return dy > 2 ? "directly above you" : dy < -2 ? "directly below you" : "right at your feet";
        }
        return bearing(dx, dz) + ", " + vertical;
    }

    private static String bearing(int dx, int dz) {
        double degrees = Math.toDegrees(Math.atan2(dx, -dz));
        degrees = (degrees + 360.0) % 360.0;
        String[] names = {"north", "north-east", "east", "south-east", "south", "south-west", "west", "north-west"};
        int index = (int) Math.round(degrees / 45.0) % 8;
        return names[index];
    }

    // Overrides PowerToolItem's default hover text rather than calling it: the base version
    // reports the socketed item's remaining durability, which is meaningless for a reference
    // sample that's never consumed. Battery reporting is identical to every other power tool.
    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        ItemStack sample = getImplement(itemStack);
        if (sample.isEmpty()) {
            builder.accept(Component.translatable("tooltip.industrialmetallurgy.no_implement_installed",
                            Component.translatable(implementTranslationKey()))
                    .withStyle(ChatFormatting.RED));
        } else {
            builder.accept(Component.translatable("tooltip.industrialmetallurgy.sample_installed", sample.getHoverName())
                    .withStyle(ChatFormatting.GRAY));
        }

        ItemStack battery = getBattery(itemStack);
        if (battery.isEmpty()) {
            builder.accept(Component.translatable("tooltip.industrialmetallurgy.no_battery_installed").withStyle(ChatFormatting.RED));
        } else {
            int stored = battery.getOrDefault(ModDataComponents.STORED_ENERGY.get(), 0);
            int capacity = BatteryPackItem.capacityOf(battery.getItem());
            builder.accept(Component.translatable("tooltip.industrialmetallurgy.battery_installed", stored, capacity).withStyle(ChatFormatting.GRAY));
        }
    }

}
