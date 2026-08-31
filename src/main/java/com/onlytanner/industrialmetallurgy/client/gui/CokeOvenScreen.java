package com.onlytanner.industrialmetallurgy.client.gui;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.containers.CokeOvenContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CokeOvenScreen extends AbstractContainerScreen<CokeOvenContainer> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/coke_oven.png");

    private static final int FLAME_X = 84, FLAME_Y = 35;

    public CokeOvenScreen(CokeOvenContainer menu, Inventory playerInventory, Component title) {
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

        int burnOffset = 14 - this.menu.getBurnTimeScaled();
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + FLAME_X, y + FLAME_Y + burnOffset, 176.0F, burnOffset, 14, this.menu.getBurnTimeScaled(), 256, 256);
    }

}
