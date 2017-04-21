#! /bin/bash

cd /opt/kafka_$SCALA_VERSION-$KAFKA_VERSION
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
bin/kafka-server-start.sh -daemon config/server.properties

while true; do sleep 1000; done

