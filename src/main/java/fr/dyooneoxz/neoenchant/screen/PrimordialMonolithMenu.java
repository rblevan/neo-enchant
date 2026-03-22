package fr.dyooneoxz.neoenchant.screen;

import fr.dyooneoxz.neoenchant.block.entity.PrimordialMonolithBlockEntity;
import fr.dyooneoxz.neoenchant.init.ModBlocks;
import fr.dyooneoxz.neoenchant.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
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

    private float countPowerBlocks() {
        float power = 0;
        net.minecraft.world.level.Level level = this.blockEntity.getLevel();
        net.minecraft.core.BlockPos center = this.blockEntity.getBlockPos();

        // On scanne sur un rayon de trois blocs (comme la table classique.)
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                // On check à la hauteur de la table (0) et juste au-dessus (1)
                for (int y = -2 ; y <= 2; y++) {
                    // On ignore les 9 blocs centraux pour laisser la place au joueur et à la table
                    if (Math.abs(x) < 2 && Math.abs(z) < 2) continue;
                    net.minecraft.core.BlockPos checkPos = center.offset(x, y, z);

                    // --- LE BLOC REQUIS ---
                    // Ici on utilise ton Obsidienne Primordiale (si elle est bien dans ModBlocks) !
                    // Tu peux remplacer par Blocks.OBSIDIAN si tu n'as pas encore créé le bloc dans le code.
                    if (level.getBlockState(checkPos).is(Blocks.NETHERITE_BLOCK)) {
                       power = power + 2;
                    }
                    if (level.getBlockState(checkPos).is(Blocks.DIAMOND_BLOCK)) {
                        power = power + 1;
                    }
                    if (level.getBlockState(checkPos).is(Blocks.EMERALD_BLOCK)) {
                        power = power + 1;
                    }
                    if (level.getBlockState(checkPos).is(Blocks.GOLD_BLOCK)) {
                        power = power + 0.8f;
                    }
                    if (level.getBlockState(checkPos).is(Blocks.LAPIS_BLOCK)) {
                        power = power + 0.5f;
                    }

                }
            }
        }
        return power;
    }

    private void updateEnchantmentOptions(ItemStack stack) {
        if (stack.isEmpty() || !stack.isEnchantable()) {
            for (int i = 0; i < 4; i++) {
                costs[i] = 0;
                enchantClue[i] = -1;
                levelClue[i] = -1;
            }
        } else {
            this.enchantSeed.set(this.blockEntity.getLevel().random.nextInt());
            var random = this.blockEntity.getLevel().random;

            float powerBlocks = countPowerBlocks();
            int bonus = (int)(powerBlocks * 2.5);

            int cost0 = random.nextInt(10) + 10 + (int)(bonus * 0.55);
            costs[0] = Math.min(cost0, 47);
            int cost1 = random.nextInt(15) + 20 + (int)(bonus * 0.65);
            costs[1] = Math.min(cost1, 65);
            int cost2 = random.nextInt(20) + 35 + (int)(bonus * 0.8);
            costs[2] = Math.min(cost2, 82);
            int cost3 = random.nextInt(25) + 50 + bonus;
            costs[3] = Math.min(cost3, 100);

            this.broadcastFullState();
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < 4) {
            int cost = this.costs[id];

            ItemStack weaponStack = this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .map(h -> h.getStackInSlot(0)).orElse(ItemStack.EMPTY);

            ItemStack lapisStack = this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .map(h -> h.getStackInSlot(1)).orElse(ItemStack.EMPTY);

            int lapisCost = id + 1;

            if (cost > 0 && !weaponStack.isEmpty() &&
                    (player.experienceLevel >= cost || player.getAbilities().instabuild) &&
                    (lapisStack.getItem() == net.minecraft.world.item.Items.LAPIS_BLOCK && lapisStack.getCount() >= lapisCost || player.getAbilities().instabuild)) {

                if (!player.getAbilities().instabuild) {
                    player.onEnchantmentPerformed(weaponStack, cost);
                    lapisStack.shrink(lapisCost);
                }

                java.util.List<net.minecraft.world.item.enchantment.EnchantmentInstance> enchantmentsToApply = getCustomEnchantments(weaponStack, cost, this.blockEntity.getLevel().random);

                for (net.minecraft.world.item.enchantment.EnchantmentInstance instance : enchantmentsToApply) {
                    weaponStack.enchant(instance.enchantment, instance.level);
                }

                this.updateEnchantmentOptions(ItemStack.EMPTY);
                player.level().playSound(null, player.blockPosition(),
                        net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, player.level().random.nextFloat() * 0.1F + 0.9F);

                return true;
            }
        }
        return false;
    }

    private java.util.List<net.minecraft.world.item.enchantment.EnchantmentInstance> getCustomEnchantments(ItemStack stack, int powerLevel, net.minecraft.util.RandomSource random) {
        java.util.List<net.minecraft.world.item.enchantment.EnchantmentInstance> list = new java.util.ArrayList<>();
        java.util.List<net.minecraft.world.item.enchantment.Enchantment> possibleEnchants = new java.util.ArrayList<>();
        for (net.minecraft.world.item.enchantment.Enchantment enchantment : net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS.getValues()) {
            if (enchantment.isCurse()) {
                continue;
            }

            if (enchantment.canEnchant(stack)) {
                net.minecraft.resources.ResourceLocation registryName = net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                if (registryName != null && registryName.getNamespace().equals(fr.dyooneoxz.neoenchant.NeoEnchant.MODID)) {

                    // C'est un de tes enchantements (Givre, Poison...) !
                    // On lui donne seulement 15% de chance de pouvoir être sélectionné.
                    // (Tu peux changer le 0.15f si tu veux les rendre plus ou moins rares)
                    if (random.nextFloat() > 0.15f) {
                        continue; // 85% du temps, on l'ignore pour forcer le joueur à farmer !
                    }
                }

                possibleEnchants.add(enchantment);
            }
        }

        if (possibleEnchants.isEmpty()) return list;

        // --- Règle 1 : Le nombre d'enchantements selon la puissance ---
        // Niveau 30+ = 2 enchants min. Niveau 80+ = 4 enchants min !
        int enchantCount;
        if (powerLevel >= 80) {
            // Niveau 80 à 100 : Entre 2 et 4 enchantements (Jamais 6 !)
            enchantCount = 2 + random.nextInt(3);
        } else if (powerLevel >= 50) {
            // Niveau 50 à 79 : Entre 1 et 3 enchantements
            enchantCount = 1 + random.nextInt(3);
        } else {
            // Niveau 0 à 49 : 1 ou 2 enchantements max
            enchantCount = 1 + random.nextInt(2);
        }
        java.util.Collections.shuffle(possibleEnchants, new java.util.Random(random.nextInt()));

        for (net.minecraft.world.item.enchantment.Enchantment enchantment : possibleEnchants) {
            if (list.size() >= enchantCount) break;
            // On vérifie que le nouvel enchantement est compatible avec ceux déjà choisis (ex: pas Tranchant ET Fléau des arthropodes)
            boolean isCompatible = true;
            for (net.minecraft.world.item.enchantment.EnchantmentInstance existing : list) {
                if (!enchantment.isCompatibleWith(existing.enchantment)) {
                    isCompatible = false;
                    break;
                }
            }
            if (isCompatible) {
                int maxLevel = enchantment.getMaxLevel();
                int levelToApply = 1;
                if (maxLevel > 1) {
                    if (powerLevel >= 80) {
                        levelToApply = maxLevel;
                    } else if (powerLevel >= 50) {
                        levelToApply = Math.max(1, maxLevel - random.nextInt(2));
                    } else {
                        levelToApply = random.nextInt(maxLevel) + 1;
                    }
                }
                list.add(new net.minecraft.world.item.enchantment.EnchantmentInstance(enchantment, levelToApply));
            }
        }
        return list;
    }
}