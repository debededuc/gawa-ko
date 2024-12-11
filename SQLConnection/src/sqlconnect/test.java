package sqlconnect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Stack;

public class test extends JFrame {
    private JPanel productPanel;
    private JPanel cartPanel;
    private DefaultListModel<String> cartModel;
    private HashMap<String, Integer> productPrices;
    private HashMap<String, String> productImages;
    private Stack<String> transactionHistory;

    // Balances
    private double gcashBalance = 1000.00;
    private double creditCardBalance = 2000.00;
    private double debitCardBalance = 1500.00;

    public test() {
        // Set up the frame
        setTitle("Beyond Collections Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);

        transactionHistory = new Stack<>();
        initializeProducts();

        // Product Panel
        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(2, 3, 10, 10));
        for (String productName : productPrices.keySet()) {
            addProductToPanel(productName);
        }
        add(productPanel, BorderLayout.CENTER);

        // Cart Panel
        cartPanel = new JPanel();
        cartPanel.setLayout(new BorderLayout());
        cartPanel.add(new JLabel("Cart:"), BorderLayout.NORTH);

        cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        cartPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JButton removeButton = new JButton("Remove from Cart");
        removeButton.addActionListener(e -> {
            int selectedIndex = cartList.getSelectedIndex();
            if (selectedIndex != -1) {
                cartModel.remove(selectedIndex);
            }
        });
        cartPanel.add(removeButton, BorderLayout.SOUTH);

        JButton payButton = new JButton("Pay");
        payButton.addActionListener(e -> verifyAndPay());
        cartPanel.add(payButton, BorderLayout.SOUTH);

        add(cartPanel, BorderLayout.EAST);

        // Account Button
        JButton accountButton = new JButton("Account");
        accountButton.addActionListener(e -> openAccountWindow());
        add(accountButton, BorderLayout.WEST);

        // Transaction History Button
        JButton historyButton = new JButton("Transaction History");
        historyButton.addActionListener(e -> showTransactionHistory());
        add(historyButton, BorderLayout.NORTH);

        setVisible(true);

        // Initialize MySQL
        initializeDatabase();
    }

    private void initializeProducts() {
        productPrices = new HashMap<>();
        productPrices.put("Frieren Plush", 500);
        productPrices.put("Himmel Plush", 500);
        productPrices.put("Evangelion Figure", 1000);
        productPrices.put("JJK Plush", 600);
        productPrices.put("Pokemon Toy", 900);
        productPrices.put("Haikyuu Figure", 800);

        productImages = new HashMap<>();
        productImages.put("Frieren Plush", "img/furiri.png");
        productImages.put("Himmel Plush", "himmel_plush.png");
        productImages.put("Evangelion Figure", "evangelion_figure.png");
        productImages.put("JJK Plush", "jjk_plush.png");
        productImages.put("Pokemon Toy", "pokemon_toy.png");
        productImages.put("Haikyuu Figure", "haikyuu_figure.png");
    }

    private void addProductToPanel(String productName) {
        JPanel product = new JPanel();
        product.setLayout(new BorderLayout());

        String imagePath = productImages.get(productName);
        ImageIcon productImage = new ImageIcon(imagePath);
        Image img = productImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        product.add(imageLabel, BorderLayout.CENTER);

        JLabel productLabel = new JLabel("Title: " + productName + " - $" + productPrices.get(productName));
        product.add(productLabel, BorderLayout.NORTH);

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> cartModel.addElement(productName));
        product.add(addToCartButton, BorderLayout.SOUTH);

