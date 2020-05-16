package me.kodysimpson.chunkchest.menu;

import me.kodysimpson.chunkchest.ChunkCollector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    public Menu() {

    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e, PlayerMenuUtility playerMenuUtility);

    public abstract void setMenuItems(PlayerMenuUtility playerMenuUtility);

    public void open(Player p) { //Create the inventory and open it for the provided player
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems(ChunkCollector.getPlayerMenuUtility(p));

        p.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}