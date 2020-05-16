package me.kodysimpson.chunkchest.menu.mobdrops;

import me.kodysimpson.chunkchest.menu.PaginatedMenu;
import me.kodysimpson.chunkchest.menu.PlayerMenuUtility;
import me.kodysimpson.chunkchest.utils.Collector;
import me.kodysimpson.chunkchest.utils.Database;
import me.kodysimpson.chunkchest.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DropInventoryMenu extends PaginatedMenu {

    @Override
    public String getMenuName() {
        return "Collected Mob Drops";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e, PlayerMenuUtility playerMenuUtility) {
        Player p = (Player) e.getWhoClicked();

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());
        ArrayList<ItemStack> drops = Utils.combine(collector.getItems());

        if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

            new DropCollectorMenu().open(p);

        }else if(e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)){
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")){
                if (page == 0){
                    p.sendMessage(ChatColor.GRAY + "You are on the first page.");
                }else{
                    page = page - 1;
                    page = page - 1;
                    super.open(p);
                }
            }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                if (!((index + 1) >= drops.size())){
                    page = page + 1;
                    super.open(p);
                }else{
                    p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                }
            }
        }
    }

    @Override
    public void setMenuItems(PlayerMenuUtility playerMenuUtility) {

        addMenuBorder();

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());
        ArrayList<ItemStack> drops = Utils.combine(collector.getItems());

        if(drops != null && !drops.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= drops.size()) break;
                if (drops.get(index) != null){
                    inventory.addItem(drops.get(index));
                }
            }
        }


    }
}
