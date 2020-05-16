package me.kodysimpson.chunkchest.menu.mobdrops;

import me.kodysimpson.chunkchest.menu.Menu;
import me.kodysimpson.chunkchest.menu.PlayerMenuUtility;
import me.kodysimpson.chunkchest.utils.Collector;
import me.kodysimpson.chunkchest.utils.Database;
import me.kodysimpson.chunkchest.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class DropUpgradeMenu extends Menu {

    @Override
    public String getMenuName() {
        return "Collector Upgrade Options";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e, PlayerMenuUtility playerMenuUtility) {

        Player p = (Player) e.getWhoClicked();

        switch (e.getCurrentItem().getType()){
            case OAK_FENCE_GATE:



                break;
        }

    }

    @Override
    public void setMenuItems(PlayerMenuUtility playerMenuUtility) {

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());

        ItemStack capacity = new ItemStack(Material.OAK_FENCE_GATE);
        ItemMeta capacityMeta = capacity.getItemMeta();
        capacityMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Storage Capacity");
        ArrayList<String> capacityLore = new ArrayList<>();
        capacityLore.add(ChatColor.GOLD + "The storage capacity is the ");
        capacityLore.add(ChatColor.GOLD + "max amount of items that ");
        capacityLore.add(ChatColor.GOLD + "can be stored in the collector ");
        capacityLore.add(ChatColor.GOLD + "before the items are sold.");
        capacityLore.add(ChatColor.WHITE + "------------------------");
        capacityLore.add(ChatColor.RED + "Current Capacity: " + Utils.getCapacityAmount(collector.getStorageCapacity()));
        capacityLore.add(ChatColor.RED + "Next Tier: " + Utils.getNextCapacity(collector.getStorageCapacity()));
        capacityLore.add(ChatColor.WHITE + "------------------------");
        capacityLore.add(ChatColor.BLUE + "(Click To Upgrade) $" + Utils.getCapacityUpgradePrice(collector.getStorageCapacity() + 1));
        capacityMeta.setLore(capacityLore);
        capacity.setItemMeta(capacityMeta);

        inventory.setItem(22, capacity);
    }
}
