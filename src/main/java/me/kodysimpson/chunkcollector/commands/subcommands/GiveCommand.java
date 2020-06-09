package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.commands.SubCommand;
import me.kodysimpson.chunkcollector.config.Config;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveCommand extends SubCommand {

    @Override
    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Give.name"));
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Give.description"));
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Give.syntax"));
    }

    @Override
    public void perform(Player p, String[] args) {

        if (p.hasPermission("chunkcollector.admin") || p.hasPermission("chunkcollector.give")){
            if (args.length == 2){

                if (args[1].equalsIgnoreCase("drop")){

                    ItemStack collector = Utils.makeCollector(p, Database.CollectionType.DROP);
                    if (collector != null){
                        p.getInventory().addItem(collector);
                        p.sendMessage(Config.GOT_COLLECTOR_DROP);
                    }

                }else if(args[1].equalsIgnoreCase("crop")){

                    ItemStack collector = Utils.makeCollector(p, Database.CollectionType.CROP);
                    if (collector != null){
                        p.getInventory().addItem(collector);
                        p.sendMessage(Config.GOT_COLLECTOR_CROP);
                    }

                }else{

                    p.sendMessage(ChatColor.RED + "The collector type provided is invalid. Try Again.");
                    p.sendMessage(ChatColor.GRAY + "ex: /collector give drop [username] | /collector give crop [username]");

                }

            }else if(args.length == 3){

                //see if the third argument is a valid online player
                Player target = Bukkit.getPlayer(args[2]);
                if(target == null){
                    p.sendMessage(ChatColor.RED + "That is not a valid online player. Try again");
                    p.sendMessage(ChatColor.GRAY + "ex: /collector give crop Illuminatiiiiii");
                }else{

                    if (args[1].equalsIgnoreCase("drop")){

                        ItemStack collector = Utils.makeCollector(p, Database.CollectionType.DROP);
                        if (collector != null){
                            target.getInventory().addItem(collector);
                            target.sendMessage(Config.GIVEN_COLLECTOR_DROP);

                            p.sendMessage(ChatColor.GREEN + "Collector given to " + target.getDisplayName());
                        }

                    }else if(args[1].equalsIgnoreCase("crop")){

                        ItemStack collector = Utils.makeCollector(p, Database.CollectionType.CROP);
                        if (collector != null){
                            target.getInventory().addItem(collector);
                            target.sendMessage(Config.GIVEN_COLLECTOR_CROP);

                            p.sendMessage(ChatColor.GREEN + "Collector given to " + target.getDisplayName());
                        }

                    }else{

                        p.sendMessage(ChatColor.RED + "The collector type provided is invalid. Try Again.");
                        p.sendMessage(ChatColor.GRAY + "ex: /collector give drop [username] | /collector give crop [username]");

                    }

                }

            } else if(args.length == 1){
                p.sendMessage(ChatColor.GRAY + "Specify a collector type. Collector Types: " + ChatColor.GREEN + "drop" + ChatColor.GRAY + "| " + ChatColor.GREEN + "crop");
                p.sendMessage(ChatColor.GRAY + "Example: /collector give drop");
            }
        }else{
            p.sendMessage(Config.NO_PERMISSION);
        }


    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {

        if (args.length == 2){
            String collectorTypes[] = {"drop", "crop"};
            return Arrays.asList(collectorTypes);
        }else if(args.length == 3){

            List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (int i = 0; i < players.length; i++){
                playerNames.add(players[i].getName());
            }

            return playerNames;
        }

        return null;
    }
}
