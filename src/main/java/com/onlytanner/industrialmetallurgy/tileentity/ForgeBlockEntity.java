package com.onlytanner.industrialmetallurgy.tileentity;

import net.minecraft.network.chat.Component;

/** Implemented by both the solid-fuel (tiers 1-2) and electric (tiers 3-4) forge block entities. */
public interface ForgeBlockEntity {

    void tick();

    void setCustomName(Component name);

}
