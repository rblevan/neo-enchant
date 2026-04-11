package fr.dyooneoxz.neoenchant.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class HerosBaneEnchantment extends Enchantment {

    public HerosBaneEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, EnchantmentCategory.WEAPON, slots);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    @Override
    public boolean isTreasureOnly() { return true; }

    @Override
    public boolean isDiscoverable() { return false; }

    @Override
    public boolean isTradeable() { return false; }
}
