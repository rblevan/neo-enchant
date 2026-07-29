package fr.dyooneoxz.neoenchant.enchantments;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class MineralEchoEnchantment extends Enchantment {

    public MineralEchoEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, EnchantmentCategory.DIGGER, slots);
    }

    @Override
    public Component getFullname(int level) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        mutablecomponent.withStyle(ChatFormatting.DARK_AQUA);

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
    protected boolean checkCompatibility(@NotNull Enchantment other) {
        if (other == Enchantments.BLOCK_FORTUNE) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    @Override
    public boolean isTreasureOnly() { return true; }

    @Override
    public boolean isDiscoverable() { return false; }

    @Override
    public boolean isTradeable() { return false; }
}
