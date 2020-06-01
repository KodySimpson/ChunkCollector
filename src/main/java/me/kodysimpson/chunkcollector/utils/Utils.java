package me.kodysimpson.chunkcollector.utils;

import me.kodysimpson.chunkcollector.ChunkCollector;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.milkbowl.vault.economy.EconomyResponse;
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
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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

    public static double getItemPrice(Material item, Database.CollectionType type){

        switch (type){
            case DROP:
                return ChunkCollector.getPlugin().getConfig().getDouble("mob-drops." + item.toString());
            case CROP:
                return ChunkCollector.getPlugin().getConfig().getDouble("crop-pricing." + item.toString());
        }

        return 0.0;
    }

    /**
     * Used to sell all items in a given collector.
     * @param collectorID collector to be accessed and emptied
     */
    public static void sellAllItems(int collectorID){

        Collector collector = Database.findByID(collectorID);

        System.out.println("collector id: " + collectorID);
        System.out.println("wtftotal items about to be sold: " + collector.getItems().stream().mapToInt(ItemStack::getAmount).sum());

        Player p = Bukkit.getPlayer(collector.getOwnerUUID());

        ComponentBuilder receipt = new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.BOLD + "Receipt of Items Sold");
        TextComponent newLine = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));
        receipt.append(newLine).reset();

        ArrayList<ItemStack> storage = Utils.combine(collector.getItems());

        if (collector.getItems().isEmpty()) {
            p.sendMessage(ChatColor.GRAY + "The collector is empty.");
        } else {

            long itemsSold = 0;
            double earned = 0.0;

            for (ItemStack itemStack : storage) {
                itemsSold = itemsSold + itemStack.getAmount();
                earned = earned + (Utils.getItemPrice(itemStack.getType(), collector.getType()) * itemStack.getAmount());
            }

            //Count each material type sold for the receipt
            HashMap<Material, Long> countedItems = new HashMap<>();
            storage.stream()
                    .forEach(item -> {
                        if (!countedItems.containsKey(item.getType())){
                            countedItems.put(item.getType(), (long) item.getAmount());
                        }else{
                            countedItems.replace(item.getType(), countedItems.get(item.getType()) + item.getAmount());
                        }
                    });
            //Add the counted materials to the receipt
            countedItems.forEach(((material, aLong) -> {
                receipt.append(new TextComponent(net.md_5.bungee.api.ChatColor.GRAY + "Sold " + aLong.toString() + " " + material.toString().toLowerCase().replace("_", " ") + " for $" + net.md_5.bungee.api.ChatColor.YELLOW + String.format("%.2f", (Utils.getItemPrice(material, collector.getType()) * aLong)))).reset();
                receipt.append(newLine);
            }));

            receipt.append(net.md_5.bungee.api.ChatColor.GREEN + "---------------------------");

            TextComponent text = new TextComponent(net.md_5.bungee.api.ChatColor.BLUE + "" + net.md_5.bungee.api.ChatColor.BOLD + "Hover for Receipt");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, receipt.create()));

            EconomyResponse transaction = ChunkCollector.getEconomy().depositPlayer(p, earned);

            if (transaction.transactionSuccess()) {
                collector.setSold(itemsSold);
                collector.setEarned(earned);

                p.sendMessage(" ");
                if (collector.getType() == Database.CollectionType.DROP){
                    p.sendMessage(ChatColor.GREEN + "All items in your " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Drop Collector" + ChatColor.GREEN + " have been sold.");
                }else{
                    p.sendMessage(ChatColor.GREEN + "All items in your " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Crop Collector" + ChatColor.GREEN + " have been sold.");
                }
                p.sendMessage(ChatColor.GRAY + "Total Earned: " + ChatColor.GREEN + "$" + String.format("%.2f", earned));
                p.sendMessage(ChatColor.GRAY + "Total Sold: " + ChatColor.GREEN + itemsSold);
                p.sendMessage(" ");

                p.spigot().sendMessage(text);

                //Update the collector to reflect the earnings
                collector.getItems().clear();
                Database.updateCollector(collector);
            }

        }

    }

    /**
     * Takes a collector and a list of items to put into that collector.
     * If the collector is full while trying to add item, sell all items before
     * adding more.
     * @param collector Collector that the items should be added to
     * @param items The items that will be stored into the collector
     */
    public static void processItems(Collector collector, ArrayList<ItemStack> items){

        items.stream()
                .forEach(itemStack -> {

                    //if the collector capacity is reached, sell all
                    if ((collector.getItems().stream().mapToInt(ItemStack::getAmount).sum() + itemStack.getAmount()) > Utils.getCapacityAmount(collector.getStorageCapacity())){
                        collector.getItems().add(itemStack);

                        sellAllItems(collector.getId());

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
