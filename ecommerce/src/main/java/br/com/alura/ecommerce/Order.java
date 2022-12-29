package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final String userID, orderID;
    private final BigDecimal amount;
    
    public Order(String userID, String orderID, BigDecimal amount) {
        this.userID = userID;
        this.orderID = orderID;
        this.amount = amount;
    }



}
