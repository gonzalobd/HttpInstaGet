#! /bin/bash

cd /opt/kafka_$SCALA_VERSION-$KAFKA_VERSION
echo advertised.listeners=PLAINTEXT://$HOST:9092 >> config/server.properties
sed -i -e '16iexport KAFKA_HEAP_OPTS="-Xmx256M -Xms128M"\' bin/kafka-server-start.sh
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
bin/kafka-server-start.sh  -daemon config/server.properties
sleep 5
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic like \
--partitions 1 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic comment \
--partitions 1 --replication-factor 1
sleep 10
java -jar gonzalo-1.0-SNAPSHOT-selfcontained.jar $ID $TOKEN

#while true; do sleep 1000; done

