package me.kodysimpson.chunkcollector.commands.subcommands;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.menusystem.menus.BuyMenu;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.command.SubCommand;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BuyCommand extends SubCommand {

    @Override
    public String getName() {
        return ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Commands.Buy.name"));
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Commands.Buy.description"));
    }

    @Override
    public String getSyntax() {
        return ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Commands.Buy.syntax"));
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        if (sender instanceof Player p) {
            if (p.hasPermission("chunkcollector.buy")) {
                try {
                    MenuManager.openMenu(BuyMenu.class, p);
                } catch (MenuManagerException | MenuManagerNotSetupException e) {
                    e.printStackTrace();
                }
            } else {
                p.sendMessage(ColorTranslator.translateColorCodes(ChunkCollector.getPlugin().getConfig().getString("Messages.no-permission")));
            }
        } else {
            sender.sendMessage("You must be a player to send this command.");
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }


}
