package fr.dyooneoxz.neoenchant.init;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.block.PrimordialMonolithBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NeoEnchant.MODID);

    public static final RegistryObject<Block> PRIMORDIAL_MONOLITH = BLOCKS.register("primordial_monolith",
            () -> new PrimordialMonolithBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}