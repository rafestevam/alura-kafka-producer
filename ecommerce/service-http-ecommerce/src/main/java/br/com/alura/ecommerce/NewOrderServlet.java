package br.com.alura.ecommerce;

import org.eclipse.jetty.servlet.Source;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Instanciando um KafkaDispatcher do tipo "ORDER"
        try(var orderDispatcher = new KafkaDispatcher<Order>()) {
            try(var emailDispatcher = new KafkaDispatcher<Email>()) {
                var userEmail = Math.random() + "@email.com";

                try {
                    //Envio de um unico pedido por vez - Acesso via web
                    var orderID = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);

                    var order = new Order(orderID, amount, userEmail);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", userEmail, order);

                    var subject = "Order #:" + orderID;
                    var body = "Thank you! We are processing your order #" + orderID +"!";
                    var email = new Email(body, subject);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userEmail, email);

                    System.out.println("New order sent successfully");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("New order sent!");
                } catch (InterruptedException e) {
                    throw new ServletException(e);
                } catch (ExecutionException e) {
                    throw new ServletException(e);
                }

            }
        }
    }
}
