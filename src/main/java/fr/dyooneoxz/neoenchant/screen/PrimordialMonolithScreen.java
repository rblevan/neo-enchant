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
    private static final Style RUNIC_STYLE = Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt"));

    private final int[] buttonPosX = {9, 90};
    private final int[] buttonPosY = {13, 43};

    private static final int BTN_WIDTH = 54;
    private static final int BTN_HEIGHT = 24;


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

        // --- DESSIN DES 4 BOUTONS ---
        // ATTENTION : Tu devras sûrement modifier ces deux valeurs (60 et 14) pour
        // aligner les boutons avec le dessin de ton interface personnalisée !
        int buttonX = x + 9;
        int buttonStartY = y + 13;

        EnchantmentNames.getInstance().initSeed(this.menu.enchantSeed.get());

        for (int i = 0; i < 4; ++i) {
            int cost = this.menu.costs[i];

            if (cost > 0) { // Si l'enchantement est disponible
                // On récupère tes coordonnées personnalisées pour ce bouton précis
                int btnX = x + buttonPosX[i];
                int btnY = y + buttonPosY[i];

                // La souris est-elle sur ce bouton ?
                boolean isHovering = mouseX >= btnX && mouseX < btnX + BTN_WIDTH && mouseY >= btnY && mouseY < btnY + BTN_HEIGHT;

                // Si survolé, on décale la lecture de l'image vers le bas pour afficher le bouton illuminé
                int vOffset = isHovering ? BTN_HEIGHT : 0;

                // On dessine ton bouton personnalisé ! (Les deux derniers paramètres sont la taille totale de ton fichier monolith_button.png)
                guiGraphics.blit(BTN_TEXTURE, btnX, btnY, BTN_WIDTH, BTN_HEIGHT, 0, vOffset, BTN_WIDTH, BTN_HEIGHT * 2, BTN_WIDTH, BTN_HEIGHT * 2);

                // --- GESTION DU TEXTE SANS DÉPASSEMENT ---
                String runicText = String.valueOf(EnchantmentNames.getInstance().getRandomName(this.font, 86));
                String costText = String.valueOf(cost);

                // Espace disponible pour le texte (Largeur du bouton - la place prise par le coût - marges)
                int maxTextWidth = BTN_WIDTH - this.font.width(costText) - 10;
                int actualTextWidth = this.font.width(runicText);

                // Sauvegarde l'état du pinceau
                guiGraphics.pose().pushPose();

                // Si le texte est plus grand que la place dispo, on calcule le pourcentage de réduction
                float scale = 1.0F;
                if (actualTextWidth > maxTextWidth) {
                    scale = (float) maxTextWidth / (float) actualTextWidth;
                }

                // On applique la réduction (et on déplace le point de départ pour compenser le rétrécissement)
                guiGraphics.pose().translate(btnX + 4, btnY + (BTN_HEIGHT / 2.0F) - 4, 0);
                guiGraphics.pose().scale(scale, scale, 1.0F);

                // On dessine le texte runique (En jaune clair si survolé, sinon couleur parchemin sombre)
                FormattedText formattedRunic = FormattedText.of(runicText, RUNIC_STYLE);
                guiGraphics.drawString(this.font, runicText, 0, 0, isHovering ? 0xFFFF80 : 0x3d3522, false);

                // On restaure le pinceau à sa taille normale
                guiGraphics.pose().popPose();

                // On dessine le coût à la fin du bouton
                guiGraphics.drawString(this.font, costText, btnX + BTN_WIDTH - this.font.width(costText) - 4, btnY + (BTN_HEIGHT / 2) - 4, 0x80FF20, false);
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

            // Si le joueur clique dans les limites de ton bouton
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
