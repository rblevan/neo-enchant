package fr.dyooneoxz.neoenchant.events;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.init.ModEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NeoEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArcaneOverloadEvent {

    @SubscribeEvent
    public static void onArrowHit(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow) {

            if (arrow.getOwner() instanceof Player player) {

                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ARCANE_OVERLOAD.get(), player.getMainHandItem());
                if (level == 0) {
                    level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ARCANE_OVERLOAD.get(), player.getOffhandItem());
                }

                if (level > 0) {
                    int effectiveLevel = Math.min(player.experienceLevel, 200);

                    float xpBonus = (effectiveLevel * 0.025f) * level;

                    event.setAmount(event.getAmount() + xpBonus);

                    // ==========================================
                    // OPTION : PÉNALITÉ D'XP
                    // Décommente ce bloc pour que chaque flèche réussie consomme un peu d'XP
                    // ==========================================
                    /*
                    if (!player.getAbilities().instabuild && player.experienceLevel > 0) {
                        // Retire 1 point d'XP (pas un niveau entier, juste un point)
                        player.giveExperiencePoints(-1);
                    }
                    */
                }
            }
        }
    }
}