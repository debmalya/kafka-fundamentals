package com.luxoft.eas026.module3.simple;

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

public class GeneratorProducer {
	private static final Logger LOG = LoggerFactory.getLogger(GeneratorProducer.class);

	private static final String BOOTSTRAP_SERVERS = ":9092";
	private static final String CLIENT_ID = "ex";

	private static Producer<String, Integer> producer;

	public static void throwException() throws InterruptedException {
		throw new InterruptedException();
	}


	public static void main(String[] args) {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());

		producer = new KafkaProducer<>(props);

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(() -> {
			try {
				send(args[0]);
			} catch (InterruptedException e) {
				ProducerRecord<String, Integer> data1 = new ProducerRecord<>("error_topic", "key_error", 2);
				try {

					RecordMetadata meta = producer.send(data1).get();
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				} catch (ExecutionException ex) {
					throw new RuntimeException(ex);
				}

			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			} finally {
				producer.flush();
			}
		}, 0, 3, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(producer::close));
	}

	@SuppressWarnings({ "boxing", "unused" })
	public static void send(String topic) throws InterruptedException, ExecutionException {
		final int number = new Random().nextInt(3);

		if (number == 2 ) {
			throwException();
		}
		ProducerRecord<String, Integer> data = new ProducerRecord<>(topic, "key" + number, number);

//		ProducerRecord<String, Integer> data = new ProducerRecord<>(topic, "key_string_1_part", number);
		try {
			RecordMetadata meta = producer.send(data).get();
			LOG.info("key = {}, value = {} => partition = {}, offset= {}", data.key(), data.value(), meta.partition(), meta.offset());
		} catch (InterruptedException | ExecutionException e) {
		} finally {
			producer.flush();
		}

	}

}
