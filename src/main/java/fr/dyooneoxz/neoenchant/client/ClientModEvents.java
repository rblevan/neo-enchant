package fr.dyooneoxz.neoenchant.client;

import fr.dyooneoxz.neoenchant.client.renderer.blockentity.PrimordialMonolithRenderer;
import fr.dyooneoxz.neoenchant.client.renderer.layers.FrostAuraLayer;
import fr.dyooneoxz.neoenchant.init.ModBlockEntities;
import fr.dyooneoxz.neoenchant.init.ModMenuTypes;
import fr.dyooneoxz.neoenchant.screen.PrimordialMonolithScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.PRIMORDIAL_MONOLITH_MENU.get(), PrimordialMonolithScreen::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PRIMORDIAL_MONOLITH_BE.get(), PrimordialMonolithRenderer::new);
    }
}