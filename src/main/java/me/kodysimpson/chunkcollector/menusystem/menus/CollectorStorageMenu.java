package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.config.Config;
import me.kodysimpson.chunkcollector.menusystem.PaginatedMenu;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.utils.Collector;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CollectorStorageMenu extends PaginatedMenu {

    public CollectorStorageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        if (playerMenuUtility.getType() == Database.CollectionType.DROP) {
            return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Collector-Storage Menu.drop"));
        } else {
            return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Collector-Storage Menu.crop"));
        }
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());

        ArrayList<ItemStack> drops = Utils.combine(collector.getItems());

        if (e.getCurrentItem().getType() == Material.BARRIER) {

            new CollectorMenu(playerMenuUtility).open();

        } else if (e.getCurrentItem().getType() == Material.FIREWORK_ROCKET) {

            Utils.sellAllItems(collector);

            //Reload the page
            super.open();

        } else if (e.getCurrentItem().getType() == Material.DARK_OAK_BUTTON) {
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")) {
                if (page == 0) {
                    p.sendMessage(Config.FIRST_PAGE);
                } else {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")) {
                if (!((index + 1) >= drops.size())) {
                    page = page + 1;
                    super.open();
                } else {
                    p.sendMessage(Config.LAST_PAGE);
                }
            }
        }
    }

    @Override
    public void setMenuItems() {

        addMenuBorder();

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());

        ArrayList<ItemStack> drops = Utils.combine(collector.getItems());

        if (drops != null && !drops.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= drops.size()) break;
                if (drops.get(index) != null) {
                    inventory.addItem(drops.get(index));
                }
            }
        }


    }
}
