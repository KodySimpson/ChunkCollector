package me.kodysimpson.chunkcollector.commands;

import me.kodysimpson.chunkcollector.commands.subcommands.BuyCommand;
import me.kodysimpson.chunkcollector.commands.subcommands.GiveCommand;
import me.kodysimpson.chunkcollector.commands.subcommands.HelpCommand;
import me.kodysimpson.chunkcollector.commands.subcommands.ReloadCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {

    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){
        subcommands.add(new GiveCommand());
        subcommands.add(new BuyCommand());
        subcommands.add(new ReloadCommand());
        subcommands.add(new HelpCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player p = (Player) sender;

            if (args.length > 0){
                for (int i = 0; i < getSubCommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                        getSubCommands().get(i).perform(p, args);
                    }
                }
            }else if(args.length == 0){

                new HelpCommand().perform(p, args);

            }

        }


        return true;
    }

    public ArrayList<SubCommand> getSubCommands(){
        return subcommands;
    }

}
