package me.kodysimpson.chunkcollector.menusystem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public void addMenuBorder(){

        ItemStack sell = new ItemStack(Material.FIREWORK_ROCKET,1);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Sell All");
        ArrayList<String> sellLore = new ArrayList<>();
        sellLore.add(ChatColor.YELLOW + "Getting impatient?");
        sellLore.add(ChatColor.YELLOW + "Sell all items in the collector.");
        sellMeta.setLore(sellLore);
        sell.setItemMeta(sellMeta);

        inventory.setItem(46, sell);

        ItemStack left = new ItemStack(Material.DARK_OAK_BUTTON, 1);
        ItemMeta leftmeta = left.getItemMeta();
        leftmeta.setDisplayName(ChatColor.GREEN + "Left");
        left.setItemMeta(leftmeta);

        inventory.setItem(48, left);

        ItemStack close = new ItemStack(Material.BARRIER, 1);
        ItemMeta close_meta = close.getItemMeta();
        close_meta.setDisplayName(ChatColor.DARK_RED + "Close");
        close.setItemMeta(close_meta);

        inventory.setItem(49, close);

        ItemStack right = new ItemStack(Material.DARK_OAK_BUTTON, 1);
        ItemMeta rightmeta = right.getItemMeta();
        rightmeta.setDisplayName(ChatColor.GREEN + "Right");
        right.setItemMeta(rightmeta);

        inventory.setItem(50, right);

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