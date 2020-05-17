package me.kodysimpson.chunkcollector.utils;

import me.kodysimpson.chunkcollector.ChunkCollector;

import java.sql.*;
import java.util.UUID;

public class Database {

    public enum CollectionType{
        DROP, CROP;
    }

    //Create a new collector in the database
    public static int createCollector(UUID ownerUUID, CollectionType type){

        try {
            PreparedStatement statement = ChunkCollector.getConnection()
                    .prepareStatement("INSERT INTO Collectors(Type, OwnerUUID, Items, Capacity) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            if (type == CollectionType.CROP){
                statement.setString(1, "CROP");
            }else if(type == CollectionType.DROP){
                statement.setString(1, "DROP");
            }
            statement.setString(2, ownerUUID.toString());
            statement.setString(3, " ");
            statement.setInt(4, 1);

            statement.execute();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating collector failed, no ID obtained.");
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return 0;
    }

    public static Collector findByID(int id){

        PreparedStatement preparedStatement;
        Collector collector = null;

        try{

            preparedStatement = ChunkCollector.getConnection()
                    .prepareStatement("SELECT * FROM Collectors WHERE CollectorID = ?");
            preparedStatement.setInt(1, id);

            ResultSet found = preparedStatement.executeQuery();
            while (found.next()){
                collector = new Collector(UUID.fromString(found.getString("OwnerUUID")), id, BukkitSerialization.fromBase64(found.getString("Items")), CollectionType.valueOf(found.getString("TYPE")), found.getInt("Capacity"), found.getLong("Sold"), found.getDouble("Earned"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return collector;
    }

    public static void updateCollector(Collector collector){

        PreparedStatement statement = null;

        try {

            statement = ChunkCollector.getConnection()
                    .prepareStatement("UPDATE Collectors SET Type = ?, Items = ?, Sold = ?, Earned = ?, Capacity = ? WHERE CollectorID = ?");
            statement.setString(1, collector.getType().toString());
            statement.setString(2, BukkitSerialization.toBase64(collector.getItems()));
            statement.setLong(3, collector.getSold());
            statement.setDouble(4, collector.getEarned());
            statement.setInt(5, collector.getStorageCapacity());
            statement.setInt(6, collector.getId());

            statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error updating collector items in the database");
        }

    }

    public static void deleteCollector(int id){

        PreparedStatement statement;

        try {

            statement = ChunkCollector.getConnection()
                    .prepareStatement("DELETE FROM Collectors WHERE CollectorID = ?");
            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error deleting collector from the DB");
        }

    }

}
