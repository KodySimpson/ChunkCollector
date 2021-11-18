package me.kodysimpson.chunkcollector.listeners;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.database.Database;
import me.kodysimpson.chunkcollector.menusystem.menus.CollectorMenu;
import me.kodysimpson.chunkcollector.menusystem.menus.MenuData;
import me.kodysimpson.chunkcollector.model.Collector;
import me.kodysimpson.chunkcollector.utils.CollectionType;
import me.kodysimpson.chunkcollector.utils.Utils;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class CollectorListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

        if (e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {

            //Check the chunk to see if there is already a collector
            if (Utils.isChunkTaken(e.getBlockPlaced().getChunk())) {
                e.getPlayer().sendMessage(ChatColor.RED + "There is a Collector already in this chunk.");
                e.setCancelled(true);
            } else {

                Collector collector = Database.getCollectorDataAccess().findById(e.getItemInHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                TileState thing = (TileState) e.getBlockPlaced().getState();

                thing.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER, e.getItemInHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                thing.update();


                if (ChunkCollector.getPlugin().getConfig().getBoolean("hologram.enabled")) {
                    ArmorStand title = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, -0.5, 0.5), EntityType.ARMOR_STAND);
                    title.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "holo-line"), PersistentDataType.INTEGER, 1);
                    ArmorStand itemsSold = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, -0.75, 0.5), EntityType.ARMOR_STAND);
                    itemsSold.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "holo-line"), PersistentDataType.INTEGER, 2);
                    ArmorStand moneyEarned = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, -1, 0.5), EntityType.ARMOR_STAND);
                    moneyEarned.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "holo-line"), PersistentDataType.INTEGER, 3);

                    title.setVisible(false);
                    title.setCustomNameVisible(true);
                    title.setGravity(false);

                    itemsSold.setVisible(false);
                    itemsSold.setCustomNameVisible(true);
                    itemsSold.setGravity(false);

                    moneyEarned.setVisible(false);
                    moneyEarned.setCustomNameVisible(true);
                    moneyEarned.setGravity(false);

                    if (collector.getType() == CollectionType.DROP) {
                        title.setCustomName(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("hologram.drop")));
                        e.getBlockPlaced().getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, e.getBlockPlaced().getLocation(), 50);
                    } else if (collector.getType() == CollectionType.CROP) {
                        title.setCustomName(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("hologram.crop")));
                        e.getBlockPlaced().getWorld().spawnParticle(Particle.DRAGON_BREATH, e.getBlockPlaced().getLocation(), 50);
                    } else {
                        title.setCustomName(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("hologram.ore")));
                        e.getBlockPlaced().getWorld().spawnParticle(Particle.DRAGON_BREATH, e.getBlockPlaced().getLocation(), 50);
                    }

                    itemsSold.setCustomName(ChatColor.GRAY + "Items Sold: " + ChatColor.GREEN + collector.getSold());
                    moneyEarned.setCustomName(ChatColor.GRAY + "Money Earned: $" + ChatColor.GREEN + String.format("%.2f", collector.getEarned()));
                } else {

                    if (collector.getType() == CollectionType.DROP) {
                        e.getBlockPlaced().getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, e.getBlockPlaced().getLocation(), 50);
                    } else if (collector.getType() == CollectionType.CROP) {
                        e.getBlockPlaced().getWorld().spawnParticle(Particle.DRAGON_BREATH, e.getBlockPlaced().getLocation(), 50);
                    } else {
                        e.getBlockPlaced().getWorld().spawnParticle(Particle.DRAGON_BREATH, e.getBlockPlaced().getLocation(), 50);
                    }

                }

                e.getPlayer().sendMessage(ChatColor.GREEN + "Collector placed in chunk.");

                //Turn the collector into a regular hopper
                if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    e.getPlayer().getInventory().setItemInMainHand(null);
                }

            }

        }

    }

    @EventHandler
    public void onCollectorBreak(BlockBreakEvent e){
        if (e.getBlock().getState() instanceof TileState) {
            TileState tileState = (TileState) e.getBlock().getState();

            if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {

                Collector collector = Database.getCollectorDataAccess().findById(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                if (!collector.getOwnerUUID().equals(e.getPlayer().getUniqueId())) {

                    if (!e.getPlayer().hasPermission("chunkcollector.break") && !e.getPlayer().hasPermission("chunkcollector.admin")) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.GRAY + "You can't break this collector.");
                    }
                } else if (ChunkCollector.getPlugin().getConfig().getBoolean("hologram.enabled")) {
                    Arrays.stream(e.getBlock().getChunk().getEntities())
                            .filter(entity -> entity.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "holo-line"), PersistentDataType.INTEGER)).map(entity -> (ArmorStand) entity)
                            .forEach(armorStand -> armorStand.remove());
                }

            }

        }
    }

    @EventHandler
    public void onCollectorBreakDrop(BlockDropItemEvent e) {

        if (e.getBlockState() instanceof TileState) {
            TileState tileState = (TileState) e.getBlockState();

            if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {

                Collector collector = Database.getCollectorDataAccess().findById(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                    if (e.getItems().size() == 0) { //means they are in creative or something

                        //delete the collector from the DB
                        Database.getCollectorDataAccess().deleteById(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                    } else {
                        e.getItems().stream()
                                .forEach(item -> {
                                    ItemMeta itemMeta = item.getItemStack().getItemMeta();

                                    //get the collector-id of the block broken and put it into the item dropped
                                    itemMeta.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER, tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                                    item.getItemStack().setItemMeta(itemMeta);

                                    //Cancel the event so the item doesnt drop
                                    e.setCancelled(true);

                                    if (e.getPlayer().getInventory().firstEmpty() == -1){
                                        e.getPlayer().sendMessage(ChatColor.RED + "Your inventory is full, the Collector was dropped at your feet.");
                                        e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), item.getItemStack());
                                    }else{
                                        e.getPlayer().sendMessage(ChatColor.GREEN + "Collector broken and placed into inventory.");
                                        e.getPlayer().getInventory().addItem(item.getItemStack());
                                    }
                                });
                    }

            }

        }

    }

    @EventHandler
    public void onMobAutoKill(EntityDeathEvent e) {

        if (ChunkCollector.getPlugin().getConfig().getBoolean("mob-drop-auto-pickup")) {
            Chunk chunk = e.getEntity().getLocation().getChunk();

            for (BlockState blockState : chunk.getTileEntities()) {
                TileState tileState = (TileState) blockState;

                if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {

                    //get the collector from the found id
                    Collector collector = Database.getCollectorDataAccess().findById(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                    if (collector != null) {
                        if (collector.isEnabled()) {
                            if (collector.getType() == CollectionType.DROP) {

                                Utils.processItems(collector, (ArrayList<ItemStack>) e.getDrops());

                                e.getDrops().clear();

                                //since there can only be one collector per chunk, break out of the loop
                                break;
                            }
                        }
                    }
                }

            }
        }

    }

    @EventHandler
    public void onCollectorExplode(EntityExplodeEvent e) {

        for (int i = 0; i < e.blockList().size(); i++) {
            if (e.blockList().get(i).getState() instanceof TileState) {
                TileState tileState = (TileState) e.blockList().get(i).getState();
                if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER) && ChunkCollector.getPlugin().getConfig().getBoolean("bomb-proof")) {
                    e.blockList().remove(i);
                }

            }
        }

    }

    @EventHandler
    public void onCollectorOpen(PlayerInteractEvent e) throws MenuManagerException, MenuManagerNotSetupException {

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getState() instanceof TileState) {

                TileState tileState = (TileState) e.getClickedBlock().getState();

                if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {
                    e.setCancelled(true);

                    Collector collector = Database.getCollectorDataAccess().findById(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                    //see if the person who is trying to open the collector owns it
                    if (collector.getOwnerUUID().equals(e.getPlayer().getUniqueId()) || e.getPlayer().hasPermission("chunkcollector.open") || e.getPlayer().hasPermission("chunkcollector.admin")) {

                        PlayerMenuUtility playerMenuUtility = MenuManager.getPlayerMenuUtility(e.getPlayer());
                        playerMenuUtility.setData(MenuData.COLLECTOR_ID, collector.getId());
                        MenuManager.openMenu(CollectorMenu.class, e.getPlayer());

                    } else {
                        e.getPlayer().sendMessage(ChatColor.GRAY + "You don't have access to this collector.");
                    }

                }

            }
        }


    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent e) {

        if (e.getBlock().getBlockData() instanceof Ageable) {

            BlockState state = e.getNewState();

            Ageable thing = (Ageable) state.getBlockData();

            //We are working with a crop, see if it's at its max age
            if (thing.getAge() == thing.getMaximumAge()) {

                //the crop is fully grown, see if there is a collector in the chunk
                int collectorId = Utils.isCollectorInChunk(e.getBlock().getChunk());
                if (collectorId != 0) {

                    Collector collector = Database.getCollectorDataAccess().findById(collectorId);

                    if (collector.isEnabled()) {
                        //See if the map already has this collector
                        if (ChunkCollector.getCrops().containsKey(collectorId)) {

                            ArrayList<Block> items = ChunkCollector.getCrops().get(collectorId);
                            items.add(e.getBlock());

                            ChunkCollector.getCrops().replace(collectorId, items);

                        } else { //map doesnt have this collector yet

                            ArrayList<Block> items = new ArrayList<>();
                            items.add(e.getBlock());

                            ChunkCollector.getCrops().put(collectorId, items);

                        }
                    }


                }

            }

        }

    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent e) {

        if (e.getInventory().getType().equals(InventoryType.HOPPER) && !ChunkCollector.getPlugin().getConfig().getBoolean("hopper-transfer")) {
            Hopper hopper = (Hopper) e.getInventory().getHolder();

            Block block = hopper.getBlock();

            if (block.getState() instanceof TileState) {

                TileState tileState = (TileState) block.getState();

                if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {
                    e.setCancelled(true);
                }

            }

        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){

        Player p = e.getPlayer();

        new BukkitRunnable(){
            @Override
            public void run() {
                double totalEarned = 0.0;
                long totalSold = 0;

                ResultSet result = Database.getCollectorDataAccess().findAllByUUID(p);
                try{
                    while(result.next()){
                        totalEarned += result.getDouble("TotalEarned");
                        totalSold += result.getLong("TotalSold");
                    }

                    if (totalSold > 0){
                        p.sendMessage(" ");
                        p.sendMessage(ChatColor.LIGHT_PURPLE + "Your Collectors earned you money while you were gone.");
                        p.sendMessage(ChatColor.GRAY + "Total Earned: " + ChatColor.GREEN + "$" + totalEarned);
                        p.sendMessage(ChatColor.GRAY + "Total Items Sold: " + ChatColor.AQUA + totalSold);
                        p.sendMessage(" ");

                        Database.getCollectorDataAccess().deleteAllByUUID(p);
                    }

                    result.close();

                }catch (SQLException ex){
                    System.out.println(ex);
                }
            }
        }.runTaskLater(ChunkCollector.getPlugin(), 90L);

    }

}
