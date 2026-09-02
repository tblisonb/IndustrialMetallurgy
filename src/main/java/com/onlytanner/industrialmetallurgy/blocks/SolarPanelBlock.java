package com.onlytanner.industrialmetallurgy.blocks;

import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.tileentity.SolarPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

// No inventory, no recipe, no GUI -- a real solar panel has nothing to insert. Right-clicking just
// reports current status (buffer level, whether it's generating and why not, if not) rather than
// opening a menu, since there's nothing for a menu to show that isn't already visible here.
public class SolarPanelBlock extends Block implements EntityBlock {

    public SolarPanelBlock(Properties properties) {
        super(properties);
    }

    public static BlockBehaviour.Properties newProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLUE)
                .strength(4.0f, 6.0f)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return type == ModTileEntityTypes.SOLAR_PANEL.get() ? (lvl, pos, st, be) -> ((SolarPanelBlockEntity) be).tick() : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity tile = level.getBlockEntity(pos);
        if (!(tile instanceof SolarPanelBlockEntity solarPanel)) {
            return InteractionResult.FAIL;
        }

        String statusKey;
        if (!solarPanel.canGenerate(serverLevel)) {
            statusKey = serverLevel.getSkyDarken() == 0 ? "message.industrialmetallurgy.solar_panel_no_sky" : "message.industrialmetallurgy.solar_panel_night";
        } else if (serverLevel.isThundering()) {
            statusKey = "message.industrialmetallurgy.solar_panel_thunder";
        } else if (serverLevel.isRaining()) {
            statusKey = "message.industrialmetallurgy.solar_panel_rain";
        } else {
            statusKey = "message.industrialmetallurgy.solar_panel_generating";
        }

        serverPlayer.sendOverlayMessage(Component.translatable("message.industrialmetallurgy.solar_panel_status",
                solarPanel.getEnergyAmount(), SolarPanelBlockEntity.MAX_ENERGY, Component.translatable(statusKey)));
        return InteractionResult.CONSUME;
    }

}
