package com.onlytanner.industrialmetallurgy.client.gui;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.containers.BasicForgeContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class BasicForgeScreen extends AbstractContainerScreen<BasicForgeContainer> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/forge_main.png");

    public BasicForgeScreen(BasicForgeContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 94, y + 35, 176.0F, 14.0F, this.menu.getSmeltProgressionScaled(), 16, 256, 256);

        int burnOffset = 14 - this.menu.getBurnTimeScaled();
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 18, y + 17 + burnOffset, 176.0F, burnOffset, 14, this.menu.getBurnTimeScaled(), 256, 256);

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 164, y + 76 - this.menu.getTemperatureScaled(), 176.0F, 31.0F, 7, 7, 256, 256);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (this.isHovering(164, 6, 7, 77, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(this.font, Component.translatable("tooltip.industrialmetallurgy.temperature",
                    this.menu.currentTemperature.get(), this.menu.blockEntity.maxTemperature), mouseX, mouseY);
        }
    }

}
