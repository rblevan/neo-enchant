package fr.dyooneoxz.neoenchant.init;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.enchantments.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, NeoEnchant.MODID);

    public static final RegistryObject<Enchantment> POISON_STRIKE =
            ENCHANTMENTS.register("poison_strike", PoisonStrikeEnchantment::new);

    public static final RegistryObject<Enchantment> FROST_ASPECT =
            ENCHANTMENTS.register("frost_aspect", FrostAspectEnchantment::new);

    public static final RegistryObject<Enchantment> FROST =
            ENCHANTMENTS.register("frost", FrostEnchantment::new);

    public static final RegistryObject<Enchantment> TIMBER =
            ENCHANTMENTS.register("timber", () -> new TimberEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> MINERAL_ECHO = ENCHANTMENTS.register("mineral_echo",
            () -> new MineralEchoEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> HEROS_BANE = ENCHANTMENTS.register("heros_bane",
            () -> new HerosBaneEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
