package me.kodysimpson.chunkcollector.utils;

import me.kodysimpson.chunkcollector.ChunkCollector;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {

    //See if the chunk provided already contains a chunk collector
    public static boolean isChunkTaken(Chunk chunk){
        for(BlockState blockState : chunk.getTileEntities()){
            TileState tileState = (TileState) blockState;

            if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)){
                return true;
            }

        }

        return false;
    }

    public static boolean isMobDrop(Item item){

        ArrayList<Material> mob_drops = (ArrayList<Material>) ChunkCollector.getPlugin().getConfig().getConfigurationSection("mob-drops").getKeys(false)
                .stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
        
        if (mob_drops.contains(item.getItemStack().getType())){
            return true;
        }
        return false;
    }

    public static ArrayList<ItemStack> combine( ArrayList< ItemStack > items ) {

        Inventory test = Bukkit.createInventory(null, 54, "thing");
        items.stream()
                .forEach(itemStack -> {
                    if (itemStack != null){
                        test.addItem(itemStack);
                    }
                });

        ArrayList<ItemStack> compressed =  new ArrayList<>();
        Arrays.stream(test.getContents())
                .forEach(itemStack -> compressed.add(itemStack));

        return compressed;

    }

    public static double getPricing(Material item){

        ArrayList<Material> mob_drops = (ArrayList<Material>) ChunkCollector.getPlugin().getConfig().getConfigurationSection("mob-drops").getKeys(false)
                .stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
        
        if (mob_drops.contains(item)){
            return ChunkCollector.getPlugin().getConfig().getDouble("mob-drops." + item.toString());
        }

        return 0.0;
    }



    public static void addGroundItems(Collector collector, ArrayList<ItemStack> groundItems){

        groundItems.stream()
                .forEach(itemStack -> {

                    //if the collector capacity is reached, sell all
                    if ((collector.getItems().stream().mapToInt(ItemStack::getAmount).sum() + itemStack.getAmount()) > Utils.getCapacityAmount(collector.getStorageCapacity())){
                        collector.getItems().add(itemStack);

                        collector.getItems().stream()
                                .forEach(item -> {

                                    OfflinePlayer owner = Bukkit.getOfflinePlayer(collector.getOwnerUUID());

                                    ChunkCollector.getEconomy().depositPlayer(owner, (getPricing(item.getType()) * item.getAmount()));

                                    collector.setSold(collector.getSold() + item.getAmount());
                                    collector.setEarned(collector.getEarned() + (getPricing(item.getType()) * item.getAmount()));

                                    if (owner.isOnline()){
                                        owner.getPlayer().sendMessage("Sold " + item.getAmount() + " " + item.getType().toString() + " for $" + (getPricing(item.getType()) * item.getAmount()));
                                    }


                                });

                        collector.getItems().clear();
                    }else{
                        collector.getItems().add(itemStack);
                    }

                });

        Database.updateCollector(collector);
    }

    public static int getCapacityAmount(int capacityLevel){

        return ChunkCollector.getPlugin().getConfig().getInt("capacity." + capacityLevel);

    }

    public static String getNextCapacity(int capacityLevel){

        if (ChunkCollector.getPlugin().getConfig().contains("capacity." + (capacityLevel + 1))){
            return String.valueOf(ChunkCollector.getPlugin().getConfig().getInt("capacity." + (capacityLevel + 1)));
        }else{
            return "AT MAX";
        }

    }

    public static double getCapacityUpgradePrice(int upgradeLevel){

        if (ChunkCollector.getPlugin().getConfig().contains("capacity-prices." + upgradeLevel)){
            return ChunkCollector.getPlugin().getConfig().getDouble("capacity-prices." + upgradeLevel);
        }else{
            return 0.0;
        }

    }

}
