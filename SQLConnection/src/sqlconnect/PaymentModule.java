package sqlconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentModule {
    public boolean makePayment(String paymentMethod, double amount) {
        BankAccount bankAccount = new BankAccount();
        if (bankAccount.updateBalance(amount)) {
            logTransaction(paymentMethod, amount, "SUCCESS");
            System.out.println("Payment successful via " + paymentMethod);
            return true;
        } else {
            logTransaction(paymentMethod, amount, "FAILED");
            System.out.println("Insufficient balance. Payment failed.");
            return false;
        }
    }

    private void logTransaction(String paymentMethod, double amount, String status) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO Transactions (payment_method, amount, status) VALUES (?, ?, ?)")) {
            ps.setString(1, paymentMethod);
            ps.setDouble(2, amount);
            ps.setString(3, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
