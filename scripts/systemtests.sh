#!/bin/bash
docker run -d -p 1883:1883 -p 9001:9001 --name=broker eclipse-mosquitto
docker run -d -p 80:80 -e "AMY_MQTT_CONFIG_BROKERADDRESS=tcp://broker" --name=system-test amy-master-node
sleep 10
curl -I localhost
docker stop system-test
docker stop broker
echo system test log:
docker logs system-test
docker logs system-test | grep -i -E "(Error|Exception)"
ERROR=$?
if [ $ERROR -eq "0" ]; then
	exit 1;
else
	exit 0;
fi
