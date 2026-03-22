package fr.dyooneoxz.neoenchant.block;

import fr.dyooneoxz.neoenchant.block.entity.PrimordialMonolithBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;

public class PrimordialMonolithBlock extends BaseEntityBlock {

    public PrimordialMonolithBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PrimordialMonolithBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PrimordialMonolithBlockEntity) {
                ((PrimordialMonolithBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof PrimordialMonolithBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer) player), (PrimordialMonolithBlockEntity) entity, pos);
            } else {
                throw new IllegalStateException("Notre Container provider manque à l'appel !");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}