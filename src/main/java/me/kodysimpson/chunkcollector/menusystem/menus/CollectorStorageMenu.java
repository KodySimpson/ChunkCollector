package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.database.Database;
import me.kodysimpson.chunkcollector.model.Collector;
import me.kodysimpson.chunkcollector.utils.Utils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.PaginatedMenu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class CollectorStorageMenu extends PaginatedMenu {

    private final Collector collector;

    public CollectorStorageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        collector = Database.getCollectorDataAccess().findById(playerMenuUtility.getData(MenuData.COLLECTOR_ID, Integer.class));
    }

    @Override
    public <T> List<T> getData() {
        return (List<T>) Utils.combine(collector.getItems());
    }

    @Override
    public void loopCode(Object object) {
        inventory.addItem((ItemStack) object);
    }

    @Override
    public String getMenuName() {

        return switch (collector.getType()) {
            case DROP -> ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Collector-Storage Menu.drop"));
            case CROP -> ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Collector-Storage Menu.crop"));
            case ORE -> ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Menu Titles.Collector-Storage Menu.ore"));
        };

    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems() {

        HashMap<Integer, ItemStack> borderItems = new HashMap<>();
        ItemStack sellAllItems = makeItem(Material.FIREWORK_ROCKET, ColorTranslator.translateColorCodes("&e&lSell All Items"));
        borderItems.put(46, sellAllItems);

        return borderItems;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerException, MenuManagerNotSetupException {

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType() == Material.BARRIER) {

            new CollectorMenu(playerMenuUtility).open();

        } else if (e.getCurrentItem().getType() == Material.FIREWORK_ROCKET) {

            Utils.sellAllItems(collector);

            //Reload the page
            reload();

        } else if (e.getCurrentItem().getType() == Material.DARK_OAK_BUTTON) {

            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")) {

                if (!prevPage()) {
                    p.sendMessage(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Messages.first-page")));
                }
            } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")) {
                if (!nextPage()) {
                    p.sendMessage(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Messages.last-page")));
                }
            }
        }
    }
}

