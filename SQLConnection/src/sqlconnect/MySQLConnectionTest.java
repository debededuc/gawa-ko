package sqlconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnectionTest {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mylocaldb",
                "root",
                "12345"
            );
            System.out.println("Connection created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
