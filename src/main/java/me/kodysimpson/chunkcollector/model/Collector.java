package me.kodysimpson.chunkcollector.model;

import me.kodysimpson.chunkcollector.utils.CollectionType;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Used to model Chunk Collectors to then do operations upon them and get information
 */
public class Collector {

    private int id;
    private UUID ownerUUID;
    private CollectionType type;
    private int storageCapacity;
    private int fortuneLevel;
    private boolean enabled;

    private long sold;
    private double earned;

    private ArrayList<ItemStack> items;

    public Collector() {
    }

    public Collector(UUID ownerUUID, CollectionType type) {
        this.ownerUUID = ownerUUID;
        this.type = type;
    }

    public Collector(UUID ownerUUID, int id, ArrayList<ItemStack> items, CollectionType type, int storageCapacity, long sold, double earned, int fortuneLevel, boolean enabled) {
        this.ownerUUID = ownerUUID;
        this.id = id;
        this.items = items;
        this.type = type;
        this.storageCapacity = storageCapacity;
        this.sold = sold;
        this.earned = earned;
        this.fortuneLevel = fortuneLevel;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public CollectionType getType() {
        return type;
    }

    public void setType(CollectionType type) {
        this.type = type;
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemStack> items) {
        this.items = items;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(int storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public long getSold() {
        return sold;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }

    public double getEarned() {
        return earned;
    }

    public void setEarned(double earned) {
        this.earned = earned;
    }

    public int getFortuneLevel() {
        return fortuneLevel;
    }

    public void setFortuneLevel(int fortuneLevel) {
        this.fortuneLevel = fortuneLevel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
