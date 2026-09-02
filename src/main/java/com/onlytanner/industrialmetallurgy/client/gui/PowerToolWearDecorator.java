package com.onlytanner.industrialmetallurgy.client.gui;

import com.onlytanner.industrialmetallurgy.items.PowerToolItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

// The socketed battery's charge already renders via PowerToolItem's own isBarVisible/getBarWidth/
// getBarColor overrides -- that's vanilla's one built-in durability-bar slot, at (x+2, y+13). This
// draws a second bar for the socketed implement's wear, 3px higher, using the exact same
// background+foreground geometry vanilla uses for its own bar (see ItemStack#isBarVisible and
// friends) so the two read as one consistent pair rather than two different bar styles stacked
// together. Implements with no durability (samples, Tungsten-Rhenium bits) simply show no bar,
// same as vanilla hides its own bar for a full-durability or non-damageable stack.
public class PowerToolWearDecorator implements IItemDecorator {

    private static final int BAR_Y_OFFSET = 10;

    @Override
    public boolean render(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
        if (!(stack.getItem() instanceof PowerToolItem powerTool)) {
            return false;
        }
        ItemStack implement = powerTool.getImplement(stack);
        if (implement.isEmpty() || !implement.isDamageableItem()) {
            return false;
        }

        int maxDamage = implement.getMaxDamage();
        int damage = implement.getDamageValue();
        float remaining = Math.max(0.0F, (float) (maxDamage - damage) / maxDamage);
        int barWidth = Mth.clamp(Math.round(13.0F * remaining), 0, 13);
        int color = ARGB.opaque(Mth.hsvToRgb(remaining / 3.0F, 1.0F, 1.0F));

        int barX = x + 2;
        int barY = y + BAR_Y_OFFSET;
        graphics.fill(RenderPipelines.GUI, barX, barY, barX + 13, barY + 2, 0xFF000000);
        graphics.fill(RenderPipelines.GUI, barX, barY, barX + barWidth, barY + 1, color);
        return true;
    }

}
