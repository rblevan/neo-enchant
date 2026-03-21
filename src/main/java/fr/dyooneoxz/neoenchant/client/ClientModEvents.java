package fr.dyooneoxz.neoenchant.client;

import fr.dyooneoxz.neoenchant.client.renderer.layers.FrostAuraLayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = fr.dyooneoxz.neoenchant.NeoEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String skinName : event.getSkins()) {
            PlayerRenderer playerRenderer = event.getSkin(skinName);
            if (playerRenderer != null) {
                playerRenderer.addLayer(new FrostAuraLayer<>(playerRenderer));
            }
        }
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            if (type == EntityType.PLAYER) continue;

            addLayerToMobSafe(event, type);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addLayerToMobSafe(EntityRenderersEvent.AddLayers event, EntityType<?> type) {
        try {
            Object renderer = event.getRenderer((EntityType) type);
            if (renderer instanceof LivingEntityRenderer) {
                LivingEntityRenderer livingRenderer = (LivingEntityRenderer) renderer;
                livingRenderer.addLayer(new FrostAuraLayer(livingRenderer));
            }
        } catch (Exception e) {
            // Si une entité moddée bizarre panique, on l'ignore en silence pour ne pas crasher le jeu
        }
    }
}