package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.commands.SubCommand;
import me.kodysimpson.chunkcollector.utils.Database;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class GiveCommand extends SubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give yourself or another player a chunk collector.";
    }

    @Override
    public String getSyntax() {
        return "/collector give [type] [player]";
    }

    @Override
    public void perform(Player p, String[] args) {

        if (args.length == 2){

            if (args[1].equalsIgnoreCase("drop")){

                ItemStack collector = new ItemStack(Material.HOPPER, 1);
                ItemMeta collectorMeta = collector.getItemMeta();
                collectorMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mob Collector");

                //Create collector and give it to the player if created succesfully
                int id = Database.createCollector(p.getUniqueId(), Database.CollectionType.DROP);

                if (id != 0){
                    collectorMeta.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER, id);

                    collector.setItemMeta(collectorMeta);

                    p.getInventory().addItem(collector);
                }else{
                    p.sendMessage("Error creating drop collector.");
                }

            }else if(args[1].equalsIgnoreCase("crop")){

                ItemStack collector = new ItemStack(Material.HOPPER, 1);
                ItemMeta collectorMeta = collector.getItemMeta();
                collectorMeta.setDisplayName(ChatColor.YELLOW + "Crop Collector");

                //Create collector and give it to the player if created succesfully
                int id = Database.createCollector(p.getUniqueId(), Database.CollectionType.CROP);

                if (id != 0){
                    collectorMeta.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "collector-id"), PersistentDataType.INTEGER, id);

                    collector.setItemMeta(collectorMeta);

                    p.getInventory().addItem(collector);
                }else{
                    p.sendMessage("Error creating crop collector.");
                }

            }

        }else if(args.length == 1){
            p.sendMessage("Specify a collector type. Collector Types: drop | food");
            p.sendMessage("Example: /collector give drop");
        }


    }
}
