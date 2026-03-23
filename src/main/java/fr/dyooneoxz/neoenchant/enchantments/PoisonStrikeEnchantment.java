package fr.dyooneoxz.neoenchant.enchantments;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class PoisonStrikeEnchantment extends Enchantment {

    // Constructors

    public PoisonStrikeEnchantment() {
        super(
                Rarity.RARE,
                EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND}
        );
    }

    // Methods

    // -- LEVEL CONFIG -- //

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    // -- ENCHANTING TABLE SETUP -- //

    @Override
    public int getMinCost(int level) {
        return 10 + 20 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 10;
    }

    // -- APPLIABLE TO SWORDS AND AXES -- //

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    }

    // -- IMCOMPATIBLE WITH FIRE ASPECT AND FROST ASPECT -- //

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other == Enchantments.FIRE_ASPECT || other instanceof FrostAspectEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    // -- EFFECT LOGIC -- //

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {

        super.doPostAttack(attacker, target, level);

        if (!attacker.level().isClientSide()) {
            if (target instanceof LivingEntity) {

                LivingEntity livingTarget = (LivingEntity) target;

                int duration = level * 80;

                livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, duration, level - 1));

            }
        }
    }
}

