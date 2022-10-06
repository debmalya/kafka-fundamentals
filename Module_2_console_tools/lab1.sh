#!/bin/bash

docker exec -ti kafka1 bash

cd /usr/bin/


# 1. Create topic
kafka-topics \
  --create \
  --bootstrap-server kafka1:19092 \
  --replication-factor 2 \
  --partitions 4 \
  --topic test_vo

# 2. Describe topic
kafka-topics \
  --bootstrap-server kafka1:19092 \
  --describe \
  --topic unordered_vo

# 2 List all topics
kafka-topics \
  --list \
  --bootstrap-server kafka1:19092


# 3. Send data
kafka-console-producer \
  --bootstrap-server kafka1:19092 \
  --topic message_model_vo


# 4. Read the data online and show order and from beginning, only new and specific offset
kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic delivery_test_vo \
  --from-beginning \
  --group cons1


kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic delivery_test_vo \
  --group cons2

kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic delivery_test_vo \
  --partition 0 \
  --offset 1 \
  --max-messages 1


# 5.  Check data in local file system of data

# 5.1. show the configs
    docker exec -ti kafka1 bash

# 5.2. see the logs
    cat /etc/kafka/server.properties

    log.dirs=/var/lib/kafka - folder with a logs

    default num partitions - num.partitions=1

# 5.3 see the file structure
    cd /var/lib/kafka/data
    ls -la

#    folder with partition and replica without events1-0
#        events1-1
#        events1-2
#        events1-3
#
#     __consumer_offsets-13 ~ topic with 50 partitions for storing offsets of group consumers
#
#    ls -la events1-1
#
#        10485760 Aug  1 11:16 00000000000000000000.index
#             406 Jul 27 15:25 00000000000000000000.log  ~ !!!!!! file with data
#        10485756 Aug  1 11:16 00000000000000000000.timeindex
#              10 Jul 28 10:40 00000000000000000006.snapshot
#               8 Aug  1 11:16 leader-epoch-checkpoint
#              43 Jul 27 13:57 partition.metadata

    cat 00000000000000000000.log
#        F☻��ŝ☺☺�@<Ϭ☺�@<����������������☻↑☺♀asdasd ~ !!!!!! this is or message in order
#        ♫�     ☻☺☻K☻�O��☺�@>7�☺�@>7���������������☺2☺&;lask;dlkas;kd;askd ~ !!!!!! this is or message in order
#        ♥G☻��nN☺�@>�#☺�@>�#��������������☺*☺▲asdasdasdasdasd
#        ♦A☻��}�☺�@@t‼☺�@@t‼��������������☺▲☺↕sadasdasd
#        ♣A☻Я��☺�@B�g☺�@B�g��������������☺▲☺↕sadasdasd

    cat ./events2-0/leader-epoch-checkpoint - change epoch of leader
#        0
#        0


# 5. Describe system topic


#__consumer_offsets  - is used to store information about committed offsets for each topic:partition per group of consumers (groupID).
#                      It is compacted topic, so data will be periodically compressed and only latest offsets information available.
#_schema             - is not a default kafka topic (at least at kafka 8,9). It is added by Confluent. See more: Confluent Schema Registry - github.com/confluentinc/schema-registry


kafka-topics \
  --bootstrap-server kafka1:19092 \
  --describe \
  --topic __consumer_offsets



kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic __consumer_offsets \
  --from-beginning



6. Found problems

docker-compose kill kafka2

# partitions without leaders
kafka-topics \
  --bootstrap-server kafka1:19092 \
  --describe \
  --unavailable-partitions

# partitions without followers
kafka-topics \
  --bootstrap-server kafka1:19092 \
  --describe \
  --under-replicated-partitions

# partitions without lagging
kafka-topics \
  --bootstrap-server kafka1:19092 \
  --describe \
  --under-min-isr-partitions

docker-compose restart kafka2


# ISR means when you produce data with ack=all it look in Kafka if there is atleast min ISR number of replicas are synced
# with the leader including leader. If not, it returns an error and not produced.

