package com.onlytanner.industrialmetallurgy.client.gui;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.containers.AutoclaveContainer;
import com.onlytanner.industrialmetallurgy.tileentity.AutoclaveBlockEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AutoclaveScreen extends AbstractContainerScreen<AutoclaveContainer> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/autoclave.png");

    public AutoclaveScreen(AutoclaveContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.inventoryLabelX += 20;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 86, y + 40, 176.0F, 98.0F, this.menu.getProcessProgressionScaled(), 16, 256, 256);

        int energyOffset = 70 - this.menu.getCurrentEnergyScaled();
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 8, y + 8 + energyOffset, 176.0F, energyOffset + 28.0F, 16, this.menu.getCurrentEnergyScaled(), 256, 256);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (this.isHovering(8, 8, 16, 70, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(this.font, Component.translatable("tooltip.industrialmetallurgy.machine_energy",
                    this.menu.currentEnergy.get(), AutoclaveBlockEntity.MAX_ENERGY), mouseX, mouseY);
        }
    }

}
