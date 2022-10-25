package com.luxoft.eas026.module3.simple;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

public class OnlyLastMessagesConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleConsumer.class);

    private static final String BOOTSTRAP_SERVERS = ":9092";
    private static final String CONSUMER_GROUP = "first";
    private static final String OFFSET_RESET = "earliest";
//	private static final String OFFSET_RESET = "latest"; // default


    @SuppressWarnings("boxing")
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP); // !!!!! fixed
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_RESET); // !!!!!
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 15);


        try (KafkaConsumer<String, Integer> consumer = new KafkaConsumer<>(props)) {
            String topic = args[0];
            List<PartitionInfo> partitions = consumer.partitionsFor(topic);
            ArrayList<TopicPartition> topicPartitions = new ArrayList<>();

            topicPartitions.add(new TopicPartition(topic, Integer.parseInt(args[1])));

//            for (PartitionInfo part: partitions) {
//                topicPartitions.add(new TopicPartition(topic, part.partition()));
//            }

            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);

            consumer.assign(topicPartitions);

            for (Map.Entry<TopicPartition, Long> entry : endOffsets.entrySet()) {
                long newOffsetPosition = entry.getValue() - 5;
                consumer.seek(entry.getKey(), newOffsetPosition);
            }

            while (true) {
                ConsumerRecords<String, Integer> records = consumer.poll(Duration.ofSeconds(2)); // read from kafka

                // commit offset => 3

                for (ConsumerRecord<String, Integer> data : records) { // write messages to the console ~ processing
                    LOG.info("key = {}, value = {} => partition = {}, offset= {}", data.key(), data.value(), data.partition(), data.offset());
                }



            }

            // max time
        } catch (Exception e) {
            LOG.error("Something goes wrong: {}", e.getMessage(), e);
        }
    }


}
