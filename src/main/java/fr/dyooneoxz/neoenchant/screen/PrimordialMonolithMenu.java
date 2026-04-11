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
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index == 0 || index == 1) {
            if (!this.moveItemStackTo(sourceStack, 2, 38, true)) {
                return ItemStack.EMPTY;
            }
            sourceSlot.onQuickCraft(sourceStack, copyOfSourceStack);
        }
        else {
            if (sourceStack.getItem() == net.minecraft.world.item.Items.LAPIS_BLOCK) {
                if (!this.moveItemStackTo(sourceStack, 1, 2, false)) {
                    if (index < 29) {
                        if (!this.moveItemStackTo(sourceStack, 29, 38, false)) return ItemStack.EMPTY;
                    } else if (!this.moveItemStackTo(sourceStack, 2, 29, false)) return ItemStack.EMPTY; // Hotbar vers inventaire
                }
            }
            else if (sourceStack.isEnchantable() || sourceStack.is(net.minecraft.world.item.Items.BOOK)) {
                if (!this.moveItemStackTo(sourceStack, 0, 1, false)) {
                    if (index < 29) {
                        if (!this.moveItemStackTo(sourceStack, 29, 38, false)) return ItemStack.EMPTY;
                    } else if (!this.moveItemStackTo(sourceStack, 2, 29, false)) return ItemStack.EMPTY;
                }
            }
            else {
                if (index < 29) {
                    if (!this.moveItemStackTo(sourceStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38) {
                    if (!this.moveItemStackTo(sourceStack, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        if (sourceStack.getCount() == copyOfSourceStack.getCount()) {
            return ItemStack.EMPTY;
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        ItemStack currentItem = this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(h -> h.getStackInSlot(0)).orElse(ItemStack.EMPTY);

        if (!ItemStack.matches(currentItem, lastItem)) {
            lastItem = currentItem.copy();
            this.updateEnchantmentOptions(currentItem);
        }
    }

    private float countPowerBlocks() {
        float power = 0;
        net.minecraft.world.level.Level level = this.blockEntity.getLevel();
        net.minecraft.core.BlockPos center = this.blockEntity.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -2 ; y <= 2; y++) {
                    if (Math.abs(x) < 3 && Math.abs(z) < 3) continue;
                    net.minecraft.core.BlockPos checkPos = center.offset(x, y, z);

                    if(level.getBlockState(checkPos).is(ModBlocks.PRIMORDIAL_OBSIDIAN.get())) {
                        power = power + 2;
                    }
                    if (level.getBlockState(checkPos).is(Blocks.NETHERITE_BLOCK)) {
                       power = power + 1;
                    }
                    if (level.getBlockState(checkPos).is(Blocks.DIAMOND_BLOCK)) {
                        power = power + 0.9f;
                    }
                    if (level.getBlockState(checkPos).is(Blocks.EMERALD_BLOCK)) {
                        power = power + 0.9f;
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
            int cost1 = random.nextInt(15) + 20 + (int)(bonus * 0.65);
            int cost2 = random.nextInt(20) + 35 + (int)(bonus * 0.8);
            int cost3 = random.nextInt(25) + 50 + bonus;

            costs[0] = Math.min(cost0, 47);
            costs[1] = Math.min(cost1, 65);

            if (powerBlocks >= 5.0f || random.nextFloat() > 0.5f) {
                costs[2] = Math.min(cost2, 82);
                if (powerBlocks >= 12.0f || random.nextFloat() > 0.8f) {
                    costs[3] = Math.min(cost3, 100);
                } else {
                    costs[3] = 0;
                }
            } else {
                costs[2] = 0;
                costs[3] = 0;
            }

            this.broadcastFullState();
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < 4) {
            int cost = this.costs[id];

            net.minecraftforge.items.IItemHandler handler = this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (handler == null) return false;

            ItemStack weaponStack = handler.getStackInSlot(0);
            ItemStack lapisStack = handler.getStackInSlot(1);

            int lapisCost = id + 1;

            if (cost > 0 && !weaponStack.isEmpty() &&
                    (player.experienceLevel >= cost || player.getAbilities().instabuild) &&
                    (lapisStack.getItem() == net.minecraft.world.item.Items.LAPIS_BLOCK && lapisStack.getCount() >= lapisCost || player.getAbilities().instabuild)) {

                if (!player.getAbilities().instabuild) {
                    player.onEnchantmentPerformed(weaponStack, cost);
                    handler.extractItem(1, lapisCost, false);
                }

                boolean isBook = weaponStack.is(net.minecraft.world.item.Items.BOOK);

                if (isBook) {
                    java.util.List<net.minecraft.world.item.enchantment.EnchantmentInstance> enchantmentsToApply = new java.util.ArrayList<>();
                    net.minecraft.util.RandomSource random = this.blockEntity.getLevel().random;

                    boolean getCurse = (id == 3) && (random.nextFloat() <= 0.05f);

                    if (getCurse) {
                        java.util.List<net.minecraft.world.item.enchantment.Enchantment> curses = net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS.getValues().stream()
                                .filter(net.minecraft.world.item.enchantment.Enchantment::isCurse)
                                .toList();
                        if (!curses.isEmpty()) {
                            net.minecraft.world.item.enchantment.Enchantment randomCurse = curses.get(random.nextInt(curses.size()));
                            enchantmentsToApply.add(new net.minecraft.world.item.enchantment.EnchantmentInstance(randomCurse, 1));
                        }
                    } else {
                        int numEnchants = (id == 3) ? 4 + random.nextInt(2) : id + 1;
                        java.util.List<net.minecraft.world.item.enchantment.Enchantment> allEnchants = net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS.getValues().stream()
                                .filter(e -> !e.isCurse() && !e.isTreasureOnly())
                                .toList();

                        for (int i = 0; i < numEnchants; i++) {
                            net.minecraft.world.item.enchantment.Enchantment ench = allEnchants.get(random.nextInt(allEnchants.size()));
                            int maxLevel = ench.getMaxLevel();
                            int givenLevel = 1;

                            if (id == 1) givenLevel = Math.min(2, maxLevel);
                            else if (id == 2) givenLevel = Math.max(1, maxLevel - 1);
                            else if (id == 3) givenLevel = maxLevel;

                            boolean alreadyHas = enchantmentsToApply.stream().anyMatch(inst -> inst.enchantment == ench);
                            if (!alreadyHas) {
                                enchantmentsToApply.add(new net.minecraft.world.item.enchantment.EnchantmentInstance(ench, givenLevel));
                            }
                        }
                    }

                    ItemStack enchantedBook = new ItemStack(net.minecraft.world.item.Items.ENCHANTED_BOOK);
                    for (net.minecraft.world.item.enchantment.EnchantmentInstance inst : enchantmentsToApply) {
                        net.minecraft.world.item.EnchantedBookItem.addEnchantment(enchantedBook, inst);
                    }

                    handler.extractItem(0, 1, false);
                    handler.insertItem(0, enchantedBook, false);

                } else {
                    java.util.List<net.minecraft.world.item.enchantment.EnchantmentInstance> enchantmentsToApply = getCustomEnchantments(weaponStack, cost, this.blockEntity.getLevel().random);
                    for (net.minecraft.world.item.enchantment.EnchantmentInstance instance : enchantmentsToApply) {
                        weaponStack.enchant(instance.enchantment, instance.level);
                    }
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

    private java.util.List<net.minecraft.world.item.enchantment.EnchantmentInstance> getCustomEnchantments(net.minecraft.world.item.ItemStack stack, int powerLevel, net.minecraft.util.RandomSource random) {
        java.util.List<net.minecraft.world.item.enchantment.EnchantmentInstance> list = new java.util.ArrayList<>();
        java.util.List<net.minecraft.world.item.enchantment.Enchantment> possibleEnchants = new java.util.ArrayList<>();

        for (net.minecraft.world.item.enchantment.Enchantment enchantment : net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS.getValues()) {
            if (enchantment.isCurse()) continue;

            if (enchantment.canEnchant(stack)) {
                net.minecraft.resources.ResourceLocation registryName = net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                boolean isCustom = registryName != null && registryName.getNamespace().equals(fr.dyooneoxz.neoenchant.NeoEnchant.MODID);

                if (enchantment == fr.dyooneoxz.neoenchant.init.ModEnchantments.HEROS_BANE.get()) {
                    if (powerLevel < 85) continue;
                    if (random.nextFloat() > 0.02f) continue;
                }
                else if (isCustom) {
                    if (powerLevel <= 66) continue;
                    float customChance = powerLevel >= 85 ? 0.05f : 0.02f;
                    if (random.nextFloat() > customChance) continue;
                }

                //  MENDING
                if (enchantment == net.minecraft.world.item.enchantment.Enchantments.MENDING) {
                    float mendingChance = powerLevel <= 30 ? 0.01f :
                            powerLevel <= 48 ? 0.03f :
                                    powerLevel <= 66 ? 0.08f :
                                            powerLevel <= 84 ? 0.15f : 0.25f;
                    if (random.nextFloat() > mendingChance) continue;
                }
                possibleEnchants.add(enchantment);
            }
        }

        if (possibleEnchants.isEmpty()) return list;
        java.util.Collections.shuffle(possibleEnchants, new java.util.Random(random.nextInt()));

        int enchantCount = 1;
        boolean allowMaxLevel = true;
        boolean forceOneMaxLevel = false;
        boolean forceAllMaxLevel = false;

        int roll = random.nextInt(100);

        if (powerLevel <= 30) {
            enchantCount = 1 + random.nextInt(2);
            allowMaxLevel = false;
        }
        else if (powerLevel <= 48) {
            if (roll < 2) {
                enchantCount = 4 + random.nextInt(2);
                allowMaxLevel = false;
            } else if (roll < 10) {
                enchantCount = 2 + random.nextInt(2);
                forceOneMaxLevel = true;
                allowMaxLevel = false;
            } else if (roll < 20) {
                enchantCount = 2 + random.nextInt(2);
                allowMaxLevel = true;
            } else {
                enchantCount = 1 + random.nextInt(2);
                allowMaxLevel = false;
            }
        }
        else if (powerLevel <= 66) {
            if (roll < 5) { // 5%
                enchantCount = 5 + random.nextInt(2);
                allowMaxLevel = false;
            } else if (roll < 20) { // 15%
                enchantCount = 3 + random.nextInt(2);
                forceOneMaxLevel = true;
                allowMaxLevel = false;
            } else if (roll < 50) { // 30%
                enchantCount = 3 + random.nextInt(2);
                allowMaxLevel = true;
            } else { // 50%
                enchantCount = 2 + random.nextInt(2);
                allowMaxLevel = false;
            }
        }
        else if (powerLevel <= 84) {
            enchantCount = 3 + random.nextInt(3); // 3 à 5 enchants
            allowMaxLevel = true;
        }
        else {
            if (roll < 20) {
                enchantCount = 3;
                forceAllMaxLevel = true;
            } else {
                enchantCount = 4 + random.nextInt(4);
                allowMaxLevel = true;
            }
        }

        boolean hasAssignedMax = false;

        for (net.minecraft.world.item.enchantment.Enchantment enchantment : possibleEnchants) {
            if (list.size() >= enchantCount) break;
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

                net.minecraft.resources.ResourceLocation registryName = net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                boolean isCustom = registryName != null && registryName.getNamespace().equals(fr.dyooneoxz.neoenchant.NeoEnchant.MODID);

                // ex: mending
                if (maxLevel > 1) {
                    if (forceAllMaxLevel) {
                        levelToApply = maxLevel;
                    } else if (forceOneMaxLevel && !hasAssignedMax && !isCustom) {
                        levelToApply = maxLevel;
                        hasAssignedMax = true;
                    } else {
                        if (isCustom) {
                            if (powerLevel <= 84) {
                                levelToApply = Math.max(1, random.nextInt(maxLevel));
                            } else {
                                levelToApply = Math.max(1, maxLevel - random.nextInt(2));
                            }
                        } else {
                            if (!allowMaxLevel) {
                                levelToApply = Math.max(1, random.nextInt(maxLevel));
                            } else {
                                if (powerLevel >= 85) {
                                    levelToApply = Math.max(1, maxLevel - random.nextInt(2));
                                } else {
                                    levelToApply = random.nextInt(maxLevel) + 1;
                                }
                            }
                        }
                    }
                }

                list.add(new net.minecraft.world.item.enchantment.EnchantmentInstance(enchantment, levelToApply));
            }
        }
        return list;
    }
}