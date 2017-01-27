FROM anapsix/alpine-java:8
RUN apk add bash

RUN mkdir /http-json-kafka
WORKDIR /http-json-kafka

COPY target/http-json-kafka-1.0.jar http-json-kafka.jar
COPY startup.sh .
EXPOSE 8080 8090
ENTRYPOINT [ "/http-json-kafka/startup.sh"]
