#!/bin/bash

docker exec -ti kafka1 bash

cd /usr/bin/


# 1. Create topic
kafka-topics \
  --create \
  --bootstrap-server kafka1:19092 \
  --replication-factor 2 \
  --partitions 3 \
  --topic model-vo-18

# 2. Send data
kafka-console-producer \
  --bootstrap-server kafka1:19092 \
  --topic model-vo-18 \
  --property parse.key=true \
  --property key.separator=,

k1,v1
k2,v2
k3,v3
k4,v4
k5,v5

# Check that all records in different partitions
kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic model-vo-18 \
  --property print.key=true \
  --property key.separator="," \
  --property print.partition=true \
  --property print.timestamp=true \
  --property print.offset=true \
  --from-beginning



k1,v1_1
k1,v1_2
k1,v1_3

# Hash partitioning, the same key allows to keep order of messages
# Check that all keys are in the same partitions

kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic events2 \
  --property print.key=true \
  --property key.separator="," \
  --property print.partition=true \
  --property print.timestamp=true \
  --property print.offset=true \
  --from-beginning \
  --group cons1


kafka-console-producer \
  --bootstrap-server kafka1:19092 \
  --topic events4

v1_1
v1_2
v1_3
v1_4
v1_5
v1_6
v1_7
v1_8
v1_9

# Check that all messages are in different partitions (Round Robin strategy)

kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic keyed_orders_vo \
  --property print.key=true \
  --property key.separator="," \
  --property print.partition=true \
  --property print.timestamp=true \
  --property print.offset=true \
  --from-beginning \
  --group cons1


docker-compose logs
-- show the rebalancing process in logs
-- show joining to group coordinator, explain the hashing process of hashing consumer group name - __consumer_offsets-21



# 3. Read the data
kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic events4 \
  --property print.key=true \
  --property key.separator=","

# No data will be shown because we do not have --from-beginning property


# 4. Run with specifying consumer group and printing the partition
kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic events4 \
  --property print.key=true \
  --property key.separator="," \
  --property print.partition=true \
  --from-beginning \
  --group cons1

# Commit offset (auto or manual)
# Whether or not you need to commit the offset depends on the value you choose for the parameter enable.auto.commit.
# By default this is set to true, which means the consumer will automatically commit its offset regularly
# (how often is defined by auto.commit.interval.ms). If you set this to false, then you will need to commit the offsets yourself.
# This default behavior is probably also what is causing your "problem" where your consumer always starts with the latest message.
# Since the offset was auto-committed it will use that offset.


# Check that messages with the same key go to the same partition. Notice, that messages may come in a different order, when they are in different partitions.

# 5. Run in another terminal the same command
kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic events4 \
  --property print.key=true \
  --property key.separator="," \
  --property print.partition=true \
  --from-beginning \
  --group cons1

# Now we have two consumers within one group.

# 6. Send more data with our producer

k6,v6
k7,v7
k8,v8
k9,v9
k10,v10

# Check that messages come to different consumers



# th 1 members (kafka.coordinator.group.GroupCoordinator)
# kafka1    | [2022-08-23 11:36:29,677] INFO [GroupCoordinator 1]: Assignment received from leader consumer-cons3-1-bddde742-39d2-4cc1-805a-da6bbefa95e1 for group cons3 for generation 1. The group has 1 members, 0 of which are static. (kafka.coordinator.group.GroupCoordinator)
# kafka1    | [2022-08-23 11:37:39,759] INFO [Controller id=1] Processing automatic preferred replica leader election (kafka.controller.KafkaController)
# kafka1    | [2022-08-23 11:39:06,136] INFO [GroupCoordinator 1]: Dynamic member with unknown member id joins group cons3 in Stable state. Created a new member id consumer-cons3-1-e76f6acd-afb1-46c9-b05e-f139daa424c4 and request the member to rejoin with this id. (kafka.coordinator.group.GroupCoordinator)
# kafka1    | [2022-08-23 11:39:06,139] INFO [GroupCoordinator 1]: Preparing to rebalance group cons3 in state PreparingRebalance with old generation 1 (__consumer_offsets-6) (reason: Adding new member consumer-cons3-1-e76f6acd-afb1-46c9-b05e-f139daa424c4 with group instance id None) (kafka.coordinator.group.GroupCoordinator)
# kafka1    | [2022-08-23 11:39:08,708] INFO [GroupCoordinator 1]: Stabilized group cons3 generation 2 (__consumer_offsets-6) with 2 members (kafka.coordinator.group.GroupCoordinator)
# kafka1    | [2022-08-23 11:39:08,711] INFO [GroupCoordinator 1]: Assignment received from leader consumer-cons3-1-bddde742-39d2-4cc1-805a-da6bbefa95e1 for group cons3 for generation 2. The group has 2 members, 0 of which are static. (kafka.coordinator.group.GroupCoordinator)
