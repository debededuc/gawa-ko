package sqlconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankAccount {
    private static final int ACCOUNT_ID = 1; // Dummy account ID

    public double getBalance() {
        double balance = 0.0;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT balance FROM BankAccount WHERE account_id = ?")) {
            ps.setInt(1, ACCOUNT_ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public boolean updateBalance(double amount) {
        double currentBalance = getBalance();
        if (currentBalance >= amount) {
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE BankAccount SET balance = balance - ? WHERE account_id = ?")) {
                ps.setDouble(1, amount);
                ps.setInt(2, ACCOUNT_ID);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
