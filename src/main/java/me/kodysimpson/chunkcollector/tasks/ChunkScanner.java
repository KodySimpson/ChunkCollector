package me.kodysimpson.chunkcollector.tasks;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.database.Database;
import me.kodysimpson.chunkcollector.model.Collector;
import me.kodysimpson.chunkcollector.utils.*;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ChunkScanner extends BukkitRunnable {


    /**
     * This method will scan the loaded chunks of each world, looking for collectors.
     * If collectors are found, it will filter the collectible ground items
     * and put them into the collector for that chunk.
     *
     * @param worldName The name of the world to scan the chunks of
     */
    private static void processChunks(String worldName) {
        if (Bukkit.getServer().getWorld(worldName) != null) {

            final Chunk[] loadedChunks = Bukkit.getServer().getWorld(worldName).getLoadedChunks();

            for (Chunk chunk : loadedChunks) {

                Collector collector = null;
                boolean foundCollector = false;

                for (BlockState blockState : chunk.getTileEntities()) {
                    TileState tileState = (TileState) blockState;

                    if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {

                        //get the collector from the found id

                        collector = Database.getCollectorDataAccess().findById(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                        if (collector != null) {
                            foundCollector = true;
                            if (collector.isEnabled()) {
                                if (collector.getType() == CollectionType.DROP) {

                                    //Get all ground items that are set to be picked up in the config.yml
                                    ArrayList<ItemStack> groundItems = (ArrayList<ItemStack>) Arrays.stream(chunk.getEntities())
                                            .filter((Entity entity) -> entity.isOnGround())
                                            .filter((Entity entity) -> entity instanceof Item)
                                            .map((Entity entity) -> (Item) entity)
                                            .filter(item -> Utils.isMobDrop(item))
                                            .map(Item::getItemStack)
                                            .collect(Collectors.toList());

                                    //Add picked up items to the collector. Sell if storage capacity reached.
                                    Utils.processItems(collector, groundItems);

                                    //Remove the entities from the ground
                                    Arrays.stream(chunk.getEntities())
                                            .filter((Entity entity) -> entity.isOnGround())
                                            .filter((Entity entity) -> entity instanceof Item)
                                            .map((Entity entity) -> (Item) entity)
                                            .filter(item -> Utils.isMobDrop(item))
                                            .forEach(item -> item.remove());

                                    //since there can only be one collector per chunk, break out of the loop
                                    break;
                                } else if (collector.getType() == CollectionType.CROP) {

                                    //Get all ground items that are set to be picked up in the config.yml
                                    ArrayList<ItemStack> cropGroundItems = (ArrayList<ItemStack>) Arrays.stream(chunk.getEntities())
                                            .filter((Entity entity) -> entity.isOnGround())
                                            .filter((Entity entity) -> entity instanceof Item)
                                            .map((Entity entity) -> (Item) entity)
                                            .map(Item::getItemStack)
                                            .filter(item -> item.getType() == Material.CACTUS || item.getType() == Material.SUGAR_CANE)
                                            .collect(Collectors.toList());

                                    //Add picked up items to the collector. Sell if storage capacity reached.
                                    Utils.processItems(collector, cropGroundItems);

                                    //Remove the entities from the ground
                                    Arrays.stream(chunk.getEntities())
                                            .filter((Entity entity) -> entity.isOnGround())
                                            .filter((Entity entity) -> entity instanceof Item)
                                            .map((Entity entity) -> (Item) entity)
                                            .filter(item -> item.getItemStack().getType() == Material.CACTUS || item.getItemStack().getType() == Material.SUGAR_CANE)
                                            .forEach(item -> item.remove());

                                    //since there can only be one collector per chunk, break out of the loop
                                    break;

                                }
                            }
                        }
                    }

                }


                //UPDATE THE HOLOGRAMS FOR THE COLLECTORS
                if (foundCollector) {

                    Collector finalCollector = collector;
                    Arrays.stream(chunk.getEntities())
                            .filter(entity -> entity.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "holo-line"), PersistentDataType.INTEGER)).map(entity -> (ArmorStand) entity)
                            .forEach(armorStand -> {

                                if (ChunkCollector.getPlugin().getConfig().getBoolean("hologram.enabled")) {
                                    switch (armorStand.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "holo-line"), PersistentDataType.INTEGER)) {
                                        case 1:
                                            if (finalCollector.getType() == CollectionType.DROP) {
                                                armorStand.setCustomName(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("hologram.drop")));
                                            } else if(finalCollector.getType() == CollectionType.CROP) {
                                                armorStand.setCustomName(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("hologram.crop")));
                                            }else{
                                                armorStand.setCustomName(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("hologram.ore")));
                                            }
                                            break;
                                        case 2:
                                            armorStand.setCustomName(ChatColor.GRAY + "Items Sold: " + ChatColor.GREEN + finalCollector.getSold());
                                            break;
                                        case 3:
                                            armorStand.setCustomName(ChatColor.GRAY + "Money Earned: " + ChatColor.GREEN + "$" + String.format("%.2f", finalCollector.getEarned()));
                                            break;
                                    }
                                } else {
                                    armorStand.remove();
                                }

                            });

                }


            }
        }
    }

    /**
     * The method  processes the fully grown crops given by the CollectorListener
     * to harvest them and put the gains into the collector.
     * <p>
     * It will also process the ground items in each chunks
     */
    @Override
    public void run() {

        //get the fully grown crops from the hashmap
        final HashMap<Integer, ArrayList<Block>> items = ChunkCollector.getCrops();

        items.forEach((integer, blocks) -> blocks.forEach(block -> {

            //see if the block at that location is still a crop
            if (block.getWorld().getBlockAt(block.getLocation()).getState().getBlockData() instanceof Ageable) {
                Ageable ageableBlock = (Ageable) block.getWorld().getBlockAt(block.getLocation()).getState().getBlockData();

                //see if the crop is still max age
                if (ageableBlock.getAge() == ageableBlock.getMaximumAge()) {
                    BlockState blockState = block.getState();
                    Ageable ageable = (Ageable) blockState.getBlockData();

                    ItemStack fortuneItem = new ItemStack(Material.WOODEN_SHOVEL, 1);

                    int fortuneLevel = Database.getCollectorDataAccess().findById(integer.intValue()).getFortuneLevel();
                    if (fortuneLevel != 0) {
                        fortuneItem.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, fortuneLevel);
                    }

                    Utils.processItems(Database.getCollectorDataAccess().findById(integer.intValue()), (ArrayList<ItemStack>) block.getDrops(fortuneItem));

                    //Reset the crop to a baby
                    ageable.setAge(0);
                    blockState.setBlockData(ageable);
                    blockState.update();
                }
            }
        }));
        ChunkCollector.getCrops().clear();

        //Collect the drops for the worlds enabled in the config.yml
        ChunkCollector.getPlugin().getConfig().getStringList("worlds").stream()
                .forEach(ChunkScanner::processChunks);

    }

}
