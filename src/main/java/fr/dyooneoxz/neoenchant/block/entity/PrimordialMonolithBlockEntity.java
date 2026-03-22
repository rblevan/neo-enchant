package fr.dyooneoxz.neoenchant.block.entity;

import fr.dyooneoxz.neoenchant.init.ModBlockEntities;
import fr.dyooneoxz.neoenchant.screen.PrimordialMonolithMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PrimordialMonolithBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public PrimordialMonolithBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRIMORDIAL_MONOLITH_BE.get(), pos, state);
    }

    // -- SAVE -- //

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.neoenchant.primordial_monolith");
    }


    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PrimordialMonolithMenu(containerId, playerInventory, this);
    }

    public void drops() {
        net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), itemHandler.getStackInSlot(0));
        net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), itemHandler.getStackInSlot(1));
    }
}