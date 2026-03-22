package fr.dyooneoxz.neoenchant.screen;

import fr.dyooneoxz.neoenchant.block.entity.PrimordialMonolithBlockEntity;
import fr.dyooneoxz.neoenchant.init.ModBlocks;
import fr.dyooneoxz.neoenchant.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PrimordialMonolithMenu extends AbstractContainerMenu {
    public final PrimordialMonolithBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public final int[] costs = new int[4];
    public final int[] enchantClue = new int[4];
    public final int[] levelClue = new int[4];
    public final DataSlot enchantSeed = DataSlot.standalone();

    private final ContainerData enchantmentData = new ContainerData() {
      @Override
      public int get(int index) {
        return switch (index) {
            case 0, 1, 2, 3 -> costs[index];
            case 4, 5, 6, 7 -> enchantClue[index - 4];
            case 8, 9, 10, 11 -> levelClue[index - 8];
            default -> 0;
        };
      }

      @Override
      public void set(int index, int value) {
          switch (index) {
              case 0, 1, 2, 3 -> costs[index] = value;
              case 4, 5, 6, 7 -> enchantClue[index - 4] = value;
              case 8, 9, 10, 11 -> levelClue[index - 8] = value;
          }
      }

      @Override
      public int getCount() {
        return 12;
      }

    };

    private ItemStack lastItem = ItemStack.EMPTY;

    public PrimordialMonolithMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public PrimordialMonolithMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.PRIMORDIAL_MONOLITH_MENU.get(), containerId);
        checkContainerSize(playerInventory, 2);
        this.blockEntity = (PrimordialMonolithBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 70, 33));
            this.addSlot(new SlotItemHandler(iItemHandler, 1, 90, 33));
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        this.addDataSlots(enchantmentData);
        this.addDataSlot(enchantSeed);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, ModBlocks.PRIMORDIAL_MONOLITH.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

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

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        // On récupère l'objet dans la case 0 (l'arme)
        ItemStack currentItem = this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(h -> h.getStackInSlot(0)).orElse(ItemStack.EMPTY);

        // Si l'objet a changé (le joueur a posé ou retiré un objet)
        if (!ItemStack.matches(currentItem, lastItem)) {
            lastItem = currentItem.copy();
            this.updateEnchantmentOptions(currentItem);
        }
    }

    private void updateEnchantmentOptions(ItemStack stack) {
        if (stack.isEmpty() || !stack.isEnchantable()) {
            for (int i = 0; i < 4; i++) {
                costs[i] = 0;
                enchantClue[i] = -1;
                levelClue[i] = -1;
            }
        } else {
            costs[0] = 30;  // Palier 1
            costs[1] = 50;  // Palier 2
            costs[2] = 75;  // Palier 3
            costs[3] = 100; // Palier 4

            this.enchantSeed.set(this.blockEntity.getLevel().random.nextInt());
            this.broadcastFullState();
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < 4) {
            int cost = this.costs[id];

            if (cost > 0 && (player.experienceLevel >= cost || player.getAbilities().instabuild)) {

                // POUR L'INSTANT : On affiche juste un message dans la console.
                // C'est ici, à la Phase 3, qu'on mettra la grosse logique magique !
                System.out.println("LE JOUEUR VEUT ENCHANTER ! Bouton choisi : " + id + " pour un coût de " + cost);
                return true;
            }
        }
        return false;
    }
}