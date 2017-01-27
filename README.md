# json over http to kafka

This test application allows us to dump json serialized data into a Kafka queue. Each POST requests that contains the
data must have a query parameter in the url that describes which field in the json has the Kafka key that is used for
partitioning.

This project creates an Apache Kafka HTTP endpoint for consuming json messages and putting them into a kafka queue
It is built on [the Dropwizard framework](http://dropwizard.github.io/dropwizard/).

Bart van Deenen

License: Public Domain.

## build

    mvn package

This builds a fat jar in `target`.

## run

    java -jar target/http-json-kafka-1.0.jar server kafka-http.yml

or
    ./run.sh

Make sure you have a kafka server running, on the host configured in `kafka-http.yml`.

## Configuration

The configuration is in a yaml file, and specifies where the Kafka system can be reached and what ports are being used
for the web app. Dropwizard has an extensive metrics interface, which can be reached on the admin connector port.

Example `kafka-http.yml`

    producer:
      "bootstrap.servers": "127.0.0.1:9092"
      "key.serializer": "org.apache.kafka.common.serialization.ByteArraySerializer"
      "value.serializer": "org.apache.kafka.common.serialization.ByteArraySerializer"

    server:
        adminConnectors:
            - type: http
              port: 10010

        applicationConnectors:
            - type: http
              port: 8123

## Listen on the kafka queue

    $KAFKA/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic <topic>

## Partitioning
The Kafka partition that is being used for the data, is via the 
[default Kafka partition](http://blog.rocana.com/kafkas-defaultpartitioner-and-byte-arrays) 
function on the field defined with the query parameter `keyfield`.

## Send a POST http request

I'm sending a request to the topic `k3`. 

```
curl -v -X POST -H 'Content-Type: application/json' \
    -d '{"visitorId":123, "payload": [1,2,3] }' \
    http://localhost:8123/ad-hoc/k3?keyfield=visitorId

> POST /ad-hoc/k3?keyfield=visitorId HTTP/1.1
> Host: localhost:8123
> Accept: */*
> Content-Type: application/json
> Content-Length: 38
>
* upload completely sent off: 38 out of 38 bytes
< HTTP/1.1 200 OK
```

Observe the event in the `kafka-console-consumer`

```
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic k3
{"visitorId":123, "payload": [1,2,3] }
```

Wrong commands will generate http error responses:

```
curl -v -H 'Content-Type: application/json' \
    -d '{"wrongKeyField":123, "payload": [1,2,3] }' \
    http://localhost:8123/ad-hoc/k3?keyfield=visitorId

> POST /ad-hoc/k3?keyfield=visitorId HTTP/1.1
>
* upload completely sent off: 42 out of 42 bytes
< HTTP/1.1 400 Bad Request
Input has no field visitorId


curl -v -H 'Content-Type: application/json' \
    -d 'not json' http://localhost:8123/ad-hoc/k3?keyfield=visitorId

< HTTP/1.1 400 Bad Request
input is not json
```

# Docker



	sudo docker build -t http-json-kafka:1 .

	sudo docker run -P -d http-json-kafka:1 <broker-list>


