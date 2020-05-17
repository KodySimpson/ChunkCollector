package me.kodysimpson.chunkcollector.listeners;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.menusystem.Menu;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.menusystem.menus.CollectorMenu;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class CollectorListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){


        if (e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)){

            //Check the chunk to see if there is already a collector
            if (Utils.isChunkTaken(e.getBlockPlaced().getChunk())){
                System.out.println("already a collector in this chunk");
                e.setCancelled(true);
            }else{
                TileState thing = (TileState) e.getBlockPlaced().getState();

                thing.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER, e.getItemInHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                thing.update();

                e.getBlockPlaced().getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, e.getBlockPlaced().getLocation(), 50);

            }


        }

    }

    @EventHandler
    public void onCollectorBreak(BlockDropItemEvent e){

        if (e.getBlockState() instanceof TileState){
            TileState tileState = (TileState) e.getBlockState();

            if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)){

                if (e.getItems().size() == 0){ //means they are in creative or something

                    //delete the collector from the DB
                    Database.deleteCollector(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                }else{
                    e.getItems().stream()
                            .forEach(item -> {
                                ItemMeta itemMeta = item.getItemStack().getItemMeta();

                                //get the collector-id of the block broken and put it into the item dropped
                                itemMeta.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER, tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                                item.getItemStack().setItemMeta(itemMeta);
                            });
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

                    new CollectorMenu(playerMenuUtility).open();

                }

            }
        }


    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){

        Player p = (Player) e.getWhoClicked();

        //Make sure the player has a menu system object
        ChunkCollector.getPlayerMenuUtility(p);

        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Menu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            Menu menu = (Menu) holder;
            menu.handleMenu(e);
        }

    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent e){

        if (e.getBlock().getBlockData() instanceof Ageable){

            BlockState state = e.getNewState();

            Ageable thing = (Ageable) state.getBlockData();

            //We are working with a crop, see if it's at its max age
            if (thing.getAge() == thing.getMaximumAge()){

                //the crop is fully grown, see if there is a collector in the chunk
                int collectorId = Utils.isCollectorInChunk(e.getBlock().getChunk());
                if (collectorId != 0){

                    if (ChunkCollector.getCrops().containsKey(collectorId)) {

                        ArrayList<Block> items = ChunkCollector.getCrops().get(collectorId);
                        items.add(e.getBlock());

                        ChunkCollector.getCrops().replace(collectorId, items);

                    } else {

                        ArrayList<Block> items = new ArrayList<>();
                        items.add(e.getBlock());

                        ChunkCollector.getCrops().put(collectorId, items);

                    }


                }

            }

        }

    }

}
