package fr.dyooneoxz.neoenchant.init;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.block.entity.PrimordialMonolithBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NeoEnchant.MODID);

    public static final RegistryObject<BlockEntityType<PrimordialMonolithBlockEntity>> PRIMORDIAL_MONOLITH_BE =
            BLOCK_ENTITIES.register("primordial_monolith", () ->
                    BlockEntityType.Builder.of(PrimordialMonolithBlockEntity::new,
                            ModBlocks.PRIMORDIAL_MONOLITH.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}