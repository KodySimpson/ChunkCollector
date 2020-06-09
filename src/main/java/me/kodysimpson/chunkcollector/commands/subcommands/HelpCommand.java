package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.commands.CommandManager;
import me.kodysimpson.chunkcollector.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends SubCommand {

    @Override
    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Help.name"));
    }

    @Override
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Help.description"));
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', ChunkCollector.getPlugin().getConfig().getString("Commands.Help.syntax"));
    }

    @Override
    public void perform(Player p, String[] args) {

        CommandManager commandManager = new CommandManager();

        p.sendMessage(" ");
        p.sendMessage(ChatColor.GREEN + "======= " + ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Chunk" + ChatColor.AQUA + "Collector" + ChatColor.GRAY + "] " + ChatColor.YELLOW + "Commands " + ChatColor.GREEN + "=======");
        p.sendMessage(" ");
        for (int i = 0; i < commandManager.getSubCommands().size(); i++){
            p.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + commandManager.getSubCommands().get(i).getSyntax() + " - " + ChatColor.GRAY + commandManager.getSubCommands().get(i).getDescription());
        }
        p.sendMessage(" ");
        p.sendMessage(ChatColor.GREEN + "=====================================");
        p.sendMessage(" ");

    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return null;
    }

}
