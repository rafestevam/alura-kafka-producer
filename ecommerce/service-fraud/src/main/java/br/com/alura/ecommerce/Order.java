package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String orderID;
    private final BigDecimal amount;
    private final String email;

    public Order(String orderID, BigDecimal amount, String email) {
        this.orderID = orderID;
        this.amount = amount;
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Order{" +
                ", orderID='" + orderID + '\'' +
                ", email='" + email + '\'' +
                ", amount=" + amount +
                '}';
    }
}
