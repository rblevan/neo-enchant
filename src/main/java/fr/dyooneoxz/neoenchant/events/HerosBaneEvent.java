package fr.dyooneoxz.neoenchant.events;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.init.ModEnchantments;
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
            float bonusDamage = baneLevel * 2.0f;

            int sharpnessLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, weapon);

            if (sharpnessLevel > 0) {
                float synergyBoost = (sharpnessLevel * baneLevel) * 0.625f;
                bonusDamage += synergyBoost;

                attackerPlayer.level().playSound(null, targetPlayer.blockPosition(),
                        net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_CRIT,
                        net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.2F);
            }

            event.setAmount(originalDamage + bonusDamage);
        }
    }
}