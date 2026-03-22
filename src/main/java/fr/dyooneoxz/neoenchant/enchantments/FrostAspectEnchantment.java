package fr.dyooneoxz.neoenchant.enchantments;

import fr.dyooneoxz.neoenchant.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;


public class FrostAspectEnchantment extends Enchantment {

    // Constructors

    public FrostAspectEnchantment() {
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

    // -- IMCOMPATIBLE WITH FIRE ASPECT AND POISON STRIKES -- //

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other == Enchantments.FIRE_ASPECT || other instanceof PoisonStrikeEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    // -- EFFECT LOGIC -- //

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {

        super.doPostAttack(attacker, target, level);

        if (!attacker.level().isClientSide() && target instanceof LivingEntity) {

            LivingEntity livingTarget = (LivingEntity) target;

            int duration = level * 80;

            livingTarget.addEffect(new MobEffectInstance(ModEffects.FROST.get(), duration, level - 1, false, false, true));

        }
    }
}
