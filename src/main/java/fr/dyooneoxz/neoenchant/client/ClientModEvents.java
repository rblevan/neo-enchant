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
           if (type != EntityType.PLAYER) {
               addLayerToMob(event, type);
           }
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addLayerToMob(EntityRenderersEvent.AddLayers event, EntityType<?> type) {
       try {
           Object renderer = event.getRenderer((EntityType) type);
            if (renderer instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new FrostAuraLayer(livingRenderer));
            }
        } catch (ClassCastException e) {
            // Ignore
        }
    }
}