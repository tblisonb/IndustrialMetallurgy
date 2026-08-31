package com.onlytanner.industrialmetallurgy.client.gui;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.containers.CrusherContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CrusherScreen extends AbstractContainerScreen<CrusherContainer> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/crusher.png");

    public CrusherScreen(CrusherContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 79, y + 35, 176.0F, 0.0F, this.menu.getSmeltProgressionScaled(), 16, 256, 256);

        int energyOffset = 70 - this.menu.getCurrentEnergyScaled();
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 8, y + 8 + energyOffset, 176.0F, energyOffset + 17.0F, 16, this.menu.getCurrentEnergyScaled(), 256, 256);

        int acidOffset = 28 - this.menu.getAcidLevelScaled();
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 157, y + 29 + acidOffset, 192.0F, acidOffset + 17.0F, 6, this.menu.getAcidLevelScaled(), 256, 256);
    }

}
