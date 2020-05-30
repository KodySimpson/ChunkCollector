package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the configuration file";
    }

    @Override
    public String getSyntax() {
        return "/collector reload";
    }

    @Override
    public void perform(Player player, String[] args) {

        if (player.hasPermission("chunkcollector.admin") || player.hasPermission("chunkcollector.reload")){
            ChunkCollector.getPlugin().reloadConfig();

            player.sendMessage(ChatColor.GREEN + "The config.yml has been reloaded.");
        }else{
            player.sendMessage(ChatColor.GREEN + "You don't have permission to run this command.");
        }



    }
}
