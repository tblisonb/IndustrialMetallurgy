package com.onlytanner.industrialmetallurgy.util;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

// NeoForge's own Capabilities.Item.BLOCK now expects a net.neoforged.neoforge.transfer
// ResourceHandler<ItemResource>, but every machine's inventory in this mod is still the older
// IItemHandlerModifiable-based ModItemHandler -- migrating every machine's storage to the new
// resource-handler paradigm is a real, separate task (see the comment on
// IndustrialMetallurgy#registerCapabilities), not a hidden prerequisite for the Conduit/I-O Port
// to move items around. This is a capability of our own instead, scoped to exactly what those two
// blocks need: the same IItemHandlerModifiable every machine already exposes today.
public final class ModCapabilities {

    public static final BlockCapability<IItemHandlerModifiable, Direction> ITEM_HANDLER =
            BlockCapability.createSided(
                    Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "item_handler"),
                    IItemHandlerModifiable.class);

    private ModCapabilities() {
    }
}
