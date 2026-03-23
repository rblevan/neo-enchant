package fr.dyooneoxz.neoenchant.events;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.init.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Mod.EventBusSubscriber(modid = NeoEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MineralEchoEvent {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = (Level) event.getLevel();
        BlockPos startPos = event.getPos();
        BlockState startState = level.getBlockState(startPos);

        if (player == null || player.isCreative() || level.isClientSide()) return;

        ItemStack handItem = player.getMainHandItem();
        int echoLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MINERAL_ECHO.get(), handItem);

        if (echoLevel <= 0 || !startState.is(Tags.Blocks.ORES)) return;

        event.setCanceled(true);

        Set<BlockPos> ores = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(startPos);
        ores.add(startPos);

        Block targetOreBlock = startState.getBlock();

        while (!queue.isEmpty() && ores.size() < 64) {
            BlockPos current = queue.poll();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos checkPos = current.offset(x, y, z);
                        if (!ores.contains(checkPos)) {
                            BlockState checkState = level.getBlockState(checkPos);

                            if (checkState.is(targetOreBlock)) {
                                ores.add(checkPos);
                                queue.add(checkPos);
                            }
                        }
                    }
                }
            }
        }

        float dropChance = echoLevel == 3 ? 1.0f : (echoLevel == 2 ? 0.66f : 0.33f);
        int oresDestroyed = 0;

        if (level instanceof ServerLevel serverLevel) {
            for (BlockPos pos : ores) {
                BlockState state = level.getBlockState(pos);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                oresDestroyed++;

                if (level.random.nextFloat() <= dropChance) {

                    List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, null, player, handItem);
                    for (ItemStack drop : drops) {
                        spawnItem(level, startPos, drop);
                    }

                    int xp = state.getExpDrop(level, level.random, pos, 0, 0);
                    if (xp > 0) {
                        level.addFreshEntity(new ExperienceOrb(level, startPos.getX() + 0.5, startPos.getY() + 0.5, startPos.getZ() + 0.5, xp));
                    }
                }
            }
        }

        level.playSound(null, startPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
        handItem.hurtAndBreak(oresDestroyed, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
    }

    private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }
}
