#!/bin/bash
echo $*

bootstrapservers=$1

if [ "$bootstrapservers" == "" ] ; then
    echo "docker run ... -- <bootstrapservers> "
    exit 1
fi

cat <<EOF > ./config.yaml

producer:
  "bootstrap.servers": "$bootstrapservers"
  "key.serializer": "org.apache.kafka.common.serialization.ByteArraySerializer"
  "value.serializer": "org.apache.kafka.common.serialization.ByteArraySerializer"


server:
    adminConnectors:
        - type: http
          port: 8090

    applicationConnectors:
        - type: http
          port: 8080
EOF

java -jar http-json-kafka.jar server config.yaml
