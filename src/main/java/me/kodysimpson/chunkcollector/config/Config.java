package me.kodysimpson.chunkcollector.config;

import me.kodysimpson.chunkcollector.ChunkCollector;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    /*
        MESSAGES CONFIG
     */

    private static FileConfiguration config = ChunkCollector.getPlugin().getConfig();

    public static String NO_PERMISSION = trans(config.getString("Messages.no-permission"));

    public static String GOT_COLLECTOR_DROP = trans(config.getString("Messages.got-collector.drop"));
    public static String GOT_COLLECTOR_CROP = trans(config.getString("Messages.got-collector.crop"));

    public static String GIVEN_COLLECTOR_DROP = trans(config.getString("Messages.given-collector.drop"));
    public static String GIVEN_COLLECTOR_CROP = trans(config.getString("Messages.given-collector.crop"));

    public static String UPGRADE_STORAGE = trans(config.getString("Messages.upgrade-complete.storage"));
    public static String UPGRADE_FORTUNE = trans(config.getString("Messages.upgrade-complete.fortune"));
    public static String MAX_STORAGE = trans(config.getString("Messages.max-storage"));
    public static String MAX_FORTUNE = trans(config.getString("Messages.max-fortune"));
    public static String CANT_AFFORD_UPGRADE = trans(config.getString("Messages.cant-afford-upgrade"));

    public static String RELOADED = trans(config.getString("Messages.reloaded"));

    public static String FIRST_PAGE = trans(config.getString("Messages.first-page"));
    public static String LAST_PAGE = trans(config.getString("Messages.last-page"));

    private static String trans(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
