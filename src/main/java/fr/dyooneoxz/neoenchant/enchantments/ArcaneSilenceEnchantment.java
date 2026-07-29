package fr.dyooneoxz.neoenchant.enchantments;

import fr.dyooneoxz.neoenchant.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public class ArcaneSilenceEnchantment extends Enchantment {

    public ArcaneSilenceEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public Component getFullname(int level) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        // Couleur Violet Mystique/Arcanique
        mutablecomponent.withStyle(style -> style.withColor(TextColor.parseColor("#8A2BE2")));
        return mutablecomponent;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof SwordItem ||
                stack.getItem() instanceof AxeItem ||
                stack.getItem() instanceof HoeItem;
    }
}