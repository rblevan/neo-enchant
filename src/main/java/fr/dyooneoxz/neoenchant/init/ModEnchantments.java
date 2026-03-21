package fr.dyooneoxz.neoenchant.init;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.enchantments.FrostEnchantment;
import fr.dyooneoxz.neoenchant.enchantments.PoisonEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, NeoEnchant.MODID);

    public static final RegistryObject<Enchantment> POISON_STRIKE =
            ENCHANTMENTS.register("poison_strike", () -> new PoisonEnchantment());

    public static final RegistryObject<Enchantment> FROST_ASPECT =
            ENCHANTMENTS.register("frost_aspect", () -> new FrostEnchantment());

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
