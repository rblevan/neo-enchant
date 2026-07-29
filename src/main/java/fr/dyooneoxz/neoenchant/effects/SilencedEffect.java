package fr.dyooneoxz.neoenchant.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class SilencedEffect extends MobEffect {

    public SilencedEffect() {
        super(MobEffectCategory.HARMFUL, 0x8910b5);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        List<MobEffect> effectsToRemove = new ArrayList<>();

        for (MobEffectInstance instance : livingEntity.getActiveEffects()) {
            if (instance.getEffect() != this) {
                effectsToRemove.add(instance.getEffect());
            }
        }
        for (MobEffect effect : effectsToRemove) {
            livingEntity.removeEffect(effect);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}