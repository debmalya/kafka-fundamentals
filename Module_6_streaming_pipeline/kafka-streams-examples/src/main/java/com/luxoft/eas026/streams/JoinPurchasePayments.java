package com.luxoft.eas026.streams;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

public class JoinPurchasePayments {

	public static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";

	public static void main(final String[] args) throws Exception {
		final Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "join-purchases-example");
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "join-purchases-example-client");
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
		streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/streams/");
		streamsConfiguration.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);

		final boolean isKeySerde = false;
		Map<String, String> map = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
				SCHEMA_REGISTRY_URL);
		final Serde<Purchase> purchaseAvroSerde = new SpecificAvroSerde<>();
		purchaseAvroSerde.configure(map, isKeySerde);
		final Serde<Payment> paymentAvroSerde = new SpecificAvroSerde<>();
		paymentAvroSerde.configure(map, isKeySerde);


		final StreamsBuilder builder = new StreamsBuilder();

		final KStream<String, Purchase> purchases = builder.stream("Purchases");

		final KStream<String, Payment> payments = builder.stream("Payments");
		KStream<String, Payment> keyedPayments =
				payments.selectKey((k, v) -> String.valueOf(v.getPurchaseId()));


		/**
		*
		 * purchases
		 *
		 *
		 *  	Key				Value
		 *  	0				Id Product         Amount Sum  CustomerId
		 * 		2 				2   "kettle"   		 1      10    2342
		 * 		4 				4   "kettle"   		 1      10    123
		 *		6 				6   "hair dryer"    1      10    987
		 *		8 				8   "hair dryer"    1      10    234
		 *		10				10	"hair dryer"    1      10    3245
		 *
		 *	keyedPayments
		 *
		 *  	Key				Value
		 *  	1 				Id  purchaseId
		 * 		2 				1    234
		 * 		3 				2    2342
		 * 		4 				3    123
		 * 		5 				4    123
		 *		6 				5   876
		 *		7 				6   987
		 *		8 				7   324
		 *		9 				8   234
		 *		10				9   234
		 *
		 * 		joined
		 * 		 *  	Key				Value PayedPurchase
		 * 		 *  	0				Id   Purchase
		 * 		 * 		2 				2    2342
		 * 		 * 		4 				4    123
		 * 		 *		6 				6    987
		 * 		 *		8 				8    234
		 * 		 *		10				10	 3245
		 *
		 *
		* */



		purchases
				.join(keyedPayments,
					(purchase, payment) -> PayedPurchase.newBuilder()
												.setPaymentId(payment.getId())
												.setPurchaseId(payment.getPurchaseId())
												.setProduct(purchase.getProduct())
												.build(), /* ValueJoiner */
					JoinWindows.of(Duration.ofHours(1)), /* for waiting records from right left stream -  */
					StreamJoined.with(Serdes.String(), /* key */ purchaseAvroSerde, /* left */ paymentAvroSerde) /* right */
			        )
				.to("PayedPurchases");

		final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
		streams.cleanUp();
		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
