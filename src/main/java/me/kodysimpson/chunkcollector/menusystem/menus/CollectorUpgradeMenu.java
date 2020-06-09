package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.config.Config;
import me.kodysimpson.chunkcollector.menusystem.Menu;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.utils.Collector;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CollectorUpgradeMenu extends Menu {

    public CollectorUpgradeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Collector-Upgrade Menu"));
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());

        switch (e.getCurrentItem().getType()){
            case OAK_FENCE_GATE:

                //See if they are at the max tier or not
                if (Utils.getNextCapacity(collector.getStorageCapacity()).equalsIgnoreCase("AT MAX")){
                    p.sendMessage(Config.MAX_STORAGE);
                }else{
                    //check to see if they can afford the next tier
                    if (ChunkCollector.getEconomy().getBalance(p) >= Utils.getCapacityUpgradePrice(collector.getStorageCapacity())) {

                        //withdraw the money
                        EconomyResponse response = ChunkCollector.getEconomy().withdrawPlayer(p, Utils.getCapacityUpgradePrice(collector.getStorageCapacity()));

                        if (response.type == EconomyResponse.ResponseType.FAILURE) {
                            System.out.println("TRANSACTION ERROR");
                            System.out.println(response.errorMessage);
                        } else {

                            p.sendMessage(Config.UPGRADE_STORAGE);
                            p.sendMessage(ChatColor.GREEN + "$" + Utils.getCapacityUpgradePrice(collector.getStorageCapacity()) + ChatColor.YELLOW + " has been deducted from your balance.");

                            //they have enough, do the upgrade.
                            collector.setStorageCapacity(collector.getStorageCapacity() + 1);
                            Database.updateCollector(collector);

                            //reload the gui
                            new CollectorUpgradeMenu(playerMenuUtility).open();
                        }

                    } else {
                        p.sendMessage(Config.CANT_AFFORD_UPGRADE);
                    }
                }

                break;
            case EXPERIENCE_BOTTLE:

                //see if they are already at max tier
                if (collector.getFortuneLevel() == 3) {
                    p.sendMessage(Config.MAX_FORTUNE);
                } else {
                    if (ChunkCollector.getEconomy().getBalance(p) >= Utils.getFortuneUpgradePrice(collector.getFortuneLevel())) {

                        //withdraw money
                        EconomyResponse response = ChunkCollector.getEconomy().withdrawPlayer(p, Utils.getFortuneUpgradePrice(collector.getFortuneLevel()));
                        if (response.type == EconomyResponse.ResponseType.FAILURE) {
                            System.out.println("TRANSACTION ERROR");
                            System.out.println(response.errorMessage);
                        } else {

                            p.sendMessage(Config.UPGRADE_FORTUNE);
                            p.sendMessage(ChatColor.GREEN + "$" + Utils.getFortuneUpgradePrice(collector.getFortuneLevel()) + ChatColor.YELLOW + " has been deducted from your balance.");

                            //do the upgrade
                            collector.setFortuneLevel(collector.getFortuneLevel() + 1);
                            Database.updateCollector(collector);

                            //reload the gui
                            new CollectorUpgradeMenu(playerMenuUtility).open();
                        }

                    } else {
                        p.sendMessage(Config.CANT_AFFORD_UPGRADE);
                    }
                }

                break;
            case BARRIER:
                new CollectorMenu(playerMenuUtility).open();
                break;
        }

    }

    @Override
    public void setMenuItems() {

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());

        if (collector.getType() == Database.CollectionType.CROP) {

            ItemStack fortune = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
            ItemMeta fortuneUpgrade = fortune.getItemMeta();
            fortuneUpgrade.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Fortune");
            ArrayList<String> fortuneLore = new ArrayList<>();
            fortuneLore.add(ChatColor.LIGHT_PURPLE + "Fortune increases the amount of ");
            fortuneLore.add(ChatColor.LIGHT_PURPLE + "produce dropped by crops.");
            fortuneLore.add(ChatColor.WHITE + "------------------------");
            fortuneLore.add(ChatColor.GREEN + "Current Fortune: " + ChatColor.AQUA + collector.getFortuneLevel() + "/3");
            fortuneLore.add(ChatColor.WHITE + "------------------------");
            if (collector.getFortuneLevel() == 3) {
                fortuneLore.add(ChatColor.GOLD + "AT MAX LEVEL");
            } else {
                fortuneLore.add("(Click To Upgrade) $" + Utils.getFortuneUpgradePrice(collector.getFortuneLevel()));
            }
            fortuneUpgrade.setLore(fortuneLore);
            fortune.setItemMeta(fortuneUpgrade);

            inventory.setItem(20, fortune);
        }

        ItemStack capacity = new ItemStack(Material.OAK_FENCE_GATE);
        ItemMeta capacityMeta = capacity.getItemMeta();
        capacityMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Storage Capacity");
        ArrayList<String> capacityLore = new ArrayList<>();
        capacityLore.add(ChatColor.GOLD + "The storage capacity is the ");
        capacityLore.add(ChatColor.GOLD + "max amount of items that ");
        capacityLore.add(ChatColor.GOLD + "can be stored in the collector ");
        capacityLore.add(ChatColor.GOLD + "before the items are sold.");
        capacityLore.add(ChatColor.WHITE + "------------------------");
        capacityLore.add(ChatColor.RED + "Current Capacity: " + ChatColor.GREEN + Utils.getCapacityAmount(collector.getStorageCapacity()));
        capacityLore.add(ChatColor.WHITE + "------------------------");

        if (Utils.getNextCapacity(collector.getStorageCapacity()).equalsIgnoreCase("AT MAX")){
            capacityLore.add(ChatColor.GOLD + "AT MAX LEVEL");
        }else{
            capacityLore.add(ChatColor.YELLOW + "Next Tier: " + ChatColor.GREEN + Utils.getNextCapacity(collector.getStorageCapacity()));
            capacityLore.add(ChatColor.BLUE + "(Click To Upgrade) $" + Utils.getCapacityUpgradePrice(collector.getStorageCapacity()));
        }

        capacityMeta.setLore(capacityLore);
        capacity.setItemMeta(capacityMeta);

        inventory.setItem(22, capacity);

        inventory.setItem(40, makeItem(Material.BARRIER, ChatColor.DARK_RED + "Close"));

        setFillerGlass();
    }
}
