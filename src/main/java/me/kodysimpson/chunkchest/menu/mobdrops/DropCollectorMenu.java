package me.kodysimpson.chunkchest.menu.mobdrops;

import me.kodysimpson.chunkchest.menu.Menu;
import me.kodysimpson.chunkchest.menu.PlayerMenuUtility;
import me.kodysimpson.chunkchest.utils.Collector;
import me.kodysimpson.chunkchest.utils.Database;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class DropCollectorMenu extends Menu {

    @Override
    public String getMenuName() {
        return "Mob Drop Collector";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e, PlayerMenuUtility playerMenuUtility) {

        Player p = (Player) e.getWhoClicked();

        switch (e.getCurrentItem().getType()){
            case CHEST:
                new DropInventoryMenu().open(p);
                break;
            case EMERALD:
                new DropUpgradeMenu().open(p);
                break;
        }

    }

    @Override
    public void setMenuItems(PlayerMenuUtility playerMenuUtility) {

//        ItemStack foodCollection = new ItemStack(Material.BREAD, 1);
//        ItemMeta foodMeta = foodCollection.getItemMeta();
//        foodMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Food Collection");
//        ArrayList<String> foodLore = new ArrayList<>();
//        foodLore.add(ChatColor.GREEN + "Automatically collects fully ");
//        foodLore.add(ChatColor.GREEN + "grown food.");
//        foodMeta.setLore(foodLore);
//        foodCollection.setItemMeta(foodMeta);

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());

        ItemStack viewDrops = new ItemStack(Material.CHEST, 1);
        ItemMeta viewMeta = viewDrops.getItemMeta();
        viewMeta.setDisplayName(ChatColor.RED + "View Drops");
        viewDrops.setItemMeta(viewMeta);

        ItemStack mobdropCollection = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta dropMeta = mobdropCollection.getItemMeta();
        dropMeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Mob Drop Collector");
        ArrayList<String> dropLore = new ArrayList<>();
        dropLore.add(ChatColor.YELLOW + "Automatically collects mob drops.");
        dropLore.add(ChatColor.WHITE + "----------------------------");
        dropLore.add(ChatColor.GRAY + "Total Sold: " + ChatColor.BLUE + collector.getSold());
        dropLore.add(ChatColor.GRAY + "Total Earned: $" + ChatColor.GREEN + collector.getEarned());
        dropMeta.setLore(dropLore);
        mobdropCollection.setItemMeta(dropMeta);

        ItemStack upgrade = new ItemStack(Material.EMERALD, 1);
        ItemMeta upgradeMeta = upgrade.getItemMeta();
        upgradeMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Upgrade Options");
        upgrade.setItemMeta(upgradeMeta);

        inventory.setItem(11, viewDrops);
        inventory.setItem(13, mobdropCollection);
        inventory.setItem(15, upgrade);

    }

}
