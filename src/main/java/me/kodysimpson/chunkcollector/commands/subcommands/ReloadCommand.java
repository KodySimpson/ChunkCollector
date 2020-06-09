package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.commands.SubCommand;
import me.kodysimpson.chunkcollector.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {

    @Override
    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Reload.name"));
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Reload.description"));
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Reload.syntax"));
    }

    @Override
    public void perform(Player player, String[] args) {

        if (player.hasPermission("chunkcollector.admin") || player.hasPermission("chunkcollector.reload")){

            ChunkCollector.getPlugin().reloadConfig();

            player.sendMessage(Config.RELOADED);

        }else{
            player.sendMessage(Config.NO_PERMISSION);
        }

    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return null;
    }
}
