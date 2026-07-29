package fr.dyooneoxz.neoenchant.enchantments;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;


public class HerosBaneEnchantment extends Enchantment {

    public HerosBaneEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, EnchantmentCategory.WEAPON, slots);
    }

    @Override
    public Component getFullname(int level) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        mutablecomponent.withStyle(ChatFormatting.GOLD);

        // Ajoute le niveau en chiffres romains si l'enchantement a un niveau supérieur à 1
        if (level != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
        }

        return mutablecomponent;
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