        productPanel.add(product);
    }

    private void verifyAndPay() {
        String otp = generateOTP();
        JDialog otpDialog = new JDialog(this, "Verify OTP", true);
        otpDialog.setLayout(new BorderLayout());
        otpDialog.setSize(300, 150);

        JLabel promptLabel = new JLabel("Enter OTP: " + otp, SwingConstants.CENTER);
        otpDialog.add(promptLabel, BorderLayout.NORTH);

        JTextField otpField = new JTextField();
        otpDialog.add(otpField, BorderLayout.CENTER);

        JButton verifyButton = new JButton("Verify");
        verifyButton.addActionListener(e -> {
            if (otpField.getText().equals(otp)) {
                otpDialog.dispose();
                openPaymentWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect OTP. Try again!");
            }
        });
        otpDialog.add(verifyButton, BorderLayout.SOUTH);

        otpDialog.setVisible(true);
    }

    private String generateOTP() {
        int otp = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(otp);
    }

    private void openPaymentWindow() {
        String[] paymentMethods = {"GCash", "Credit Card", "Debit Card"};
        String method = (String) JOptionPane.showInputDialog(
                this,
                "Choose a payment method:",
                "Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                paymentMethods,
                paymentMethods[0]
        );

        if (method != null) {
            double totalCost = 0;
            for (int i = 0; i < cartModel.getSize(); i++) {
                totalCost += productPrices.get(cartModel.getElementAt(i));
            }

            if (processPayment(method, totalCost)) {
                JOptionPane.showMessageDialog(this, "Payment Successful! Total: $" + totalCost);
                cartModel.clear(); // Clear the cart
                transactionHistory.push("Paid $" + totalCost + " using " + method);
                updateDatabaseBalances();
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient Balance!");
            }
        }
    }

    private boolean processPayment(String method, double totalCost) {
        switch (method) {
            case "GCash":
                if (gcashBalance >= totalCost) {
                    gcashBalance -= totalCost;
                    return true;
                }
                break;
            case "Credit Card":
                if (creditCardBalance >= totalCost) {
                    creditCardBalance -= totalCost;
                    return true;
                }
                break;
            case "Debit Card":
                if (debitCardBalance >= totalCost) {
                    debitCardBalance -= totalCost;
                    return true;
                }
                break;
        }
        return false;
    }

    private void openAccountWindow() {
        JFrame accountWindow = new JFrame("Account Balances");
        accountWindow.setSize(300, 250);
        accountWindow.setLayout(new BorderLayout());

        String balances = String.format(
                "<html>GCash: $%.2f<br>Credit Card: $%.2f<br>Debit Card: $%.2f</html>",
                gcashBalance, creditCardBalance, debitCardBalance
        );

        JLabel balanceLabel = new JLabel(balances, SwingConstants.CENTER);
        accountWindow.add(balanceLabel, BorderLayout.CENTER);

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> depositAmount());
        accountWindow.add(depositButton, BorderLayout.SOUTH);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> withdrawAmount());
        accountWindow.add(withdrawButton, BorderLayout.NORTH);

        accountWindow.setVisible(true);
    }

    private void depositAmount() {
        String method = (String) JOptionPane.showInputDialog(
                this,
                "Choose deposit method:",
                "Deposit",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"GCash", "Credit Card", "Debit Card"},
                "GCash"
        );

        if (method != null) {
            String amountString = JOptionPane.showInputDialog(this, "Enter deposit amount:");
            double amount = Double.parseDouble(amountString);

            switch (method) {
                case "GCash":
                    gcashBalance += amount;
                    break;
                case "Credit Card":
                    creditCardBalance += amount;
                    break;
                case "Debit Card":
                    debitCardBalance += amount;
                    break;
            }
            JOptionPane.showMessageDialog(this, "Deposit successful!");
            updateDatabaseBalances();
        }
    }

    private void withdrawAmount() {
        String method = (String) JOptionPane.showInputDialog(
                this,
                "Choose withdraw method:",
                "Withdraw",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"GCash", "Credit Card", "Debit Card"},
                "GCash"
        );

        if (method != null) {
            String amountString = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
            double amount = Double.parseDouble(amountString);

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
                return;
            }

            boolean isSufficient = false;

            switch (method) {
                case "GCash":
                    if (gcashBalance >= amount) {
                        gcashBalance -= amount;
                        isSufficient = true;
                    }
                    break;
                case "Credit Card":
                    if (creditCardBalance >= amount) {
                        creditCardBalance -= amount;
                        isSufficient = true;
                    }
                    break;
                case "Debit Card":
                    if (debitCardBalance >= amount) {
                        debitCardBalance -= amount;
                        isSufficient = true;
                    }
                    break;
            }

            if (isSufficient) {
                JOptionPane.showMessageDialog(this, "Withdrawal successful!");
                updateDatabaseBalances();
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient balance.");
            }
        }
    }

    private void showTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transaction history available.");
        } else {
            StringBuilder history = new StringBuilder("<html>");
            for (String transaction : transactionHistory) {
                history.append(transaction).append("<br>");
            }
            history.append("</html>");

            JOptionPane.showMessageDialog(this, new JLabel(history.toString()));
        }
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/store", "root", "12345")) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS balances (method VARCHAR(20), balance DOUBLE)");
            stmt.execute("INSERT IGNORE INTO balances VALUES ('GCash', 1000.0), ('Credit Card', 2000.0), ('Debit Card', 1500.0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDatabaseBalances() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/store", "root", "12345")) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE balances SET balance = ? WHERE method = ?");
            pstmt.setDouble(1, gcashBalance);
            pstmt.setString(2, "GCash");
            pstmt.executeUpdate();

            pstmt.setDouble(1, creditCardBalance);
            pstmt.setString(2, "Credit Card");
            pstmt.executeUpdate();

            pstmt.setDouble(1, debitCardBalance);
            pstmt.setString(2, "Debit Card");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new test();
    }
}
