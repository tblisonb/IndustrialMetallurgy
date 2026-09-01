package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.init.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

// A rechargeable lithium-ion pack -- the only chemistry among the mod's batteries that's actually
// right for a handheld power tool (lead-acid/dry-cell are the wrong tech for that, see ROADMAP).
// Stores FE directly via a data component rather than the one-shot burn-for-a-fixed-value pattern
// Battery Box's own fuel slot uses; recharges in place when sitting in Battery Box's charge slot.
public class BatteryPackItem extends Item {

    private final int capacity;

    public BatteryPackItem(Properties properties, int capacity) {
        super(properties);
        this.capacity = capacity;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public static int capacityOf(Item item) {
        return item instanceof BatteryPackItem pack ? pack.getCapacity() : 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int capacity = capacityOf(stack.getItem());
        int stored = stack.getOrDefault(ModDataComponents.STORED_ENERGY.get(), 0);
        return capacity <= 0 ? 0 : Mth.clamp(Math.round(13.0F * stored / capacity), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x3B8FE0;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        int stored = itemStack.getOrDefault(ModDataComponents.STORED_ENERGY.get(), 0);
        int capacity = capacityOf(itemStack.getItem());
        builder.accept(Component.translatable("tooltip.industrialmetallurgy.stored_energy", stored, capacity).withStyle(ChatFormatting.GRAY));
    }

}
