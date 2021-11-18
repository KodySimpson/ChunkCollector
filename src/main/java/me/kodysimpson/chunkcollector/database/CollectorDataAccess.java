package me.kodysimpson.chunkcollector.database;

import me.kodysimpson.chunkcollector.utils.ItemSerializer;
import me.kodysimpson.chunkcollector.utils.CollectionType;
import me.kodysimpson.chunkcollector.model.Collector;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CollectorDataAccess{

    public List<Collector> findAll() {

        List<Collector> collectors = new ArrayList<>();
        PreparedStatement preparedStatement;

        try{
            preparedStatement = Database.getConnection().prepareStatement("SELECT * FROM COLLECTORS");
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()){
                Collector collector = new Collector(UUID.fromString(result.getString("OwnerUUID")), result.getInt("CollectorID"), ItemSerializer.fromBase64(result.getString("Items")), CollectionType.valueOf(result.getString("TYPE")), result.getInt("Capacity"), result.getLong("Sold"), result.getDouble("Earned"), result.getInt("Fortune"), result.getBoolean("isEnabled"));
                collectors.add(collector);
            }

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

        return collectors;
    }

    public boolean existsById(int id) {
        return false;
    }

    public Collector findById(int id) {
        PreparedStatement preparedStatement = null;
        Collector collector = null;

        try {

            preparedStatement = Database.getConnection()
                    .prepareStatement("SELECT * FROM Collectors WHERE CollectorID = ?");
            preparedStatement.setInt(1, id);

            ResultSet found = preparedStatement.executeQuery();
            while (found.next()) {
                collector = new Collector(UUID.fromString(found.getString("OwnerUUID")), id, ItemSerializer.fromBase64(found.getString("Items")), CollectionType.valueOf(found.getString("TYPE")), found.getInt("Capacity"), found.getLong("Sold"), found.getDouble("Earned"), found.getInt("Fortune"), found.getBoolean("isEnabled"));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return collector;
    }

    public Collector insert(Collector collector) {

        try {
            PreparedStatement statement = Database.getConnection()
                    .prepareStatement("INSERT INTO Collectors(Type, OwnerUUID, Items, Capacity, Fortune, isEnabled) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, collector.getType().getAsString());
            statement.setString(2, collector.getOwnerUUID().toString());
            statement.setString(3, " ");
            statement.setInt(4, 1);
            statement.setInt(5, 0);
            statement.setBoolean(6, true);

            statement.execute();

            //Get the id of the just created collector
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    collector.setId(generatedKeys.getInt(1));
                    return collector;
                } else {
                    throw new SQLException("Creating collector failed, no ID obtained.");
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public void update(Collector collector) {
        PreparedStatement statement;

        try {

            statement = Database.getConnection()
                    .prepareStatement("UPDATE Collectors SET Type = ?, Items = ?, Sold = ?, Earned = ?, Capacity = ?, Fortune = ?, isEnabled = ? WHERE CollectorID = ?");
            statement.setString(1, collector.getType().getAsString());
            statement.setString(2, ItemSerializer.toBase64(collector.getItems()));
            statement.setLong(3, collector.getSold());
            statement.setDouble(4, collector.getEarned());
            statement.setInt(5, collector.getStorageCapacity());
            statement.setInt(6, collector.getFortuneLevel());
            statement.setBoolean(7, collector.isEnabled());
            statement.setInt(8, collector.getId());

            statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error updating collector items in the database");
        }
    }

    public void deleteById(int id) {

        PreparedStatement statement;

        try {

            statement = Database.getConnection()
                    .prepareStatement("DELETE FROM Collectors WHERE CollectorID = ?");
            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error deleting collector from the DB");
        }

    }

    public void insertOfflineProfit(OfflinePlayer p, double totalEarned, long totalSold){

        try {
            PreparedStatement statement = Database.getConnection()
                    .prepareStatement("INSERT INTO OfflineProfits(UUID, TotalEarned, TotalSold) VALUES (?, ?, ?)");
            statement.setString(1, p.getUniqueId().toString());
            statement.setDouble(2, totalEarned);
            statement.setLong(3, totalSold);

            statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public ResultSet findAllByUUID(Player p){
        PreparedStatement preparedStatement = null;

        try{
            preparedStatement = Database.getConnection().prepareStatement("SELECT * FROM OfflineProfits WHERE UUID = ?");
            preparedStatement.setString(1, p.getUniqueId().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet;

        }catch (SQLException ex){
            System.out.println(ex);
        }

        return null;
    }

    public void deleteAllByUUID(Player p){
        PreparedStatement statement;

        try {

            statement = Database.getConnection()
                    .prepareStatement("DELETE FROM OfflineProfits WHERE UUID = ?");
            statement.setString(1, p.getUniqueId().toString());

            statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error deleting earnings entries for " + p.getUniqueId().toString());
        }
    }
}
