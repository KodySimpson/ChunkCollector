package me.kodysimpson.chunkcollector.menusystem;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public void addMenuBorder() {

        inventory.setItem(46, makeItem(Material.FIREWORK_ROCKET, ChatColor.GREEN + "" + ChatColor.BOLD + "Sell All",
                ChatColor.YELLOW + "Getting impatient?",
                ChatColor.YELLOW + "Sell all items in the collector."));

        inventory.setItem(48, makeItem(Material.DARK_OAK_BUTTON, ChatColor.GREEN + "Left"));

        inventory.setItem(49, makeItem(Material.BARRIER, ChatColor.DARK_RED + "Close"));

        inventory.setItem(50, makeItem(Material.DARK_OAK_BUTTON, ChatColor.GREEN + "Right"));

        inventory.setItem(52, makeItem(Material.OAK_SIGN, ChatColor.GOLD + "" + ChatColor.BOLD + "Info",
                ChatColor.GREEN + "Every item that the collector ",
                ChatColor.GREEN + "has picked up is stored here.",
                ChatColor.GREEN + "Once the storage capacity is ",
                ChatColor.GREEN + "reached, all items are sold."));

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
        }

        inventory.setItem(17, super.FILLER_GLASS);
        inventory.setItem(18, super.FILLER_GLASS);
        inventory.setItem(26, super.FILLER_GLASS);
        inventory.setItem(27, super.FILLER_GLASS);
        inventory.setItem(35, super.FILLER_GLASS);
        inventory.setItem(36, super.FILLER_GLASS);

        for (int i = 44; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
        }
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}