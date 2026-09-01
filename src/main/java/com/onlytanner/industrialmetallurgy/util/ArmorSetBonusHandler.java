package com.onlytanner.industrialmetallurgy.util;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

// Titanium (light, strong, corrosion-resistant) -> permanent Resistance I. Stellite (real
// heat/corrosion-resistant superalloy) -> permanent Fire Resistance. Tungsten-Rhenium gets both, echoing
// how netherite armor stacks its own passive on top of best-in-slot stats. Steel is a plain
// baseline and gets no entry here. Re-applied every server tick with a duration just longer than
// one tick's worth of margin, rather than granted once, so it silently tracks whether the full
// set is still worn.
@EventBusSubscriber(modid = IndustrialMetallurgy.MODID)
public class ArmorSetBonusHandler {

    private static final int EFFECT_DURATION = 210;

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        boolean titanium = isWearingFullSet(player,
                RegistryHandler.TITANIUM_HELMET.get(), RegistryHandler.TITANIUM_CHESTPLATE.get(),
                RegistryHandler.TITANIUM_LEGGINGS.get(), RegistryHandler.TITANIUM_BOOTS.get());
        boolean stellite = isWearingFullSet(player,
                RegistryHandler.STELLITE_HELMET.get(), RegistryHandler.STELLITE_CHESTPLATE.get(),
                RegistryHandler.STELLITE_LEGGINGS.get(), RegistryHandler.STELLITE_BOOTS.get());
        boolean tungsten_rhenium = isWearingFullSet(player,
                RegistryHandler.TUNGSTEN_RHENIUM_HELMET.get(), RegistryHandler.TUNGSTEN_RHENIUM_CHESTPLATE.get(),
                RegistryHandler.TUNGSTEN_RHENIUM_LEGGINGS.get(), RegistryHandler.TUNGSTEN_RHENIUM_BOOTS.get());

        if (titanium || tungsten_rhenium) {
            applyPassive(player, MobEffects.RESISTANCE);
        }
        if (stellite || tungsten_rhenium) {
            applyPassive(player, MobEffects.FIRE_RESISTANCE);
        }
    }

    private static boolean isWearingFullSet(Player player, Item helmet, Item chestplate, Item leggings, Item boots) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem().equals(helmet)
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem().equals(chestplate)
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem().equals(leggings)
                && player.getItemBySlot(EquipmentSlot.FEET).getItem().equals(boots);
    }

    private static void applyPassive(Player player, Holder<MobEffect> effect) {
        player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, 0, true, false, true));
    }

}
