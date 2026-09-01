package com.onlytanner.industrialmetallurgy.init;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.tileentity.CokeOvenBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.CrusherBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ExtruderBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier1BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier2BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier3BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ChemicalCentrifugeBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ChemicalReactorBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ForgeTier4BlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.SolderingStationBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.ThermoelectricGeneratorBlockEntity;
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThermoelectricGeneratorBlockEntity>> THERMOELECTRIC_GENERATOR =
            BLOCK_ENTITY_TYPES.register("thermoelectric_generator", () -> new BlockEntityType<>(ThermoelectricGeneratorBlockEntity::new, RegistryHandler.THERMOELECTRIC_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForgeTier1BlockEntity>> FORGE_TIER1 =
            BLOCK_ENTITY_TYPES.register("forge_tier1", () -> new BlockEntityType<>(ForgeTier1BlockEntity::new, RegistryHandler.FORGE_TIER1.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForgeTier2BlockEntity>> FORGE_TIER2 =
            BLOCK_ENTITY_TYPES.register("forge_tier2", () -> new BlockEntityType<>(ForgeTier2BlockEntity::new, RegistryHandler.FORGE_TIER2.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForgeTier3BlockEntity>> FORGE_TIER3 =
            BLOCK_ENTITY_TYPES.register("forge_tier3", () -> new BlockEntityType<>(ForgeTier3BlockEntity::new, RegistryHandler.FORGE_TIER3.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForgeTier4BlockEntity>> FORGE_TIER4 =
            BLOCK_ENTITY_TYPES.register("forge_tier4", () -> new BlockEntityType<>(ForgeTier4BlockEntity::new, RegistryHandler.FORGE_TIER4.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtruderBlockEntity>> EXTRUDER =
            BLOCK_ENTITY_TYPES.register("extruder", () -> new BlockEntityType<>(ExtruderBlockEntity::new, RegistryHandler.EXTRUDER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolderingStationBlockEntity>> SOLDERING_STATION =
            BLOCK_ENTITY_TYPES.register("soldering_station", () -> new BlockEntityType<>(SolderingStationBlockEntity::new, RegistryHandler.SOLDERING_STATION.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChemicalCentrifugeBlockEntity>> CHEMICAL_CENTRIFUGE =
            BLOCK_ENTITY_TYPES.register("chemical_centrifuge", () -> new BlockEntityType<>(ChemicalCentrifugeBlockEntity::new, RegistryHandler.CHEMICAL_CENTRIFUGE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChemicalReactorBlockEntity>> CHEMICAL_REACTOR =
            BLOCK_ENTITY_TYPES.register("chemical_reactor", () -> new BlockEntityType<>(ChemicalReactorBlockEntity::new, RegistryHandler.CHEMICAL_REACTOR.get()));

    public static void init(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

}
