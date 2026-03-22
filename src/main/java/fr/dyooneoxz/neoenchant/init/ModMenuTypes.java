package fr.dyooneoxz.neoenchant.init;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.screen.PrimordialMonolithMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NeoEnchant.MODID);

    public static final RegistryObject<MenuType<PrimordialMonolithMenu>> PRIMORDIAL_MONOLITH_MENU =
            MENUS.register("primordial_monolith_menu", () -> IForgeMenuType.create(PrimordialMonolithMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}