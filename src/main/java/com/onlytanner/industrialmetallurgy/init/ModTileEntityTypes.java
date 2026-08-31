package com.onlytanner.industrialmetallurgy.init;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.tileentity.CokeOvenBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.CrusherBlockEntity;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTileEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IndustrialMetallurgy.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrusherBlockEntity>> CRUSHER =
            BLOCK_ENTITY_TYPES.register("crusher", () -> new BlockEntityType<>(CrusherBlockEntity::new, RegistryHandler.CRUSHER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CokeOvenBlockEntity>> COKE_OVEN =
            BLOCK_ENTITY_TYPES.register("coke_oven", () -> new BlockEntityType<>(CokeOvenBlockEntity::new, RegistryHandler.COKE_OVEN.get()));

    public static void init(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

}
