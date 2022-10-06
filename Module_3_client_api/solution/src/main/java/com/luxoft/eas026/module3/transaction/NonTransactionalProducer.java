package com.luxoft.eas026.module3.transaction;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NonTransactionalProducer {
    private static final Logger LOG = LoggerFactory.getLogger(NonTransactionalProducer.class);

    private static final String BOOTSTRAP_SERVERS = ":9092,:9093,:9094";
    private static final String TOPIC1 = "topic1"; // auto creating
    private static final String TOPIC2 = "topic2";
    private static final String CLIENT_ID = "ex37";

    public static void throwException() throws InterruptedException {
        throw new InterruptedException();
    }

    @SuppressWarnings("boxing")
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());

        try (Producer<String, Integer> producer = new KafkaProducer<>(props)) {
            final ProducerRecord<String, Integer> data1 = new ProducerRecord<>(TOPIC1, 100);
            final ProducerRecord<String, Integer> data2 = new ProducerRecord<>(TOPIC2, 200);
            try {
                RecordMetadata meta1 = producer.send(data1).get();
                LOG.info("key = {}, value = {} => partition = {}, offset= {}", data1.key(), data1.value(), meta1.partition(), meta1.offset());

                throwException();

                RecordMetadata meta2 = producer.send(data2).get();
                LOG.info("key = {}, value = {} => partition = {}, offset= {}", data2.key(), data2.value(), meta2.partition(), meta2.offset());


            } catch (InterruptedException | ExecutionException e) {
                LOG.error("Something goes wrong: {}", e.getMessage(), e);


            } finally {
                producer.flush();
            }
        }
    }
}
