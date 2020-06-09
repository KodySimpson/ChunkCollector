package me.kodysimpson.chunkcollector.utils;

import com.sun.rowset.CachedRowSetImpl;
import me.kodysimpson.chunkcollector.ChunkCollector;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class Database {

    //Create and establish connection with SQL Database
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");

            try {
                connection = DriverManager.getConnection(ChunkCollector.getConnectionURL());

            } catch (SQLException e) {
                System.out.println("Unable to establish a connection with the database");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Unable to find the h2 DB sql driver");
        }
        return connection;
    }

    //Initialize database tables
    public static void initializeDatabase() {

        try {

            //Create the desired tables for our database if they don't exist
            Statement statement = getConnection().createStatement();
            //Table for storing all of the locks
            statement.execute("CREATE TABLE IF NOT EXISTS Collectors(CollectorID int NOT NULL IDENTITY(1, 1), Type varchar(255), OwnerUUID varchar(255), Items clob, Sold long, Earned double, Capacity int, Fortune int);");
            statement.execute("CREATE TABLE IF NOT EXISTS OfflineProfits(ID int NOT NULL IDENTITY(1, 1), UUID varchar(255), TotalEarned double, TotalSold long)");

            System.out.println("Database loaded");

            statement.close();

        } catch (SQLException e) {
            System.out.println("Unable to initialize database.");
        }

    }

    //Create a new collector in the database
    public static int createCollector(UUID ownerUUID, CollectionType type) {

        try {
            PreparedStatement statement = getConnection()
                    .prepareStatement("INSERT INTO Collectors(Type, OwnerUUID, Items, Capacity, Fortune) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            if (type == CollectionType.CROP) {
                statement.setString(1, "CROP");
            } else if (type == CollectionType.DROP) {
                statement.setString(1, "DROP");
            }
            statement.setString(2, ownerUUID.toString());
            statement.setString(3, " ");
            statement.setInt(4, 1);
            statement.setInt(5, 0);

            statement.execute();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating collector failed, no ID obtained.");
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return 0;
    }

    public static void insertOfflineProfit(OfflinePlayer p, double totalEarned, long totalSold){

        try {
            PreparedStatement statement = getConnection()
                    .prepareStatement("INSERT INTO OfflineProfits(UUID, TotalEarned, TotalSold) VALUES (?, ?, ?)");
            statement.setString(1, p.getUniqueId().toString());
            statement.setDouble(2, totalEarned);
            statement.setLong(3, totalSold);

            statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static ResultSet findAllByUUID(Player p){
        PreparedStatement preparedStatement = null;

        try{
            preparedStatement = getConnection().prepareStatement("SELECT * FROM OfflineProfits WHERE UUID = ?");
            preparedStatement.setString(1, p.getUniqueId().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet;

        }catch (SQLException ex){
            System.out.println(ex);
        }

        return null;
    }

    public static void deleteAllByUUID(Player p){
        PreparedStatement statement;

        try {

            statement = getConnection()
                    .prepareStatement("DELETE FROM OfflineProfits WHERE UUID = ?");
            statement.setString(1, p.getUniqueId().toString());

            statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error deleting earnings entries for " + p.getUniqueId().toString());
        }
    }

    public static Collector findByID(int id) {

        PreparedStatement preparedStatement = null;
        Collector collector = null;

        try {

            preparedStatement = getConnection()
                    .prepareStatement("SELECT * FROM Collectors WHERE CollectorID = ?");
            preparedStatement.setInt(1, id);

            ResultSet found = preparedStatement.executeQuery();
            while (found.next()) {
                collector = new Collector(UUID.fromString(found.getString("OwnerUUID")), id, BukkitSerialization.fromBase64(found.getString("Items")), CollectionType.valueOf(found.getString("TYPE")), found.getInt("Capacity"), found.getLong("Sold"), found.getDouble("Earned"), found.getInt("Fortune"));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return collector;
    }

    public static void updateCollector(Collector collector) {

        PreparedStatement statement;

        try {

            statement = getConnection()
                    .prepareStatement("UPDATE Collectors SET Type = ?, Items = ?, Sold = ?, Earned = ?, Capacity = ?, Fortune = ? WHERE CollectorID = ?");
            statement.setString(1, collector.getType().toString());
            statement.setString(2, BukkitSerialization.toBase64(collector.getItems()));
            statement.setLong(3, collector.getSold());
            statement.setDouble(4, collector.getEarned());
            statement.setInt(5, collector.getStorageCapacity());
            statement.setInt(6, collector.getFortuneLevel());
            statement.setInt(7, collector.getId());

            statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error updating collector items in the database");
        }


    }

    public static void deleteCollector(int id) {

        PreparedStatement statement;

        try {

            statement = getConnection()
                    .prepareStatement("DELETE FROM Collectors WHERE CollectorID = ?");
            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error deleting collector from the DB");
        }

    }

    public enum CollectionType {
        DROP, CROP
    }

}
