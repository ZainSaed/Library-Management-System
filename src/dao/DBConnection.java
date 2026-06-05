package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {


	private static final String URL = "jdbc:mysql://127.0.0.1:3306/library_db";
    private static final String USER     = "root";      
    private static final String PASSWORD = "";          


    private static DBConnection instance   = null;
    private        Connection   connection = null;


    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("✅ Database connected successfully.");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver not found. Add JAR to Build Path.");
            e.printStackTrace();

        } catch (SQLException e) {
            System.out.println("❌ Database connection failed. Is XAMPP MySQL running?");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance.connection;
    }

    public static void closeConnection() {
        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
                instance = null;
                System.out.println("✅ Database connection closed.");
            } catch (SQLException e) {
                System.out.println("❌ Error closing connection.");
                e.printStackTrace();
            }
        }
    }
}
