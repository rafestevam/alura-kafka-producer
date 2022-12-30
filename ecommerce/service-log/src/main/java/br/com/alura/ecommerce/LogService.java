package br.com.alura.ecommerce;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

public class LogService {

    public static void main(String[] args) {
        var logService = new LogService();
        try(var service = new KafkaService<String>(
            LogService.class.getSimpleName(), 
            Pattern.compile("ECOMMERCE.*"), 
            logService::parse, 
            String.class,
            Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
            )){
            try {
                service.run();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void parse(ConsumerRecord<String, String> record){
        System.out.println("----------------------------------------");
        System.out.println("LOG " + record.topic());
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
    }
}
