package me.kodysimpson.chunkcollector.tasks;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.utils.Collector;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CollectDrops extends BukkitRunnable {

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

                    int fortuneLevel = Database.findByID(integer.intValue()).getFortuneLevel();
                    if (fortuneLevel != 0) {
                        fortuneItem.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, fortuneLevel);
                    }

                    Utils.processItems(Database.findByID(integer.intValue()), (ArrayList<ItemStack>) block.getDrops(fortuneItem));

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
                .forEach(s -> {
                    if (Bukkit.getServer().getWorld(s) != null) {

                        final Chunk[] loadedChunks = Bukkit.getServer().getWorld(s).getLoadedChunks();

                        for (Chunk chunk : loadedChunks) {

                            for (BlockState blockState : chunk.getTileEntities()) {
                                TileState tileState = (TileState) blockState;

                                if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {

                                    //get the collector from the found id
                                    Collector collector = Database.findByID(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                                    if ((collector != null) && (collector.getType() == Database.CollectionType.DROP)) {

                                        //Get all ground items that are set to be picked up in the config.yml
                                        ArrayList<ItemStack> groundItems = (ArrayList<ItemStack>) Arrays.stream(chunk.getEntities())
                                                .filter((Entity entity) -> entity.isOnGround())
                                                .filter((Entity entity) -> entity instanceof Item)
                                                .map((Entity entity) -> (Item) entity)
                                                .filter(item -> Utils.isMobDrop(item))
                                                .map(Item::getItemStack)
                                                .collect(Collectors.toList());

                                        //Add picked up items to the collector. Sell if storage capacity reached.
                                        Utils.processItems(Database.findByID(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)), groundItems);

                                        //Remove the entities from the ground
                                        Arrays.stream(chunk.getEntities())
                                                .filter((Entity entity) -> entity.isOnGround())
                                                .filter((Entity entity) -> entity instanceof Item)
                                                .map((Entity entity) -> (Item) entity)
                                                .filter(item -> Utils.isMobDrop(item))
                                                .forEach(item -> item.remove());

                                        //since there can only be one collector per chunk, break out of the loop
                                        break;
                                    }
                                }

                            }
                        }
                    }
                });

    }

}
