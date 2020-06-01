package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.menusystem.Menu;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.utils.Database;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BuyMenu extends Menu {

    public BuyMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Purchase Collector";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        switch (e.getCurrentItem().getType()) {
            case DIAMOND_SWORD:

                playerMenuUtility.setBuyType(Database.CollectionType.DROP);
                new ConfirmBuyMenu(playerMenuUtility).open();

                break;
            case BREAD:

                playerMenuUtility.setBuyType(Database.CollectionType.CROP);
                new ConfirmBuyMenu(playerMenuUtility).open();

                break;
            case BARRIER:

                playerMenuUtility.getOwner().closeInventory();
                break;
        }

    }

    @Override
    public void setMenuItems() {

        inventory.setItem(11, makeItem(Material.DIAMOND_SWORD, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Mob Drop Collector",
                ChatColor.YELLOW + "Automatically collects mob drops."));

        inventory.setItem(13, makeItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "Nevermind"));

        inventory.setItem(15, makeItem(Material.BREAD, ChatColor.YELLOW + "" + ChatColor.BOLD + "Food Collector",
                ChatColor.GREEN + "Automatically collects fully ",
                ChatColor.GREEN + "grown crops."));

        setFillerGlass();
    }

}
