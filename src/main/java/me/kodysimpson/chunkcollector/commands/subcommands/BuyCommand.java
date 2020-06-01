package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.commands.SubCommand;
import me.kodysimpson.chunkcollector.menusystem.menus.BuyMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class BuyCommand extends SubCommand {

    @Override
    public String getName() {
        return "buy";
    }

    @Override
    public String getDescription() {
        return "Buy a chunk collector";
    }

    @Override
    public String getSyntax() {
        return "/collector buy";
    }

    @Override
    public void perform(Player player, String[] args) {

        if (player.hasPermission("collector.buy")){
            new BuyMenu(ChunkCollector.getPlayerMenuUtility(player)).open();
        }else{
            player.sendMessage(ChatColor.GREEN + "You don't have permission to run this command.");
        }

    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return null;
    }

}
