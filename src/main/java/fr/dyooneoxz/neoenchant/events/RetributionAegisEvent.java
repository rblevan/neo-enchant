package fr.dyooneoxz.neoenchant.events;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.init.ModEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NeoEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RetributionAegisEvent {

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack shield = player.getUseItem();

            if (shield.getItem() instanceof net.minecraft.world.item.ShieldItem) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RETRIBUTION_AEGIS.get(), shield);

                if (level > 0) {
                    int useDuration = shield.getUseDuration() - player.getUseItemRemainingTicks();
                    if (useDuration <= 10) {
                        net.minecraft.world.entity.Entity attacker = event.getDamageSource().getEntity();

                        if (attacker instanceof LivingEntity livingAttacker) {
                            float reflectDamage = (event.getBlockedDamage() * 0.5f) + (level * 3);
                            livingAttacker.hurt(player.damageSources().thorns(player), reflectDamage);
                            livingAttacker.knockback(0.4D + (0.2D * level), player.getX() - livingAttacker.getX(), player.getZ() - livingAttacker.getZ());

                            player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1.0F, 1.5F);
                        }
                    }
                }
            }
        }
    }
}