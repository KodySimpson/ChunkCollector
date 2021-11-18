package me.kodysimpson.chunkcollector.tasks;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.utils.CollectionType;
import me.kodysimpson.chunkcollector.model.Collector;
import me.kodysimpson.chunkcollector.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class AutoKill extends BukkitRunnable {

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
                                if (collector.getType() == CollectionType.DROP) {

                                    //Get all ground items that are set to be picked up in the config.yml
                                    Arrays.stream(chunk.getEntities())
                                            .filter((Entity entity) -> entity.isOnGround())
                                            .filter((Entity entity) -> entity instanceof Mob)
                                            .map((Entity entity) -> (Mob) entity)
                                            .forEach(mob -> mob.damage(1000));

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
                .forEach(AutoKill::processChunks);

    }

}
