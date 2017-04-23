#! /bin/bash

cd /opt/kafka_$SCALA_VERSION-$KAFKA_VERSION
echo advertised.listeners=PLAINTEXT://$HOST:9092 >> config/server.properties
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
bin/kafka-server-start.sh  -daemon config/server.properties
sleep 5
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic like \
--partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic comment \
--partitions 1 --replication-factor 1
sleep 5
#java -jar gonzalo-1.0-SNAPSHOT-selfcontained.jar $IDTOKEN

while true; do sleep 1000; done

