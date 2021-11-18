package me.kodysimpson.chunkcollector.database;

import me.kodysimpson.chunkcollector.ChunkCollector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static Connection connection;

    //Data access object for interacting with the DB
    private static CollectorDataAccess collectorDataAccess;

    public static CollectorDataAccess getCollectorDataAccess() {
        return collectorDataAccess;
    }

    //Create and establish connection with SQL Database
    public static Connection getConnection() {

        //Try to establish a connection if it has not yet been made
        if (connection == null){
            try {
                Class.forName("org.h2.Driver");

                try {
                    connection = DriverManager.getConnection(ChunkCollector.getConnectionURL());

                } catch (SQLException e) {
                    System.out.println("Unable to establish a connection with the database");
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException ex) {
                System.out.println("Unable to find the h2 DB sql driver");
            }
        }
        return connection;
    }

    //Initialize database tables
    public static void initializeDatabase() {

        try {

            //Create the desired tables for our database if they don't exist
            Statement statement = getConnection().createStatement();
            //Table for storing all of the locks
            statement.execute("CREATE TABLE IF NOT EXISTS Collectors(CollectorID int NOT NULL IDENTITY(1, 1), Type varchar(255), OwnerUUID varchar(255), Items clob, Sold long, Earned double, Capacity int, Fortune int, isEnabled boolean);");
            statement.execute("CREATE TABLE IF NOT EXISTS OfflineProfits(ID int NOT NULL IDENTITY(1, 1), UUID varchar(255), TotalEarned double, TotalSold long)");

            collectorDataAccess = new CollectorDataAccess();

            System.out.println("Database loaded");

            statement.close();

        } catch (SQLException e) {
            System.out.println("Database intialization error.");
            e.printStackTrace();
        }


    }
}
