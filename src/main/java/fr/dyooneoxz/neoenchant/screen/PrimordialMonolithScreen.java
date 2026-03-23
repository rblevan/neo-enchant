package fr.dyooneoxz.neoenchant.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.dyooneoxz.neoenchant.NeoEnchant;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class PrimordialMonolithScreen extends AbstractContainerScreen<PrimordialMonolithMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NeoEnchant.MODID, "textures/gui/primordial_monolith_gui.png");
    private static final ResourceLocation ACTIVE_BUTTON_TEXTURE =
            new ResourceLocation(NeoEnchant.MODID, "textures/gui/active_btn_bg.png");
    private static final ResourceLocation ACTIVE_COST_TEXTURE =
            new ResourceLocation(NeoEnchant.MODID, "textures/gui/active_cost_bg.png");

    private static final Style RUNIC_STYLE = Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt"));

    private final int[] buttonPosX = {9, 110, 9, 110};
    private final int[] buttonPosY = {13, 13, 43, 43};

    private final int[] costPosX = {66, 90, 66, 90};
    private final int[] costPosY = {13, 13, 54, 54};

    private static final int BTN_WIDTH = 57;
    private static final int BTN_HEIGHT = 25;

    private static final int BOX_WIDTH = 20;
    private static final int BOX_HEIGHT = 14;

    public PrimordialMonolithScreen(PrimordialMonolithMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, imageWidth, imageHeight, 0, 0, 352, 332, 352, 332);

        EnchantmentNames.getInstance().initSeed(this.menu.enchantSeed.get());

        for (int i = 0; i < 4; ++i) {
            int cost = this.menu.costs[i];

            if (cost > 0) {
                int btnX = x + buttonPosX[i];
                int btnY = y + buttonPosY[i];
                int currentCostX = x + costPosX[i];
                int currentCostY = y + costPosY[i];

                guiGraphics.blit(ACTIVE_BUTTON_TEXTURE, btnX, btnY, BTN_WIDTH, BTN_HEIGHT, 0.0F, 0.0F, BTN_WIDTH * 10, BTN_HEIGHT * 10, BTN_WIDTH * 10, BTN_HEIGHT * 10);
                guiGraphics.blit(ACTIVE_COST_TEXTURE, currentCostX, currentCostY, BOX_WIDTH, BOX_HEIGHT, 0.0F, 0.0F, BOX_WIDTH * 10, BOX_HEIGHT * 10, BOX_WIDTH * 10, BOX_HEIGHT * 10);

                boolean isHovering = mouseX >= btnX && mouseX < btnX + BTN_WIDTH && mouseY >= btnY && mouseY < btnY + BTN_HEIGHT;

                if (isHovering) {
                    guiGraphics.fill(btnX, btnY, btnX + BTN_WIDTH, btnY + BTN_HEIGHT, 0x40FFFFFF);
                }

                String runicText = String.valueOf(EnchantmentNames.getInstance().getRandomName(this.font, BTN_WIDTH * 2));

                java.util.List<net.minecraft.util.FormattedCharSequence> lines =
                        this.font.split(FormattedText.of(runicText, RUNIC_STYLE), BTN_WIDTH - 4);

                int maxLines = BTN_HEIGHT / this.font.lineHeight;

                for (int line = 0; line < Math.min(lines.size(), maxLines); line++) {
                    guiGraphics.drawString(this.font, lines.get(line), btnX + 2, btnY + 1 + (line * this.font.lineHeight), isHovering ? 0x8CFFFB : 0xffca18, true);
                }

                String costText = String.valueOf(cost);

                int textWidth = this.font.width(costText);

                int centeredX = currentCostX + (BOX_WIDTH / 2) - (textWidth / 2);
                int centeredY = currentCostY + (BOX_HEIGHT / 2) - (this.font.lineHeight / 2);

                guiGraphics.drawString(this.font, costText, centeredX, centeredY, 0x8CFFFB, true);
            }
        }
    }
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for (int i = 0; i < 4; ++i) {
            int btnX = x + buttonPosX[i];
            int btnY = y + buttonPosY[i];

            if (pMouseX >= btnX && pMouseX < btnX + BTN_WIDTH && pMouseY >= btnY && pMouseY < btnY + BTN_HEIGHT) {
                if (this.menu.clickMenuButton(this.minecraft.player, i)) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    return true;
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
