package fr.dyooneoxz.neoenchant.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fr.dyooneoxz.neoenchant.block.entity.PrimordialMonolithBlockEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PrimordialMonolithRenderer implements BlockEntityRenderer<PrimordialMonolithBlockEntity> {

    private static final ResourceLocation CRYSTAL_TEXTURE = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(CRYSTAL_TEXTURE);

    private final ModelPart glass;
    private final ModelPart cube;

    public PrimordialMonolithRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(ModelLayers.END_CRYSTAL);
        this.glass = modelpart.getChild("glass");
        this.cube = modelpart.getChild("cube");
    }

    @Override
    public void render(PrimordialMonolithBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 1.12D, 0.5D);

        float scale = 0.5F;
        poseStack.scale(scale, scale, scale);

        float time = blockEntity.getLevel().getGameTime() + partialTick;
        float rotation = time * 3.0F;
        float bobbing = (float) Math.sin(time * 0.1F) * 0.15F;

        poseStack.translate(0.0D, bobbing, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
        this.cube.render(poseStack, bufferSource.getBuffer(RENDER_TYPE), packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 0.8F));
        poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
        this.glass.render(poseStack, bufferSource.getBuffer(RENDER_TYPE), packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation / 2.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(60.0F));
        this.glass.render(poseStack, bufferSource.getBuffer(RENDER_TYPE), packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.popPose();
    }
}