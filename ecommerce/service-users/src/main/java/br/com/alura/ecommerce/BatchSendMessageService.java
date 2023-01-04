package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {
    private final Connection connection;
    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<User>();

    BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users(" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200))");
        }catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        var batchService = new BatchSendMessageService();
        try(var service = new KafkaService<>(
                CreateUserService.class.getSimpleName(),
                "SEND_MESSAGE_TO_ALL_USERS",
                batchService::parse,
                String.class,
                Map.of())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) throws SQLException, ExecutionException, InterruptedException {
        System.out.println("----------------------------------------");
        System.out.println("Processing new batch");
        System.out.println("Topic: " + record.value());

        for(User user : getAllUsers()){
            userDispatcher.send(record.value(), user.getUuid(), user);
        }

    }

    private List<User> getAllUsers() throws SQLException {
        List<User> allUsers = new ArrayList<>();
        var statement = connection.prepareStatement("select uuid from Users");
        var results = statement.executeQuery();

        while(results.next()){
            allUsers.add(new User(results.getString(1)));
        }

        return allUsers;
    }
}
