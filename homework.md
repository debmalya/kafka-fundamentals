1. Module 1 - system design of kafka
   1. Homework: Repeat planing your own distributed que in pairs: write, read, keep data in parallel mode. And try to answer questions:
      1. What's the format and average size of messages?
      2. Can messages be repeatedly consumed?
      3. Are messages consumed in the same order they were produced? 
      4. Does data need to be persisted? 
      5. What is the data retention? 
      6. How many producers and consumers are we going to support?
   2. Theory
2. Module 2: kafka-topics, console-consumer, console-producer
    1. Homework: 
       1. Using internal kafka-topics, console-consumer, console-producer
       2. Create topic with 3 partitions & RF = 2
       3. Send message, check the ISR
       4. Organize message writing/reading with order messages keeping
       5. Organize message writing/reading without order messages keeping and hash partitioning
       6. Organize message writing/reading without skew data
       7. Read messages from the start, end and offset
       8. Read topic with 2 partitions with 2 consumers in one consumer group and different consumer group
       9. Choose optimal number of consumers for reading topic with 4 partitions
       10. Write messages with min latency
       11. Write messages with max compression max throughput
    2. Theory

3. Module 3: Java, Scala, Python API + others languages (via Confluent Rest Services) 
   1. Homework:
      1. You need to write an application that will do the following: 
         1. Generate messages and produce messages to the source topic
         2. Read messages 3 last records from the source topic
         3. Add one more consumer to consumer group, find the assignment consumers to the partitions in the log
         4. Add writing to another topic
         5. Add transaction
      2. You need to write an application that will do the following (topic part number = 3):
         1. Producer: Read data from a CSV file, which can be downloaded from the link - https://www.kaggle.com/sootersaalu/amazon-top-50-bestselling-books-2009-2019, serialize them into JSON, and write to the books topic of the locally deployed Apache Kafka service.
         2. Consumer: Read the data from the books topic and print the last 5 records (with the maximum offset value) from each partition to stdout to the console. When reading a topic, only 15 records can be stored in memory at the same time.
    2. Theory:
4. Module 4: AVRO + Schema Registry
   1. Homework:
      1. Add avro schema
      2. compile java class 
      3. build avro consumer and producer  with a specific record 
      4. add schema registry
      5. add error topic with error topic and schema registry
      6. build avro consumer and producer  with a generic record
5. Module 5: SpringBoot + SpringCloud
   1. Homework
      1. Write template for Spring App
      2. Add Kafka Template with producer
      3. Add Kafka Template with consumer
      4. Add rest controller
      5. Modify spring boot to work in async (parallel) mode

6. Module 6: Streaming Pipelines (Kafka Streams + KSQL + Kafka Connect vs Akka Streams vs Spark Streaming vs Flink)
    1. Homework:
       1. Choose the way how read data from Kafka topic with 50 partitions
       2. Try to use checkpoint mechanism 
       3. Start the five executors and kill some of them
       4. Check the backpressure
    2. 

7. Module 7: Kafka Monitoring
   1. Homework:
      1. Build several metrics in Grafana