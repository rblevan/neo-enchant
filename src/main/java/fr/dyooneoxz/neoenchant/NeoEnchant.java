package fr.dyooneoxz.neoenchant;

import com.mojang.logging.LogUtils;
import fr.dyooneoxz.neoenchant.client.ClientModEvents;
import fr.dyooneoxz.neoenchant.init.ModEffects;
import fr.dyooneoxz.neoenchant.init.ModEnchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(NeoEnchant.MODID)
public class NeoEnchant
{
    public static final String MODID = "neoenchant";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoEnchant()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEnchantments.register(modEventBus);
        ModEffects.register(modEventBus);
        //if (FMLEnvironment.dist == Dist.CLIENT) {
        //    MinecraftForge.EVENT_BUS.register(ClientModEvents.class);
        //}

        LOGGER.info("Le mod NeoEnchant est chargé.");
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

}
