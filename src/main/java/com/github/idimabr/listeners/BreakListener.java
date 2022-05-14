package com.github.idimabr.listeners;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.util.ArrayList;
import java.util.Collection;

public class BreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(e.isCancelled()) return;

        Player player = e.getPlayer();
        Block block = e.getBlock();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInHand();

        e.setCancelled(true);

        Collection<ItemStack> originalDrops = e.getBlock().getDrops(itemInHand);
        if(originalDrops.isEmpty()){
            block.setType(Material.AIR);
            return;
        }

        if(!hasSpace(player)){
            player.sendMessage("§cVocê não tem espaço no inventário!");
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.6f, 0.6f);
            return;
        }

        int multiply = 1;

        if(itemInHand != null && itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            multiply = getAmountForDrop(
                    itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)
            );

        Collection<ItemStack> drops = new ArrayList<>(originalDrops);
        for(int i = 0;i < multiply;i++)
            for (ItemStack drop : drops)
                inventory.addItem(drop);


        if(itemInHand != null)
            if(itemInHand.containsEnchantment(Enchantment.DURABILITY)){
                int durabilityLevel = itemInHand.getEnchantmentLevel(Enchantment.DURABILITY);
                if( ( 60 + (40 / (durabilityLevel + 1)) ) > RandomUtils.nextInt(100))
                    changeDurability(player);
            }else{
                changeDurability(player);
            }

        player.giveExp(e.getExpToDrop());
        block.setType(Material.AIR);
    }

    private boolean hasSpace(Player player){
        return player.getInventory().firstEmpty() != -1;
    }

    private void changeDurability(Player player){
        ItemStack item = player.getItemInHand().clone();
        item.setDurability((short) (item.getDurability() + 1));
        player.setItemInHand(item);
        player.updateInventory();
    }

    private int getAmountForDrop(int fortuneLevel){
        int bonus = (int) (Math.random() * (fortuneLevel + 2)) - 1;
        if (bonus < 0) {
            bonus = 1;
        }
        return bonus;
    }
}
