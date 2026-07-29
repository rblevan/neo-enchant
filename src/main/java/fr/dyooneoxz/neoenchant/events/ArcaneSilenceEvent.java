package fr.dyooneoxz.neoenchant.events;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.init.ModEffects;
import fr.dyooneoxz.neoenchant.init.ModEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NeoEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArcaneSilenceEvent {

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {

            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ARCANE_SILENCE.get(), attacker.getMainHandItem());

            if (level > 0 && !attacker.level().isClientSide()) {

                int duration = 20;
                target.addEffect(new MobEffectInstance(ModEffects.SILENCED.get(), duration, 0, false, true, true));
            }
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity().hasEffect(ModEffects.SILENCED.get())) {
            if (event.getEffectInstance().getEffect() != ModEffects.SILENCED.get()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}