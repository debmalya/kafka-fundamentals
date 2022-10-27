package com.luxoft.eas026.module3.avro;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class AvroSchemaRegistryProducer {

    public static void main(String[] args) throws IOException, CsvValidationException {
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse("{\n" +
                "     \"type\": \"record\",\n" +
                "     \"namespace\": \"com.example\",\n" +
                "     \"name\": \"BookReview\",\n" +
                "     \"fields\": [\n" +
                "       { \"name\": \"name\", \"type\": \"string\", \"doc\": \"Name of the book\" },\n" +
                "       { \"name\": \"author\", \"type\": \"string\", \"doc\": \"Author of the book\" },\n" +
                "       { \"name\": \"userRating\", \"type\": \"float\", \"doc\": \"Total user rating\" },\n" +
                "       { \"name\": \"year\", \"type\": \"int\", \"doc\": \"Year of publishing\" },\n" +
                "       { \"name\": \"reviews\", \"type\": \"string\", \"doc\": \"Reviews\" },\n" +
                "       { \"name\": \"price\", \"type\": \"float\", \"doc\": \"Price of the book\" },\n" +
                "       { \"name\": \"genre\", \"type\": \"string\", \"doc\": \"Genre of the book\" }\n" +
                "     ]\n" +
                "}");


        Properties properties = new Properties();
        // normal producer
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
        properties.setProperty("acks", "all");
        properties.setProperty("retries", "10");
        // avro part
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
//        properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");
        properties.setProperty("schema.registry.url", "http://127.0.0.1:8085");


        Producer<String, GenericData.Record> producer = new KafkaProducer<>(properties);
        String topic = "bookreviews-avro-generic";


        FileReader filereader = null;
        try {
            filereader = new FileReader("./src/main/resources/bestsellers with categories.csv");
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            boolean isFirstRow = true;

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                GenericRecordBuilder customerBuilder = new GenericRecordBuilder(schema);
                if (isFirstRow) {
                    isFirstRow = false;
                } else {
                    for (int i = 0; i < nextRecord.length; i++) {
                        System.out.print(nextRecord[i] + ",");
                        switch (i) {
                            case 0:
                                customerBuilder.set("name", nextRecord[i]);
                                break;
                            case 1:
                                customerBuilder.set("author", nextRecord[i]);
                                break;
                            case 2:
                                customerBuilder.set("userRating", Float.valueOf(nextRecord[i]));
                                break;
                            case 3:
                                customerBuilder.set("reviews", nextRecord[i]);
                                break;
                            case 4:
                                customerBuilder.set("price", Float.valueOf(nextRecord[i]));

                                break;
                            case 5:
                                customerBuilder.set("year", Integer.valueOf(nextRecord[i]));
                                break;
                            case 6:
                                customerBuilder.set("genre", nextRecord[i]);
                                break;
                            default:
                                break;
                        }


                    }
                    GenericData.Record record = customerBuilder.build();
                    ProducerRecord<String, GenericData.Record> recordData = new ProducerRecord<>(topic, String.valueOf(System.currentTimeMillis()), record);
                    producer.send(recordData);
                }

            }
            System.out.println("All CSV records are published");
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (filereader != null) {
                filereader.close();
            }
            producer.flush();
            producer.close();
        }


    }
}
