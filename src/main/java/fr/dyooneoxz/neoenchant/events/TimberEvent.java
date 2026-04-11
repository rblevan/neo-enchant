package fr.dyooneoxz.neoenchant.events;

import fr.dyooneoxz.neoenchant.NeoEnchant;
import fr.dyooneoxz.neoenchant.init.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Mod.EventBusSubscriber(modid = NeoEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TimberEvent {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = (Level) event.getLevel();
        BlockPos startPos = event.getPos();
        BlockState startState = level.getBlockState(startPos);

        if (player == null || player.isCreative() || level.isClientSide()) return;
        ItemStack handItem = player.getMainHandItem();
        int timberLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.TIMBER.get(), handItem);
        if (timberLevel <= 0 || !startState.is(BlockTags.LOGS)) return;
        event.setCanceled(true);

        Set<BlockPos> logs = new HashSet<>();
        Set<BlockPos> leaves = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(startPos);
        logs.add(startPos);

        while (!queue.isEmpty() && logs.size() < 128) {
            BlockPos current = queue.poll();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos checkPos = current.offset(x, y, z);
                        if (!logs.contains(checkPos)) {
                            BlockState checkState = level.getBlockState(checkPos);
                            if (checkState.is(BlockTags.LOGS)) {
                                if (Math.abs(checkPos.getX() - startPos.getX()) <= 5 &&
                                        Math.abs(checkPos.getZ() - startPos.getZ()) <= 5) {
                                    logs.add(checkPos);
                                    queue.add(checkPos);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (BlockPos logPos : logs) {
            for (int x = -3; x <= 3; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -3; z <= 3; z++) {
                        BlockPos leafPos = logPos.offset(x, y, z);
                        if (!leaves.contains(leafPos) && !logs.contains(leafPos)) {
                            BlockState leafState = level.getBlockState(leafPos);
                            if (leafState.is(BlockTags.LEAVES) && !leafState.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.PERSISTENT)) {
                                leaves.add(leafPos);
                            }
                        }
                    }
                }
            }
        }
        float woodChance = timberLevel == 3 ? 1.0f : (timberLevel == 2 ? 0.66f : 0.33f);
        float leafChance = timberLevel == 3 ? 0.66f : (timberLevel == 2 ? 0.50f : 0.33f);
        float stickSaplingChance = timberLevel == 3 ? 0.33f : (0.16f);
        float appleChance = timberLevel == 3 ? 0.10f : (timberLevel == 2 ? 0.05f : 0.02f);

        int logsDestroyed = 0;

        for (BlockPos pos : logs) {
            BlockState state = level.getBlockState(pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            logsDestroyed++;

            if (level.random.nextFloat() <= woodChance) {
                spawnItem(level, startPos, new ItemStack(state.getBlock()));
            }
        }

        for (BlockPos pos : leaves) {
            BlockState state = level.getBlockState(pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

            float roll = level.random.nextFloat();
            if (roll <= leafChance) {
                spawnItem(level, startPos, new ItemStack(state.getBlock()));
            } else if (roll <= leafChance + stickSaplingChance) {
                ItemStack drop = level.random.nextBoolean() ? new ItemStack(Items.STICK) : new ItemStack(Items.OAK_SAPLING);
                spawnItem(level, startPos, drop);
            } else if (roll <= leafChance + stickSaplingChance + appleChance) {
                spawnItem(level, startPos, new ItemStack(Items.APPLE));
            }
        }
        level.playSound(null, startPos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);

        handItem.hurtAndBreak(logsDestroyed, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
    }

    private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }
}