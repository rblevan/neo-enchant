package fr.dyooneoxz.neoenchant.enchantments;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ArcaneOverloadEnchantment extends Enchantment {

    public ArcaneOverloadEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public Component getFullname(int level) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        mutablecomponent.withStyle(style -> style.withColor(TextColor.parseColor("#24d17a")));

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
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem;
    }
}
