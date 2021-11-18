package me.kodysimpson.chunkcollector.tasks;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.utils.CollectionType;
import me.kodysimpson.chunkcollector.model.Collector;
import me.kodysimpson.chunkcollector.database.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class MineTask extends BukkitRunnable {

    private static void processChunks(String worldName) {
        if (Bukkit.getServer().getWorld(worldName) != null) {

            final Chunk[] loadedChunks = Bukkit.getServer().getWorld(worldName).getLoadedChunks();

            for (Chunk chunk : loadedChunks) {
                for (BlockState blockState : chunk.getTileEntities()) {
                    TileState tileState = (TileState) blockState;

                    if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {

                        //get the collector from the found id
                        Collector collector = Database.getCollectorDataAccess().findById(tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER));

                        if (collector != null) {
                            if (collector.isEnabled()){
                                if (collector.getType() == CollectionType.ORE) {

                                    long delay = 0;

                                    for (int y = 0; y < 128; y++){

                                        for (int i = 0; i < 16; i++){

                                            for (int j = 0; j < 16; j++){

                                                int finalI = i;
                                                int finalY = y;
                                                int finalJ = j;

                                                Block block = chunk.getBlock(finalI, finalY, finalJ);

                                                if (Utils.isMineableBlock(block.getType())){

                                                    delay = delay + 5;

                                                    new BukkitRunnable(){
                                                        @Override
                                                        public void run() {

                                                            Utils.processItems(collector, (ArrayList<ItemStack>) block.getDrops());
                                                            block.setType(Material.AIR);

                                                        }
                                                    }.runTaskLater(ChunkCollector.getPlugin(), delay);


                                                }

                                            }

                                        }

                                    }

                                    //System.out.println(chunk.getX() + " : " + chunk.getZ());

                                    //since there can only be one collector per chunk, break out of the loop
                                    break;
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public void run() {

        ChunkCollector.getPlugin().getConfig().getStringList("worlds").stream()
                .forEach(MineTask::processChunks);

    }

}

