package sqlconnect;

public class Main {
    public static void main(String[] args) {
        PaymentQueue paymentQueue = new PaymentQueue();

        // Adding payment requests to the queue
        paymentQueue.addPaymentRequest("GCash", 200.00);
        paymentQueue.addPaymentRequest("Credit Card", 300.00);
        paymentQueue.addPaymentRequest("Debit Card", 600.00);

        // Process the payments
        paymentQueue.processPayments();
    }
}
