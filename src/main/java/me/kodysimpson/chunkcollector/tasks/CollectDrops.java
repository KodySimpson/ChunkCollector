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

        HashMap<Integer, ArrayList<Block>> items = ChunkCollector.getCrops();

        items.entrySet().stream()
                .forEach(integerArrayListEntry -> {
                    integerArrayListEntry.getValue().stream()
                            .filter(block -> {

                                //see if the block at that location is still a crop
                                if (block.getWorld().getBlockAt(block.getLocation()).getState().getBlockData() instanceof Ageable){
                                    Ageable ageable = (Ageable) block.getWorld().getBlockAt(block.getLocation()).getState().getBlockData();

                                    //see if the crop is still max age
                                    return ageable.getAge() == ageable.getMaximumAge();

                                }
                                return false;
                            })
                            .forEach(block -> {

                                //try resetting the crop
                                BlockState blockState = block.getState();
                                Ageable ageable = (Ageable) blockState.getBlockData();

                                ItemStack fortuneItem = new ItemStack(Material.WOODEN_SHOVEL, 1);

                                int fortuneLevel = Database.findByID(integerArrayListEntry.getKey().intValue()).getFortuneLevel();
                                if (fortuneLevel != 0) {
                                    fortuneItem.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, fortuneLevel);
                                }

                                Utils.addCropProduce(Database.findByID(integerArrayListEntry.getKey().intValue()), (ArrayList<ItemStack>) block.getDrops(fortuneItem));

                                ageable.setAge(0);
                                blockState.setBlockData(ageable);
                                blockState.update();

                            });
                });

        ChunkCollector.getCrops().clear();


        Chunk[] loadedChunks = Bukkit.getServer().getWorld("world").getLoadedChunks();

        for(Chunk chunk : loadedChunks){
            for(BlockState blockState : chunk.getTileEntities()){
                TileState tileState = (TileState) blockState;

                if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)){

                    //determine what type of collector it is
                    Collector collector = Database.findByID(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                    if ((collector != null) && (collector.getType() == Database.CollectionType.DROP)) {

                        System.out.println("Collector Located: #" + tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                        ArrayList<ItemStack> groundItems = (ArrayList<ItemStack>) Arrays.stream(chunk.getEntities())
                                .filter((Entity entity) -> {
                                    return entity.isOnGround();
                                })
                                .filter((Entity entity) -> {
                                    return entity instanceof Item;
                                })
                                .map((Entity entity) -> (Item) entity)
                                .filter(item -> Utils.isMobDrop(item))
                                .map(Item::getItemStack)
                                .collect(Collectors.toList());

                        System.out.println("Sending groundItems to collector in database");
                        Utils.addGroundItems(Database.findByID(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)), groundItems);

                        //Remove the entities from the ground
                        Arrays.stream(chunk.getEntities())
                                .filter((Entity entity) -> {
                                    return entity.isOnGround();
                                })
                                .filter((Entity entity) -> {
                                    return entity instanceof Item;
                                })
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

}
