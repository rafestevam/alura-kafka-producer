package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrder
{
    public static void main( String[] args ) throws InterruptedException, ExecutionException{
        //Instanciando um KafkaDispatcher do tipo "ORDER"
        try(var orderDispatcher = new KafkaDispatcher<Order>()) {
            try(var emailDispatcher = new KafkaDispatcher<Email>()) {
                for(var i = 0; i < 10; i++){
                    var userID = UUID.randomUUID().toString();
                    var orderID = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    
                    var order = new Order(userID, orderID, amount);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", userID, order);
                    
                    var subject = "Order #:" + orderID;
                    var body = "Thank you! We are processing your order #" + orderID +"!";
                    var email = new Email(body, subject);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userID, email);
                }
            }
        }
    }
}
