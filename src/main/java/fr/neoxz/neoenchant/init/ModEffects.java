package fr.neoxz.neoenchant.init;

import fr.neoxz.neoenchant.NeoEnchant;
import fr.neoxz.neoenchant.effects.FrostEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, NeoEnchant.MODID);

    public static final RegistryObject<MobEffect> FROST =
            MOB_EFFECTS.register("frost", () -> new FrostEffect());

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
