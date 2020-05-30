package me.kodysimpson.chunkcollector.utils;

import me.kodysimpson.chunkcollector.ChunkCollector;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {

    public static ItemStack makeCollector(Player p, Database.CollectionType type){

        int id = 0;

        ItemStack collector = new ItemStack(Material.HOPPER, 1);
        ItemMeta collectorMeta = collector.getItemMeta();

        if (type == Database.CollectionType.DROP){
            collectorMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mob Collector");

            //Create collector and give it to the player if created succesfully
            id = Database.createCollector(p.getUniqueId(), Database.CollectionType.DROP);
        }else{
            collectorMeta.setDisplayName(ChatColor.YELLOW + "Crop Collector");

            //Create collector and give it to the player if created succesfully
            id = Database.createCollector(p.getUniqueId(), Database.CollectionType.CROP);
        }

        //Collector ID will be zero if was unable to create in DB
        if (id != 0){
            collectorMeta.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER, id);

            collector.setItemMeta(collectorMeta);

            return collector;
        }else{
            p.sendMessage("Error creating collector.");
            return null;
        }
    }

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

        return mob_drops.contains(item.getItemStack().getType());
    }

    public static ArrayList<ItemStack> combine( ArrayList< ItemStack > items ) {

        Inventory test = Bukkit.createInventory(null, 54, "thing");
        //Put all the items into a new inventory, which automatically compresseses the items
        items.stream()
                .forEach(itemStack -> {
                    if (itemStack != null){
                        test.addItem(itemStack);
                    }
                });

        //Take the items from the compressed inventory and put into arraylist
        ArrayList<ItemStack> compressed = new ArrayList<>();
        Arrays.stream(test.getContents())
                .forEach(itemStack -> {
                    if (itemStack != null){
                        compressed.add(itemStack);
                    }
                });

        return compressed;

    }

    public static double getDropPricing(Material item){

        ArrayList<Material> mob_drops = (ArrayList<Material>) ChunkCollector.getPlugin().getConfig().getConfigurationSection("mob-drops").getKeys(false)
                .stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());
        
        if (mob_drops.contains(item)){
            return ChunkCollector.getPlugin().getConfig().getDouble("mob-drops." + item.toString());
        }

        return 0.0;
    }

    public static double getCropPricing(Material item){

        ArrayList<Material> mob_drops = (ArrayList<Material>) ChunkCollector.getPlugin().getConfig().getConfigurationSection("crop-pricing").getKeys(false)
                .stream()
                .map(Material::valueOf)
                .collect(Collectors.toList());

        //if there is a price for the crop, then return it. otherwise, return 0 as the price
        if (mob_drops.contains(item)){
            return ChunkCollector.getPlugin().getConfig().getDouble("crop-pricing." + item.toString());
        }

        return 0.0;
    }

    public static void addGroundItems(Collector collector, ArrayList<Item> groundItems){

        groundItems.stream()
                .map(Item::getItemStack)
                .forEach(itemStack -> {

                    //if the collector capacity is reached, sell all
                    if ((collector.getItems().stream().mapToInt(ItemStack::getAmount).sum() + itemStack.getAmount()) > Utils.getCapacityAmount(collector.getStorageCapacity())){
                        collector.getItems().add(itemStack);

                        collector.getItems().stream()
                                .forEach(item -> {

                                    OfflinePlayer owner = Bukkit.getOfflinePlayer(collector.getOwnerUUID());

                                    ChunkCollector.getEconomy().depositPlayer(owner, (getDropPricing(item.getType()) * item.getAmount()));

                                    collector.setSold(collector.getSold() + item.getAmount());
                                    collector.setEarned(collector.getEarned() + (getDropPricing(item.getType()) * item.getAmount()));

                                });

                        collector.getItems().clear();
                    }else{
                        collector.getItems().add(itemStack);
                    }

                });

        Database.updateCollector(collector);
    }

    public static void addCropProduce(Collector collector, ArrayList<ItemStack> produce){

        produce.stream()
                .forEach(itemStack -> {

                    //if the collector capacity is reached, sell all
                    if ((collector.getItems().stream().mapToInt(ItemStack::getAmount).sum() + itemStack.getAmount()) > Utils.getCapacityAmount(collector.getStorageCapacity())){
                        collector.getItems().add(itemStack);

                        collector.getItems().stream()
                                .forEach(item -> {

                                    OfflinePlayer owner = Bukkit.getOfflinePlayer(collector.getOwnerUUID());

                                    ChunkCollector.getEconomy().depositPlayer(owner, (getCropPricing(item.getType()) * item.getAmount()));

                                    collector.setSold(collector.getSold() + item.getAmount());
                                    collector.setEarned(collector.getEarned() + (getCropPricing(item.getType()) * item.getAmount()));

                                });

                        collector.getItems().clear();
                    }else{
                        collector.getItems().add(itemStack);
                    }

                });

        Database.updateCollector(collector);
    }

    public static int getCapacityAmount(int currentLevel){

        return ChunkCollector.getPlugin().getConfig().getInt("storage-upgrades." + currentLevel + ".items");

    }

    public static String getNextCapacity(int currentLevel){

        if (ChunkCollector.getPlugin().getConfig().contains("storage-upgrades." + (currentLevel + 1) + ".items")){
            return String.valueOf(ChunkCollector.getPlugin().getConfig().getInt("storage-upgrades." + (currentLevel + 1) + ".items"));
        }else{
            return "AT MAX";
        }

    }

    public static double getCapacityUpgradePrice(int currentLevel){

        if (ChunkCollector.getPlugin().getConfig().contains("storage-upgrades." + (currentLevel + 1) + ".price")){
            return ChunkCollector.getPlugin().getConfig().getDouble("storage-upgrades." + (currentLevel + 1) + ".price");
        }else{
            return 0.0;
        }

    }

    public static int isCollectorInChunk(Chunk chunk) {

        for (BlockState blockState : chunk.getTileEntities()) {
            TileState tileState = (TileState) blockState;
            if (tileState.getPersistentDataContainer().has(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER)) {
                return tileState.getPersistentDataContainer().get(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER);
            }
        }
        return 0;
    }

    public static double getFortuneUpgradePrice(int currentLevel) {

        if (ChunkCollector.getPlugin().getConfig().contains("fortune-prices." + (currentLevel + 1))) {
            return ChunkCollector.getPlugin().getConfig().getDouble("fortune-prices." + (currentLevel + 1));
        }
        return 0.0;
    }

}
