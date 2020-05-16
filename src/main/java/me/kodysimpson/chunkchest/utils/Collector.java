package me.kodysimpson.chunkchest.utils;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class Collector {

    private int id;
    private UUID ownerUUID;
    private Database.CollectionType type;
    private int storageCapacity;

    private long sold;
    private double earned;

    private ArrayList<ItemStack> items;

    public Collector(UUID ownerUUID, int id, ArrayList<ItemStack> items, Database.CollectionType type, int storageCapacity, long sold, double earned) {
        this.ownerUUID = ownerUUID;
        this.id = id;
        this.items = items;
        this.type = type;
        this.storageCapacity = storageCapacity;
        this.sold = sold;
        this.earned = earned;
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

    public Database.CollectionType getType() {
        return type;
    }

    public void setType(Database.CollectionType type) {
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
}
