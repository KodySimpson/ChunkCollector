package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.menusystem.Menu;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.utils.Collector;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CollectorMenu extends Menu {

    public CollectorMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        if (playerMenuUtility.getType() == Database.CollectionType.DROP) {
            return "Mob Drop Collector";
        } else {
            return "Crop Collector";
        }
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        switch (e.getCurrentItem().getType()) {
            case CHEST:
                new CollectorStorageMenu(playerMenuUtility).open();
                break;
            case EMERALD:
                new CollectorUpgradeMenu(playerMenuUtility).open();
                break;
            case BARRIER:
                e.getWhoClicked().closeInventory();
                break;
        }

    }

    @Override
    public void setMenuItems() {

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());

        if (collector.getType() == Database.CollectionType.DROP) {

            inventory.setItem(11, makeItem(Material.CHEST, ChatColor.RED + "" + ChatColor.BOLD + "View Drop Storage",
                    ChatColor.WHITE + "----------------------------",
                    ChatColor.GRAY + "Amount Stored: " + ChatColor.GREEN + collector.getItems().stream().mapToInt(ItemStack::getAmount).sum() + "/" + Utils.getCapacityAmount(collector.getStorageCapacity())));

            inventory.setItem(13, makeItem(Material.DIAMOND_SWORD, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Mob Drop Collector",
                    ChatColor.YELLOW + "Automatically collects mob drops.",
                    ChatColor.WHITE + "----------------------------",
                    ChatColor.GRAY + "Total Sold: " + ChatColor.BLUE + collector.getSold(),
                    ChatColor.GRAY + "Total Earned: $" + ChatColor.GREEN + String.format("%.2f",  collector.getEarned())));

        } else {

            inventory.setItem(11, makeItem(Material.CHEST, ChatColor.RED + "" + ChatColor.BOLD + "View Crop Storage",
                    ChatColor.WHITE + "----------------------------",
                    ChatColor.GRAY + "Amount Stored: " + ChatColor.GREEN + collector.getItems().stream().mapToInt(ItemStack::getAmount).sum() + "/" + Utils.getCapacityAmount(collector.getStorageCapacity())));

            inventory.setItem(13, makeItem(Material.BREAD, ChatColor.YELLOW + "" + ChatColor.BOLD + "Food Collection",
                    ChatColor.GREEN + "Automatically collects fully ",
                    ChatColor.GREEN + "grown food.",
                    ChatColor.WHITE + "----------------------------",
                    ChatColor.GRAY + "Total Sold: " + ChatColor.BLUE + collector.getSold(),
                    ChatColor.GRAY + "Total Earned: $" + ChatColor.GREEN + String.format("%.2f",  collector.getEarned())));
        }

        inventory.setItem(15, makeItem(Material.EMERALD, ChatColor.GREEN + "" + ChatColor.BOLD + "Upgrade Options"));

        inventory.setItem(31, makeItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "Nevermind"));

        setFillerGlass();
    }

}
