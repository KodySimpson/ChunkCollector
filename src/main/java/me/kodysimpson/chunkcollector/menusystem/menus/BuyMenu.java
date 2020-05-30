package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.menusystem.Menu;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.utils.Database;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

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

        switch (e.getCurrentItem().getType()){
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

        ItemStack mobdropCollection = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta dropMeta = mobdropCollection.getItemMeta();
        dropMeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Mob Drop Collector");
        ArrayList<String> dropLore = new ArrayList<>();
        dropLore.add(ChatColor.YELLOW + "Automatically collects mob drops.");
        dropMeta.setLore(dropLore);
        mobdropCollection.setItemMeta(dropMeta);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nevermind");
        close.setItemMeta(closeMeta);

        ItemStack foodCollection = new ItemStack(Material.BREAD, 1);
        ItemMeta foodMeta = foodCollection.getItemMeta();
        foodMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Food Collector");
        ArrayList<String> foodLore = new ArrayList<>();
        foodLore.add(ChatColor.GREEN + "Automatically collects fully ");
        foodLore.add(ChatColor.GREEN + "grown crops.");
        foodMeta.setLore(foodLore);
        foodCollection.setItemMeta(foodMeta);

        inventory.setItem(11, mobdropCollection);
        inventory.setItem(13, close);
        inventory.setItem(15, foodCollection);

        setFillerGlass();
    }

}
