package com.onlytanner.industrialmetallurgy.items;

import com.onlytanner.industrialmetallurgy.init.ModDataComponents;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

// Shared machinery for the FE-powered hand tools (Power Drill, Chainsaw, Cultivator): a live
// "implement" socket (bit/chain/blade -- wears down with use, same durability numbers as the
// hand-tool tiers) and a live "battery pack" socket (drained per action, refilled at a Battery
// Box). Both sockets are swapped in and out with the exact click-onto-item interaction vanilla
// Bundles use (Item#overrideOtherStackedOnMe) rather than a dedicated screen -- left-click a valid
// item onto the tool to install it, right-click the tool with an empty cursor to pull the
// currently-installed implement out (or the battery, if no implement is installed).
public abstract class PowerToolItem extends Item {

    public PowerToolItem(Properties properties) {
        super(properties);
    }

    protected abstract boolean isValidImplement(Item item);

    protected abstract String implementTranslationKey();

    private static boolean isValidBattery(Item item) {
        return item.equals(RegistryHandler.BATTERY_PACK.get()) || item.equals(RegistryHandler.ADVANCED_BATTERY_PACK.get());
    }

    public final ItemStack getImplement(ItemStack tool) {
        return getSocketed(tool, ModDataComponents.SOCKETED_BIT.get());
    }

    public final void setImplement(ItemStack tool, ItemStack implement) {
        setSocketed(tool, ModDataComponents.SOCKETED_BIT.get(), implement);
    }

    public final ItemStack getBattery(ItemStack tool) {
        return getSocketed(tool, ModDataComponents.SOCKETED_BATTERY.get());
    }

    public final void setBattery(ItemStack tool, ItemStack battery) {
        setSocketed(tool, ModDataComponents.SOCKETED_BATTERY.get(), battery);
    }

    private static ItemStack getSocketed(ItemStack tool, DataComponentType<ItemContainerContents> component) {
        ItemContainerContents contents = tool.get(component);
        return contents == null ? ItemStack.EMPTY : contents.copyOne();
    }

    private static void setSocketed(ItemStack tool, DataComponentType<ItemContainerContents> component, ItemStack contents) {
        if (contents.isEmpty()) {
            tool.remove(component);
        } else {
            tool.set(component, ItemContainerContents.fromItems(List.of(contents)));
        }
    }

    // Drains FE from the socketed battery pack. Returns false (draining nothing) if there's no
    // battery installed or it doesn't have enough charge.
    protected final boolean tryDrainEnergy(ItemStack tool, int amount) {
        ItemStack battery = getBattery(tool);
        if (battery.isEmpty()) {
            return false;
        }
        int stored = battery.getOrDefault(ModDataComponents.STORED_ENERGY.get(), 0);
        if (stored < amount) {
            return false;
        }
        battery.set(ModDataComponents.STORED_ENERGY.get(), stored - amount);
        setBattery(tool, battery);
        return true;
    }

    // Damages the socketed implement by `amount`, consuming it entirely (same shrink-to-nothing
    // pattern the Crusher's burr sets already use) once its durability runs out. Nequitum
    // implements have no durability set (isDamageableItem() is false), so they never wear down --
    // same capstone behavior as the Nequitum hand tools. Returns false if the implement broke or
    // there wasn't one installed.
    protected final boolean damageImplement(ItemStack tool, int amount) {
        ItemStack implement = getImplement(tool);
        if (implement.isEmpty()) {
            return false;
        }
        if (implement.isDamageableItem()) {
            int newDamage = implement.getDamageValue() + amount;
            if (newDamage >= implement.getMaxDamage()) {
                implement = ItemStack.EMPTY;
            } else {
                implement.setDamageValue(newDamage);
            }
        }
        setImplement(tool, implement);
        return !implement.isEmpty();
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        if (self.getCount() != 1) {
            return false;
        }
        if (clickAction == ClickAction.PRIMARY && !other.isEmpty() && other.getCount() == 1) {
            if (isValidImplement(other.getItem())) {
                ItemStack previous = getImplement(self);
                setImplement(self, other.copy());
                carriedItem.set(previous);
                return true;
            } else if (isValidBattery(other.getItem())) {
                ItemStack previous = getBattery(self);
                setBattery(self, other.copy());
                carriedItem.set(previous);
                return true;
            }
            return false;
        } else if (clickAction == ClickAction.SECONDARY && other.isEmpty()) {
            ItemStack implement = getImplement(self);
            if (!implement.isEmpty()) {
                setImplement(self, ItemStack.EMPTY);
                carriedItem.set(implement);
                return true;
            }
            ItemStack battery = getBattery(self);
            if (!battery.isEmpty()) {
                setBattery(self, ItemStack.EMPTY);
                carriedItem.set(battery);
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        ItemStack implement = getImplement(itemStack);
        if (implement.isEmpty()) {
            builder.accept(Component.translatable("tooltip.industrialmetallurgy.no_implement_installed", Component.translatable(implementTranslationKey()))
                    .withStyle(ChatFormatting.RED));
        } else {
            builder.accept(Component.translatable("tooltip.industrialmetallurgy.implement_installed", implement.getHoverName(),
                            implement.getMaxDamage() - implement.getDamageValue(), implement.getMaxDamage())
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
