package fr.dyooneoxz.neoenchant.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.init.ModEffects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrostAuraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation FROST_AURA_TEXTURE = new ResourceLocation(NeoEnchant.MODID, "textures/entity/frost_aura.png");

    public FrostAuraLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!entity.isFullyFrozen()) {
            return;
        }

        M model = this.getParentModel();

        float xOffset = (ageInTicks * 0.005F) % 1.0F;
        float yOffset = (ageInTicks * 0.005F) % 1.0F;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.energySwirl(FROST_AURA_TEXTURE, xOffset, yOffset));

        poseStack.pushPose();

        float scale = 1.05F;
        poseStack.scale(scale, scale, scale);
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.8F);
        poseStack.popPose();
    }
}