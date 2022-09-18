package com.example.specific.v1;

import com.example.Customer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class AvroSchemaRegistryProducer<T> {

    public static void main(String[] args) {
        Properties properties = new Properties();
        // normal producer
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
        properties.setProperty("acks", "all");
        properties.setProperty("retries", "10");
        // avro part
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");

        org.apache.kafka.clients.producer.Producer<String, Customer> producer = new KafkaProducer<String, Customer>(properties);

//        org.apache.kafka.clients.producer.Producer<String, T> producer = new KafkaProducer<String, T>(properties);

        String topic = "customer-avro";


        //  Jackson to get data from json => class

        // copied from avro examples
        Customer customer = Customer.newBuilder()
//                .setAge(34)
                .setAutomatedEmail(false)
                .setFirstName("John")
//                .setLastName("Doe")
                .setHeight(178f)
                .setWeight(75f)
                .build();

        ProducerRecord<String, Customer> producerRecord = new ProducerRecord<String, Customer>(
                topic, customer
        );

        System.out.println(customer);
        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.println(metadata);
                } else {
                    exception.printStackTrace();
                }
            }
        });

        producer.flush();
        producer.close();

    }

}
