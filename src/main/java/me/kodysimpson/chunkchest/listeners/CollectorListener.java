package me.kodysimpson.chunkchest.listeners;

import me.kodysimpson.chunkchest.ChunkCollector;
import me.kodysimpson.chunkchest.menu.mobdrops.DropCollectorMenu;
import me.kodysimpson.chunkchest.menu.Menu;
import me.kodysimpson.chunkchest.menu.PlayerMenuUtility;
import me.kodysimpson.chunkchest.utils.Database;
import me.kodysimpson.chunkchest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataType;

public class CollectorListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){


        if (e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "drop-collector"), PersistentDataType.STRING)){

            //Check the chunk to see if there is already a collector
            if (Utils.isChunkTaken(e.getBlockPlaced().getChunk())){
                System.out.println("already a collector in this chunk");
                e.setCancelled(true);
            }else{
                TileState thing = (TileState) e.getBlockPlaced().getState();

                int id = Database.createCollector(e.getPlayer().getUniqueId(), Database.CollectionType.DROP);

                if (id != 0){
                    thing.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER, id);

                    thing.update();

                    Bukkit.getServer().getWorld("world").spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, e.getBlockPlaced().getLocation(), 50);
                }

            }


        }

    }

    @EventHandler
    public void onCollectorOpen(PlayerInteractEvent e){

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getState() instanceof TileState){
                TileState tileState = (TileState) e.getClickedBlock().getState();

                if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)){
                    e.setCancelled(true);

                    int collectorID = tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER);

                    PlayerMenuUtility playerMenuUtility = ChunkCollector.getPlayerMenuUtility(e.getPlayer());
                    playerMenuUtility.setCollectorID(collectorID);

                    DropCollectorMenu menu = new DropCollectorMenu();
                    menu.open(e.getPlayer());

                }

            }
        }


    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){

        Player p = (Player) e.getWhoClicked();

        //Make sure the player has a menu system object
        ChunkCollector.getPlayerMenuUtility(p);
        //Get the player's LockManagerMenu
        PlayerMenuUtility playerMenuUtility = ChunkCollector.getPlayerMenuUtility(p);

        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Menu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            Menu menu = (Menu) holder;
            menu.handleMenu(e, playerMenuUtility);
        }

    }

}
