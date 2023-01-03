package br.com.alura.ecommerce;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

//Utilizando generics para serializar as mensagens no Kafka de maneira customizada
public class KafkaDispatcher<T> implements Closeable{

    private KafkaProducer<String, T> producer;

    KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private static Properties properties(){
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "vadhgcs62328.hycloud.softwareag.com:11001");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //Utilizando um serializador customizado herdado de GSONBuilder
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        //properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //Configuração que garante o reliability do cluster Kafka
        // - valor "ALL" garante a entrega e o sync para todos os nós e replicas do cluster
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    public void send(String topic, String key, T value) throws InterruptedException, ExecutionException {
        var record = new ProducerRecord<>(topic, key, value);
        Callback callback = (data, ex) -> {
            if(ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("sucesso enviado " + data.topic() + "::: partition " + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
        };
        producer.send(record, callback).get();
    }

    @Override
    public void close() {
        producer.close();        
    }
    
}
