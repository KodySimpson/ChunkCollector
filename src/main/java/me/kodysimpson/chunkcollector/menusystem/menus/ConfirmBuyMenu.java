package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.menusystem.Menu;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.utils.Collector;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ConfirmBuyMenu extends Menu {

    public ConfirmBuyMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        if (playerMenuUtility.getBuyType() == Database.CollectionType.DROP){
            return "Buy Drop Collector?";
        }else{
            return "Buy Crop Collector?";
        }
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        ItemStack collector;
        Player p = playerMenuUtility.getOwner();

        if ((e.getCurrentItem().getType() == Material.DIAMOND_SWORD) || (e.getCurrentItem().getType() == Material.BREAD)){

            double cost = 0;

            if (playerMenuUtility.getBuyType() == Database.CollectionType.DROP){
                cost = ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.drop");
            }else{
                cost = ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.crop");
            }

            //see if they have enough
            if (ChunkCollector.getEconomy().getBalance(playerMenuUtility.getOwner()) >= cost){

                EconomyResponse response = ChunkCollector.getEconomy().withdrawPlayer(playerMenuUtility.getOwner(), cost);

                if (response.transactionSuccess()){

                    if (playerMenuUtility.getBuyType() == Database.CollectionType.DROP){
                        p.sendMessage(ChatColor.GREEN + "Purchased Drop Collector for $" + ChatColor.YELLOW + cost);
                        collector = Utils.makeCollector(p, Database.CollectionType.DROP);
                    }else{
                        p.sendMessage(ChatColor.GREEN + "Purchased Crop Collector for $" + ChatColor.YELLOW + cost);
                        collector = Utils.makeCollector(p, Database.CollectionType.CROP);
                    }

                    if (p.getInventory().firstEmpty() == -1){
                        //Drop at their feet
                        p.sendMessage(ChatColor.RED + "Inventory full, dropping at feet.");
                        p.getWorld().dropItem(p.getLocation(), collector);
                    }else{
                        p.getInventory().addItem(collector);
                    }

                    p.sendMessage(ChatColor.GRAY + "Place collector in an empty chunk to start collecting.");
                    p.sendMessage(ChatColor.GRAY + "When the collector is full, it will sell all collected items.");
                    p.closeInventory();

                }else{
                    playerMenuUtility.getOwner().sendMessage(ChatColor.RED + "Unable to complete purchase");
                }

            }else{
                playerMenuUtility.getOwner().sendMessage(ChatColor.RED + "You don't have sufficient funds.");
            }


        }else if(e.getCurrentItem().getType() == Material.BARRIER){
            new BuyMenu(playerMenuUtility).open();
        }

    }

    @Override
    public void setMenuItems() {

        if (playerMenuUtility.getBuyType() == Database.CollectionType.DROP){

            ItemStack yes = new ItemStack(Material.DIAMOND_SWORD, 1);
            ItemMeta yes_meta = yes.getItemMeta();
            yes_meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Purchase");
            ArrayList<String> yes_lore = new ArrayList<>();
            yes_lore.add(ChatColor.AQUA + "Buy Drop Collector for");
            yes_lore.add(ChatColor.GOLD + "$" + ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.drop"));
            yes_meta.setLore(yes_lore);
            yes.setItemMeta(yes_meta);

            inventory.setItem(3, yes);
        }else if(playerMenuUtility.getBuyType() == Database.CollectionType.CROP){

            ItemStack yes = new ItemStack(Material.BREAD, 1);
            ItemMeta yes_meta = yes.getItemMeta();
            yes_meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Purchase");
            ArrayList<String> yes_lore = new ArrayList<>();
            yes_lore.add(ChatColor.AQUA + "Buy Crop Collector for");
            yes_lore.add(ChatColor.GOLD + "$" + ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.crop"));
            yes_meta.setLore(yes_lore);
            yes.setItemMeta(yes_meta);

            inventory.setItem(3, yes);
        }

        ItemStack no = new ItemStack(Material.BARRIER, 1);
        ItemMeta no_meta = no.getItemMeta();
        no_meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Cancel");
        no.setItemMeta(no_meta);

        inventory.setItem(5, no);

        setFillerGlass();
    }
}
