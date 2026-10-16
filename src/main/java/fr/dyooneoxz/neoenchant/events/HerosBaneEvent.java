package fr.dyooneoxz.neoenchant.events;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.init.ModEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NeoEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HerosBaneEvent {

    @SubscribeEvent
    public static void onPlayerAttack(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player targetPlayer)) return;
        if (!(event.getSource().getEntity() instanceof Player attackerPlayer)) return;

        ItemStack weapon = attackerPlayer.getMainHandItem();
        int baneLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.HEROS_BANE.get(), weapon);

        if (baneLevel > 0) {
            float originalDamage = event.getAmount();

            // +10% par hero's bane (10% / 20% / 30%)
            float bonusPercentage = baneLevel * 0.10f;

            // +3% par Sharpness (+15% max sharpness V)
            int sharpnessLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, weapon);
            if (sharpnessLevel > 0) {
                bonusPercentage += (sharpnessLevel * 0.03f);

                attackerPlayer.level().playSound(null, targetPlayer.blockPosition(),
                        SoundEvents.PLAYER_ATTACK_CRIT,
                        SoundSource.PLAYERS, 1.0F, 1.2F);
            }

            // calcul final
            float finalDamage = originalDamage * (1.0f + bonusPercentage);
            event.setAmount(finalDamage);
        }
    }
}