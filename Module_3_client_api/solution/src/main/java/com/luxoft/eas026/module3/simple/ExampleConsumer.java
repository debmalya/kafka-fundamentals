package com.luxoft.eas026.module3.simple;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(ExampleConsumer.class);

	private static final String BOOTSTRAP_SERVERS = ":9092";
	private static final String CONSUMER_GROUP = "first";
	private static final String OFFSET_RESET = "earliest";


	@SuppressWarnings("boxing")
	public static void main(String[] args) {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_RESET);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());

//		Kafka 50 brokers - 50 nodes - 20 000 msg/s
//		part 1    => client node 1 - 64GB 10 consumers
//		part 2	  => client node 2 - 64GB 10 consumers
//		....
//		part N	  => client node 5
//		1 node make 50 consumers -> needs 50 cores
//		 64GB * 5 + 12 Cores * 5 + 5 network interface
//		hard drive of this node
//
//		part 2    => client node 1 - 64GB 10 consumers
//		part 1	  => client node 2 - 64GB 10 consumers
//		....
//		part N	  => client node 5
//		1 node make 50 consumers -> needs 50 cores
//		 64GB * 5 + 12 Cores * 5 + 5 network interface
//		hard drive of this node



		try (KafkaConsumer<String, Integer> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Collections.singleton(args[0]));
			while (true) {
				ConsumerRecords<String, Integer> records = consumer.poll(Duration.ofSeconds(2));
				for (ConsumerRecord<String, Integer> data : records) {
					LOG.info("key = {}, value = {} => partition = {}, offset= {}", data.key(), data.value(), data.partition(), data.offset());
				}
			}
		} catch (Exception e) {
			LOG.error("Something goes wrong: {}", e.getMessage(), e);
		}
	}
}
