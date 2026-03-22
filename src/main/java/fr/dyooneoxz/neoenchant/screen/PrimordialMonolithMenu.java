package fr.dyooneoxz.neoenchant.screen;

import fr.dyooneoxz.neoenchant.block.entity.PrimordialMonolithBlockEntity;
import fr.dyooneoxz.neoenchant.init.ModBlocks;
import fr.dyooneoxz.neoenchant.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PrimordialMonolithMenu extends AbstractContainerMenu {
    public final PrimordialMonolithBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    // Constructeur appelé par le Client
    public PrimordialMonolithMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    // Constructeur appelé par le Serveur
    public PrimordialMonolithMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.PRIMORDIAL_MONOLITH_MENU.get(), containerId);
        checkContainerSize(playerInventory, 2); // On a 2 slots dans notre bloc
        this.blockEntity = (PrimordialMonolithBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 141, 66));
            this.addSlot(new SlotItemHandler(iItemHandler, 1, 180, 66));
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    // Autorise le joueur à utiliser ce menu seulement s'il est proche du bloc
    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, ModBlocks.PRIMORDIAL_MONOLITH.get());
    }

    // Dessine l'inventaire du joueur (Les maths standards de Minecraft)
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 16 + l * 18, 168 + i * 18));
            }
        }
    }

    // Dessine la barre d'action du joueur
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 16 + i * 18, 284));
        }
    }

    // Gère le "Shift-Clic" pour transférer rapidement les objets
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < 36) {
            if (!moveItemStackTo(sourceStack, 36, 38, false)) { // Vers les 2 cases du Monolithe
                return ItemStack.EMPTY;
            }
        } else if (index < 38) {
            if (!moveItemStackTo(sourceStack, 0, 36, false)) { // Vers l'inventaire du joueur
                return ItemStack.EMPTY;
            }
        } else { return ItemStack.EMPTY; }

        if (sourceStack.getCount() == 0) { sourceSlot.set(ItemStack.EMPTY); }
        else { sourceSlot.setChanged(); }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }
}