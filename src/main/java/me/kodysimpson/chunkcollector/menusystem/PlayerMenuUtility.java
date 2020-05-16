package me.kodysimpson.chunkcollector.menusystem;

import me.kodysimpson.chunkcollector.utils.Database;
import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private Player owner;
    private int collectorID;
    private Database.CollectionType type;

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
        return type;
    }

    public void setType(Database.CollectionType type) {
        this.type = type;
    }
}
