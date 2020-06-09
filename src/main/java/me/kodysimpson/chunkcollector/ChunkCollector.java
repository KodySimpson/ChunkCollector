package me.kodysimpson.chunkcollector;

import me.kodysimpson.chunkcollector.commands.CommandManager;
import me.kodysimpson.chunkcollector.listeners.CollectorListener;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.tasks.CollectDrops;
import me.kodysimpson.chunkcollector.utils.Database;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public final class ChunkCollector extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
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

        //Command manager
        getCommand("collector").setExecutor(new CommandManager());

        //Listeners
        Bukkit.getServer().getPluginManager().registerEvents(new CollectorListener(), this);

        //Collection task[starts up 1 minute from startup]
        BukkitTask task = new CollectDrops().runTaskTimer(this, 1200, getConfig().getLong("collection-duration"));

    }

    @Override
    public void onDisable() {
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
