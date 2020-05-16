package me.kodysimpson.chunkcollector.tasks;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CollectDrops extends BukkitRunnable {

    @Override
    public void run() {

        Chunk[] loadedChunks = Bukkit.getServer().getWorld("world").getLoadedChunks();

        for(Chunk chunk : loadedChunks){
            for(BlockState blockState : chunk.getTileEntities()){
                TileState tileState = (TileState) blockState;

                if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)){

                    System.out.println("Collector Located: #" + tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                    ArrayList<ItemStack> groundItems = (ArrayList<ItemStack>) Arrays.stream(chunk.getEntities())
                            .filter((Entity entity) -> {
                                if (entity.isOnGround()){
                                    return true;
                                }
                                return false;
                            })
                            .filter((Entity entity) -> {
                                if (entity instanceof Item){
                                    return true;
                                }
                                return false;
                            })
                            .map((Entity entity) -> (Item) entity)
                            .filter(item -> Utils.isMobDrop(item))
                            .map(Item::getItemStack)
                            .collect(Collectors.toList());

                    System.out.println("Sending groundItems to collector in database");
                    Utils.addGroundItems(Database.findByID(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)), groundItems);


                    groundItems.stream()
                            .forEach(itemStack -> {
                                System.out.println(itemStack.getType().toString() + " : " + itemStack.getAmount());
                            });


                    //Remove the entities from the ground
                    Arrays.stream(chunk.getEntities())
                            .filter((Entity entity) -> {
                                if (entity.isOnGround()){
                                    return true;
                                }
                                return false;
                            })
                            .filter((Entity entity) -> {
                                if (entity instanceof Item){
                                    return true;
                                }
                                return false;
                            })
                            .map((Entity entity) -> (Item) entity)
                            .filter(item -> Utils.isMobDrop(item))
                            .forEach(item -> item.remove());

                }

            }
        }




    }

}
