package sqlconnect;

import java.util.LinkedList;
import java.util.Queue;

public class PaymentQueue {
    private Queue<PaymentRequest> queue = new LinkedList<>();

    public void addPaymentRequest(String paymentMethod, double amount) {
        queue.add(new PaymentRequest(paymentMethod, amount));
        System.out.println("Payment request added to the queue: " + paymentMethod + " - $" + amount);
    }

    public void processPayments() {
        PaymentModule paymentModule = new PaymentModule();
        while (!queue.isEmpty()) {
            PaymentRequest request = queue.poll();
            System.out.println("Processing payment: " + request.paymentMethod + " - $" + request.amount);
            paymentModule.makePayment(request.paymentMethod, request.amount);
        }
    }

    static class PaymentRequest {
        String paymentMethod;
        double amount;

        PaymentRequest(String paymentMethod, double amount) {
            this.paymentMethod = paymentMethod;
            this.amount = amount;
        }
    }
}
