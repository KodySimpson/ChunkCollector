package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.commands.SubCommand;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        return "/collector give <type> [player]";
    }

    @Override
    public void perform(Player p, String[] args) {

        if (p.hasPermission("chunkcollector.admin") || p.hasPermission("chunkcollector.give")){
            if (args.length == 2){

                if (args[1].equalsIgnoreCase("drop")){

                    ItemStack collector = Utils.makeCollector(p, Database.CollectionType.DROP);
                    if (collector != null){
                        p.getInventory().addItem(collector);
                        p.sendMessage(ChatColor.GREEN + "Drop Collector obtained.");
                    }

                }else if(args[1].equalsIgnoreCase("crop")){

                    ItemStack collector = Utils.makeCollector(p, Database.CollectionType.CROP);
                    if (collector != null){
                        p.getInventory().addItem(collector);
                        p.sendMessage(ChatColor.GREEN + "Crop Collector obtained.");
                    }

                }

            }else if(args.length == 1){
                p.sendMessage("Specify a collector type. Collector Types: drop | crop");
                p.sendMessage("Example: /collector give drop");
            }
        }else{
            p.sendMessage(ChatColor.GREEN + "You don't have permission to run this command.");
        }


    }
}
