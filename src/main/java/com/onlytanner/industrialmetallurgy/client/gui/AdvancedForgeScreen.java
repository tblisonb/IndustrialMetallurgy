package com.onlytanner.industrialmetallurgy.client.gui;

import com.onlytanner.industrialmetallurgy.IndustrialMetallurgy;
import com.onlytanner.industrialmetallurgy.client.TemperatureUnit;
import com.onlytanner.industrialmetallurgy.containers.AdvancedForgeContainer;
import com.onlytanner.industrialmetallurgy.tileentity.AdvancedForgeBlockEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AdvancedForgeScreen extends AbstractContainerScreen<AdvancedForgeContainer> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IndustrialMetallurgy.MODID, "textures/gui/container/electric_forge_main.png");

    public AdvancedForgeScreen(AdvancedForgeContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        // Default label positions collide with the energy bar along the top-left of this layout
        // (same class of issue as CrusherScreen); center the title and nudge the inventory label
        // clear of it.
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.inventoryLabelX += 20;
        this.addRenderableWidget(Button.builder(Component.literal(TemperatureUnit.current().symbol()), button -> {
                    TemperatureUnit.toggle();
                    button.setMessage(Component.literal(TemperatureUnit.current().symbol()));
                })
                .bounds(this.leftPos, this.topPos - 22, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.industrialmetallurgy.toggle_temperature_unit")))
                .build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 94, y + 35, 176.0F, 14.0F, this.menu.getSmeltProgressionScaled(), 16, 256, 256);

        int energyOffset = 70 - this.menu.getCurrentEnergyScaled();
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 8, y + 8 + energyOffset, 176.0F, energyOffset + 24.0F, 16, this.menu.getCurrentEnergyScaled(), 256, 256);

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 164, y + 76 - this.menu.getTemperatureScaled(), 176.0F, 17.0F, 7, 7, 256, 256);

        // Only ever true for a tier with a multiblock structure requirement (currently just the
        // Arc Furnace) -- Tiers 3/4 always report formed and never draw this.
        if (!this.menu.isFormed()) {
            graphics.centeredText(this.font, Component.translatable("gui.industrialmetallurgy.structure_incomplete"),
                    x + this.imageWidth / 2, y + 68, 0xFF5555);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (this.isHovering(8, 8, 16, 70, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(this.font, Component.translatable("tooltip.industrialmetallurgy.machine_energy",
                    this.menu.currentEnergy.get(), AdvancedForgeBlockEntity.MAX_ENERGY), mouseX, mouseY);
        } else if (this.isHovering(164, 6, 7, 77, mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(this.font, Component.translatable("tooltip.industrialmetallurgy.temperature",
                    TemperatureUnit.current().format(this.menu.currentTemperature.get())), mouseX, mouseY);
        }
    }

}
