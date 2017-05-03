# sudo docker build -t httpinstaget .
# sudo docker run -d -p 9092:9092 -e HOST=<IPpublicaNodo> -e ID=<id> -e TOKEN=<token>   httpinstaget
# sudo docker exec -ti <id> bash

FROM java:openjdk-8-jdk

ENV DEBIAN_FRONTEND noninteractive
ENV SCALA_VERSION 2.11
ENV KAFKA_VERSION 0.10.1.0
ENV KAFKA_HOME /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"


# Install Kafka, Zookeeper and other needed things
RUN apt-get update && \
    apt-get install -y zookeeper wget supervisor dnsutils git maven && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean && \
    wget -q http://apache.mirrors.spacedump.net/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -O /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz && \
    tar xfz /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -C /opt && \
    rm /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz && \
    cd / && \
    git clone https://github.com/gonzalobd/httpInstaGet && \
    cd httpInstaGet && \
    mvn clean package


COPY entry.sh /

RUN chmod +x /entry.sh

ENTRYPOINT ["/entry.sh"]
