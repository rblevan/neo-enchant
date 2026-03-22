package fr.dyooneoxz.neoenchant.init;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NeoEnchant.MODID);

    public static final RegistryObject<Item> PRIMORDIAL_MONOLITH = ITEMS.register("primordial_monolith",
            () -> new BlockItem(ModBlocks.PRIMORDIAL_MONOLITH.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}