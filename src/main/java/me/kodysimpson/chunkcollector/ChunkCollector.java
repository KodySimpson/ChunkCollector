package me.kodysimpson.chunkcollector;

import me.kodysimpson.chunkcollector.commands.CommandManager;
import me.kodysimpson.chunkcollector.listeners.CollectorListener;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.tasks.CollectDrops;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public final class ChunkCollector extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    private static ChunkCollector plugin;

    private static Connection connection;

    public static HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    public static HashMap<Integer, ArrayList<Block>> crops = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Setup Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        try {
            Class.forName("org.h2.Driver");

            String url = "jdbc:h2:" + getDataFolder().getAbsolutePath() + "/data/chunkchest";

            try {
                connection = DriverManager.getConnection(url);

                //Create the desired tables for our database if they don't exist
                Statement statement = connection.createStatement();
                //Table for storing all of the locks
                statement.execute("CREATE TABLE IF NOT EXISTS Collectors(CollectorID int NOT NULL IDENTITY(1, 1), Type varchar(255), OwnerUUID varchar(255), Items clob, Sold long, Earned double, Capacity int, Fortune int);");

                System.out.println("Database loaded");

            } catch (SQLException e) {
                System.out.println("Unable to establish a connection with the database");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Unable to find the h2 DB sql driver");
        }

        getCommand("collector").setExecutor(new CommandManager());

        Bukkit.getServer().getPluginManager().registerEvents(new CollectorListener(), this);

        BukkitTask task = new CollectDrops().runTaskTimer(this, 1200, 1200);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    public static ChunkCollector getPlugin() {
        return plugin;
    }

    public static Connection getConnection() {
        return connection;
    }

    //Provide a player and return a menu system for that player
    //create one if they don't already have one
    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) { //See if the player has a lockmenusystem "saved" for them

            //This player doesn't. Make one for them add add it to the hashmap
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p); //Return the object by using the provided player
        }
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

    public static Economy getEconomy() {
        return econ;
    }

    public static HashMap<Integer, ArrayList<Block>> getCrops() {
        return crops;
    }
}
