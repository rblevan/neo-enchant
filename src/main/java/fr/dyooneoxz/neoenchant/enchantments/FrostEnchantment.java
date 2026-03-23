package fr.dyooneoxz.neoenchant.enchantments;

import fr.dyooneoxz.neoenchant.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BowItem;


public class FrostEnchantment extends Enchantment {

    // Constructors

    public FrostEnchantment() {
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
        return 1;
    }

    // -- ENCHANTING TABLE SETUP -- //

    @Override
    public int getMinCost(int level) {
        return 20;
    }

    @Override
    public int getMaxCost(int level) {
        return 50;
    }

    // -- APPLIABLE TO SWORDS AND AXES -- //

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof BowItem;
    }

    // -- IMCOMPATIBLE WITH FIRE ASPECT AND POISON STRIKES -- //

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other == Enchantments.FLAMING_ARROWS) {
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

        if (!attacker.level().isClientSide() && target instanceof LivingEntity) {

            LivingEntity livingTarget = (LivingEntity) target;

            int duration = level * 80;

            livingTarget.addEffect(new MobEffectInstance(ModEffects.FROST.get(), duration, level - 1, false, false, true));

        }
    }
}
