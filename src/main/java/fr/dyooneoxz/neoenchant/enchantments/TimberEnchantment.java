package fr.dyooneoxz.neoenchant.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class TimberEnchantment extends Enchantment {

    public TimberEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, EnchantmentCategory.DIGGER, slots);
    }

    @Override
    public int getMaxLevel() {
        return 3;
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

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other == Enchantments.BLOCK_FORTUNE || other == Enchantments.SILK_TOUCH) {
            return false;
        }
        return super.checkCompatibility(other);
    }
}
