package com.onlytanner.industrialmetallurgy.init;

import com.mojang.serialization.Codec;
import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// Backs the power tools' two live sockets (bit/chain/blade, and battery pack) and a battery
// pack's own charge level. SOCKETED_BIT/SOCKETED_BATTERY reuse ItemContainerContents -- the same
// value type vanilla's shulker boxes use -- purely for its ready-made codec/stream-codec, sized
// to a single slot.
public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, IndustrialMetallurgy.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> SOCKETED_BIT =
            DATA_COMPONENT_TYPES.register("socketed_bit", () -> DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> SOCKETED_BATTERY =
            DATA_COMPONENT_TYPES.register("socketed_battery", () -> DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_ENERGY =
            DATA_COMPONENT_TYPES.register("stored_energy", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    public static void init(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }

}
