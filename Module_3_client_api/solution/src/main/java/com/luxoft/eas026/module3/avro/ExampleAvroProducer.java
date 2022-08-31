package com.luxoft.eas026.module3.avro;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//import org.apache.avro.Schema;
//import org.apache.avro.generic.GenericData;
//import org.apache.avro.generic.GenericRecord;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.serialization.StringSerializer;
//import io.confluent.kafka.serializers.KafkaAvroSerializer;
//import java.util.Properties;



public class ExampleAvroProducer {

//    private static final Logger LOG = LoggerFactory.getLogger(ExampleProducer.class);
//
//    private static final String BOOTSTRAP_SERVERS = ":9092";
//    private static final String CLIENT_ID = "ex";
//
//    private static Producer<String, Integer> producer;
//
//    public static void main(String[] args) {
//
//        System.out.println("Test topic" + args[0]);
//
//
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
//
//        producer = new KafkaProducer<>(props);
//
//        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//        executor.scheduleAtFixedRate(() -> send(args[0]), 0, 3, TimeUnit.SECONDS);
//
//        Runtime.getRuntime().addShutdownHook(new Thread(producer::close));
//    }
//
//    @SuppressWarnings({ "boxing", "unused" })
//    public static void send(String topic) {
//        final int number = new Random().nextInt(10);
//        ProducerRecord<String, Integer> data = new ProducerRecord<>(topic, "key" + number, number);
//        try {
//            RecordMetadata meta = producer.send(data).get();
//            LOG.info("key = {}, value = {} => partition = {}, offset= {}", data.key(), data.value(), meta.partition(), meta.offset());
//        } catch (InterruptedException | ExecutionException e) {
//            producer.flush();
//        }
//
//    }


}
