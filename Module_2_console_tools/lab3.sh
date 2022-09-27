#!/bin/bash

docker exec -ti kafka1 bash

cd /usr/bin/

# 1. Purge the topic. Set retention to a small value

kafka-configs \
  --alter \
  --bootstrap-server kafka1:19092 \
  --entity-name events2 \
  --entity-type topics \
  --add-config retention.ms=10

# 3. Check the topic

kafka-console-consumer \
  --bootstrap-server kafka1:19092 \
  --topic events2 \
  --property print.key=true \
  --property key.separator="," \
  --property print.partition=true \
  --from-beginning


# After sometime when you run this command the messages will not be shown

4. Set retention to normal value like retention.ms=604800000

