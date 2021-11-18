package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.utils.CollectionType;
import me.kodysimpson.chunkcollector.utils.Utils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ConfirmBuyMenu extends Menu {

    private final CollectionType buyType;

    public ConfirmBuyMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        this.buyType = playerMenuUtility.getData(MenuData.BUY_TYPE, CollectionType.class);
    }

    @Override
    public String getMenuName() {
        if (buyType == CollectionType.DROP) {
            return ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Confirm-Buy Menu.drop"));
        } else if (buyType == CollectionType.CROP) {
            return ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Confirm-Buy Menu.crop"));
        } else if (buyType == CollectionType.ORE) {
            return ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Confirm-Buy Menu.ore"));
        }
        return "pickle";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        ItemStack collector = null;
        Player p = playerMenuUtility.getOwner();

        if ((e.getCurrentItem().getType() == Material.DIAMOND_SWORD) || (e.getCurrentItem().getType() == Material.BREAD) || (e.getCurrentItem().getType() == Material.GOLD_ORE)) {

            double cost = 0;

            if (buyType == CollectionType.DROP) {
                cost = ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.drop");
            } else if (buyType == CollectionType.CROP) {
                cost = ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.crop");
            } else if (buyType == CollectionType.ORE) {
                cost = ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.ore");
            }

            //see if they have enough
            if (ChunkCollector.getEconomy().getBalance(playerMenuUtility.getOwner()) >= cost){

                EconomyResponse response = ChunkCollector.getEconomy().withdrawPlayer(playerMenuUtility.getOwner(), cost);

                if (response.transactionSuccess()){

                    if (buyType == CollectionType.DROP) {
                        p.sendMessage(ChatColor.GREEN + "Purchased Drop Collector for $" + ChatColor.YELLOW + cost);
                        collector = Utils.makeCollector(p, CollectionType.DROP);
                    } else if (buyType == CollectionType.CROP) {
                        p.sendMessage(ChatColor.GREEN + "Purchased Crop Collector for $" + ChatColor.YELLOW + cost);
                        collector = Utils.makeCollector(p, CollectionType.CROP);
                    } else if (buyType == CollectionType.ORE) {
                        p.sendMessage(ChatColor.GREEN + "Purchased Ore Collector for $" + ChatColor.YELLOW + cost);
                        collector = Utils.makeCollector(p, CollectionType.ORE);
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

        if (buyType == CollectionType.DROP) {

            inventory.setItem(3, makeItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "" + ChatColor.BOLD + "Purchase",
                    ChatColor.AQUA + "Buy Drop Collector for",
                    ChatColor.GOLD + "$" + ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.drop")));

        } else if (buyType == CollectionType.CROP) {

            inventory.setItem(3, makeItem(Material.BREAD, ChatColor.GREEN + "" + ChatColor.BOLD + "Purchase",
                    ChatColor.AQUA + "Buy Crop Collector for",
                    ChatColor.GOLD + "$" + ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.crop")));

        } else if (buyType == CollectionType.ORE) {

            inventory.setItem(3, makeItem(Material.BREAD, ChatColor.GREEN + "" + ChatColor.BOLD + "Purchase",
                    ChatColor.AQUA + "Buy Ore Collector for",
                    ChatColor.GOLD + "$" + ChunkCollector.getPlugin().getConfig().getDouble("collector-cost.ore")));

        }

        inventory.setItem(5, makeItem(Material.BARRIER, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Cancel"));

        setFillerGlass();
    }
}
