package me.kodysimpson.chunkcollector.menusystem;

import me.kodysimpson.chunkcollector.utils.Collector;
import me.kodysimpson.chunkcollector.utils.Database;
import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private Player owner;
    private int collectorID;
    private Database.CollectionType type;
    private Database.CollectionType buyType;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public int getCollectorID() {
        return collectorID;
    }

    public void setCollectorID(int collectorID) {
        this.collectorID = collectorID;
    }

    public Player getOwner() {
        return owner;
    }

    public Database.CollectionType getType() {
        Collector collector = Database.findByID(collectorID);
        return collector.getType();
    }

    public Database.CollectionType getBuyType() {
        return buyType;
    }

    public void setBuyType(Database.CollectionType buyType) {
        this.buyType = buyType;
    }
}
