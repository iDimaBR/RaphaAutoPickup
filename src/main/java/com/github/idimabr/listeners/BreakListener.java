package com.github.idimabr.listeners;

import com.github.idimabr.RaphaAutoPickup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BreakListener implements Listener {


    private final ImmutableList<Material> cropList = ImmutableList.of(
            Material.CROPS,
            Material.SUGAR_CANE,
            Material.NETHER_WARTS,
            Material.COCOA,
            Material.CARROT,
            Material.POTATO,
            Material.CACTUS
    );

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player player = e.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE) return;

        if(e.isCancelled()){
            e.setCancelled(true);
            return;
        }
        Block block = e.getBlock();

        System.out.println(block.getType());

        System.out.println(block.getData());

        ApplicableRegionSet regions = RaphaAutoPickup.getWorldGuard().getRegionManager(player.getWorld()).getApplicableRegions(block.getLocation());

        if(regions.size() > 0 && !regions.allows(DefaultFlag.BLOCK_BREAK)){
            e.setCancelled(true);
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInHand();

        Collection<ItemStack> originalDrops = e.getBlock().getDrops(itemInHand);

        if(!hasSpace(player)){
            player.sendMessage("§cVocê não tem espaço no inventário!");
            e.setCancelled(true);
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.6f, 0.6f);
            return;
        }

        System.out.println(originalDrops);

        if(originalDrops.isEmpty()){
            if(cropList.contains(block.getType())){
                for (ItemStack drop : getCropDrop(block.getType(), block.getData()))
                    inventory.addItem(drop);

                block.setType(Material.AIR);
                return;
            }

            block.setType(Material.AIR);
            return;
        }

        int multiply = 1;

        if(itemInHand != null && itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) && isMultiply(block.getType()))
            multiply = getAmountForDrop(
                    itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)
            );

        Collection<ItemStack> drops = new ArrayList<>(originalDrops);
        for(int i = 0;i < multiply;i++)
            for (ItemStack drop : drops) {
                inventory.addItem(drop);
            }

        player.giveExp(e.getExpToDrop());
        block.setType(Material.AIR);
    }

    private boolean hasSpace(Player player){
        return player.getInventory().firstEmpty() != -1;
    }

    private int getAmountForDrop(int fortuneLevel){
        int bonus = (int) (Math.random() * (fortuneLevel + 2)) - 1;
        if (bonus < 0) {
            bonus = 1;
        }
        return bonus;
    }

    private boolean isMultiply(Material material){
        switch(material){
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case LAPIS_ORE:
            case QUARTZ_ORE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                return true;
        }
        return false;
    }

    private List<ItemStack> getCropDrop(Material material, int data){
        System.out.println(material);
        System.out.println(data);
        int amount = 1;
        List<ItemStack> drops = Lists.newArrayList();
        switch(material){
            case NETHER_WARTS:
                if(data == 3) amount = RandomUtils.nextInt(2, 4);
                drops.add(new ItemStack(Material.NETHER_STALK, amount));
                break;
            case COCOA:
                amount = 3;
                drops.add(new ItemStack(Material.INK_SACK, amount, (short) 3));
                break;
            case CROPS:
                amount = 2;
                drops.add(new ItemStack(Material.SEEDS, amount, (short) 3));
                if(data == 7)
                    drops.add(new ItemStack(Material.WHEAT, 1));
                break;
            case MELON_BLOCK:
                amount = RandomUtils.nextInt(2, 7);
                drops.add(new ItemStack(Material.MELON, amount));
                break;
            case POTATO:
                drops.add(new ItemStack(Material.POTATO_ITEM, amount));
                if(RandomUtils.nextInt(0, 100) < 3)
                    drops.add(new ItemStack(Material.POISONOUS_POTATO, 1));
                break;
            case CARROT:
                if(data == 7) amount = RandomUtils.nextInt(2, 4);
                drops.add(new ItemStack(Material.CARROT_ITEM, amount));
                break;
            default:
                drops.add(new ItemStack(material, amount));
                break;
        }

        System.out.println("Drops finais: " + drops);

        return drops;
    }
}
