package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.commands.SubCommand;
import me.kodysimpson.chunkcollector.config.Config;
import me.kodysimpson.chunkcollector.menusystem.menus.BuyMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class BuyCommand extends SubCommand {

    @Override
    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Buy.name"));
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Buy.description"));
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Buy.syntax"));
    }

    @Override
    public void perform(Player player, String[] args) {

        if (player.hasPermission("collector.buy")){
            new BuyMenu(ChunkCollector.getPlayerMenuUtility(player)).open();
        }else{
            player.sendMessage(Config.NO_PERMISSION);
        }

    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return null;
    }

}
