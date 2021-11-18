package me.kodysimpson.chunkcollector;

import me.kodysimpson.chunkcollector.commands.subcommands.BuyCommand;
import me.kodysimpson.chunkcollector.commands.subcommands.GiveCommand;
import me.kodysimpson.chunkcollector.commands.subcommands.ReloadCommand;
import me.kodysimpson.chunkcollector.database.Database;
import me.kodysimpson.chunkcollector.listeners.CollectorListener;
import me.kodysimpson.chunkcollector.tasks.AutoKill;
import me.kodysimpson.chunkcollector.tasks.ChunkScanner;
import me.kodysimpson.chunkcollector.tasks.MineTask;
import me.kodysimpson.simpapi.command.CommandList;
import me.kodysimpson.simpapi.command.CommandManager;
import me.kodysimpson.simpapi.command.SubCommand;
import me.kodysimpson.simpapi.menu.MenuManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class ChunkCollector extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    //collector ID - fully grown crops
    public static HashMap<Integer, ArrayList<Block>> crops = new HashMap<>();
    private static Economy econ = null;
    private static ChunkCollector plugin;
    private static String url;

    public static ChunkCollector getPlugin() {
        return plugin;
    }

    public static String getConnectionURL() {
        return url;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static HashMap<Integer, ArrayList<Block>> getCrops() {
        return crops;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        //Setup/Load Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();

        plugin = this;

        //Vault setup
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Create database tables if not already generated
        url = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "/data/chunkcollector";
        Database.initializeDatabase();

        try {
            CommandManager.createCoreCommand(this, "collector", "Use the chunk collector plugin", "/collector", new CommandList() {
                @Override
                public void displayCommandList(CommandSender p, List<SubCommand> subCommandList) {
                    p.sendMessage(" ");
                    p.sendMessage(ChatColor.GREEN + "======= " + ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Chunk" + ChatColor.AQUA + "Collector" + ChatColor.GRAY + "] " + ChatColor.YELLOW + "Commands " + ChatColor.GREEN + "=======");
                    p.sendMessage(" ");
                    for (SubCommand subCommand : subCommandList) {
                        p.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + subCommand.getSyntax() + " - " + ChatColor.GRAY + subCommand.getDescription());
                    }
                    p.sendMessage(" ");
                    p.sendMessage(ChatColor.GREEN + "=====================================");
                    p.sendMessage(" ");
                }
            }, List.of("cc", "chunkcollector"), BuyCommand.class, GiveCommand.class, ReloadCommand.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        MenuManager.setup(getServer(), this);

        //Listeners
        Bukkit.getServer().getPluginManager().registerEvents(new CollectorListener(), this);

        //Collection task[starts up 1 minute from startup]
        new ChunkScanner().runTaskTimer(this, 1200, getConfig().getLong("collection-duration"));

        if (getConfig().getBoolean("auto-kill")) {
            new AutoKill().runTaskTimer(this, 1200, getConfig().getLong("auto-kill-interval"));
        }

        new MineTask().runTaskTimer(this, 600, 1200);

    }

    @Override
    public void onDisable() {

        //Close the current connection to the DB
        try {
            System.out.println("CLOSING THE DATABASE CONNECTION");
            Database.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Plugin shutdown logic
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
