package com.onlytanner.industrialmetallurgy.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

// Opens the in-game guide book -- a single vanilla-styled BookViewScreen (GuideBookContent,
// client package) built from static content rather than an ItemStack's own book component, so
// there's nothing to write or sign. The isClientSide() guard is what keeps this item safe to
// register on a dedicated server: the client-only call inside openGuideScreen() is never
// reached, and never linked, on that side.
public class GuideBookItem extends Item {

    public GuideBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            openGuideScreen();
        }
        return InteractionResult.SUCCESS;
    }

    private static void openGuideScreen() {
        com.onlytanner.industrialmetallurgy.client.gui.GuideBookContent.open();
    }

}
