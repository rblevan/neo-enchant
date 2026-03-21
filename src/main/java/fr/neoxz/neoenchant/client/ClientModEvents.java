package fr.neoxz.neoenchant.client;

import fr.neoxz.neoenchant.NeoEnchant;
import fr.neoxz.neoenchant.init.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

public class ClientModEvents {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.level().isClientSide()) {
            return;
        }

        // DEBUG: Décommenter la ligne ci-dessous pour voir si l'événement se déclenche (attention au spam console)
        // System.out.println("Tick client sur " + entity.getName().getString());

        if (entity.hasEffect(ModEffects.FROST.get())) {

            // DEBUG: Confirmation que l'effet est détecté
            System.out.println("Effet Frost détecté sur " + entity.getName().getString());

            Random rand = new Random();


            if (rand.nextFloat() < 1.0f) {

                double x = entity.getX() + (rand.nextDouble() - 0.5) * entity.getBbWidth();
                double y = entity.getY() + rand.nextDouble() * entity.getBbHeight();
                double z = entity.getZ() + (rand.nextDouble() - 0.5) * entity.getBbWidth();

                entity.level().addParticle(
                        ParticleTypes.HEART,
                        x, y, z,
                        0.0D, 0.0D, 0.0D // Vitesse à 0 pour voir si elles apparaissent statiques
                );
            }
        }
    }
}
