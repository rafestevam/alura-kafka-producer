package br.com.alura.ecommerce;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, T> consumer;
    private ConsumerFunction<T> parse;

    public KafkaService(String groupID, String topic, ConsumerFunction parse, Class<T> type, Map<String, String> extraProps) {
        this(groupID, parse, type, extraProps);
        consumer.subscribe(Collections.singletonList(topic));
    }
    
    public KafkaService(String groupID, Pattern topic, ConsumerFunction parse, Class<T> type, Map<String, String> extraProps) {
        this(groupID, parse, type, extraProps);
        consumer.subscribe(topic);
    }

    private KafkaService(String groupID, ConsumerFunction parse, Class<T> type, Map<String, String> extraProps){
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(properties(type, groupID, extraProps));
    }

    public void run() throws ExecutionException, InterruptedException {
        while(true){
            var records = consumer.poll(Duration.ofMillis(100));
            if(!records.isEmpty()){
                System.out.println("#" + records.count() + " records found...");
                for(var record : records) {
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        //Tratamento de qualquer tipo de exceção
                        //O objetivo é pegar a proxima mensagem da fila e tratar
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private Properties properties(Class<T> type, String groupID, Map<String, String> extraProps) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "vadhgcs62328.hycloud.softwareag.com:11001");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //Desserealizando com um Desserializador customizado herdado de GSONBuilder
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        //properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        properties.putAll(extraProps);
        return properties;
    }

    @Override
    public void close() {
        consumer.close();        
    }
    
}
