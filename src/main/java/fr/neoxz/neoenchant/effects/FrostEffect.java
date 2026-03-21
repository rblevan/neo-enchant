package fr.neoxz.neoenchant.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

public class FrostEffect extends MobEffect {

    // Constructors

    public FrostEffect() {
        super(MobEffectCategory.HARMFUL, 0x99CCFF);
    }

    // Methods

    // -- EFFECT LOGIC -- //

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide() && entity.tickCount % 20 == 0) {
            entity.hurt(entity.damageSources().freeze(), 0.5F);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
