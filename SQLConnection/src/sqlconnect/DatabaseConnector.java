package sqlconnect;

import java.sql.*;

public class DatabaseConnector {
    // Method to establish the database connection
    public static Connection getConnection() {
        Connection conn = null;

        try {
            // Establish the connection
            String url = "jdbc:mysql://localhost:3306/PaymentSystem";  // Your database URL
            String username = "root";  // Your MySQL username (replace with your actual username)
            String password = "12345";  // Your MySQL password (replace with your actual password)

            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            conn = DriverManager.getConnection(url, username, password);

            System.out.println("Connection Successful!");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static void main(String[] args) {
        // Test the connection
        getConnection();
    }
}
