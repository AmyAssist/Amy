#!/bin/bash
docker run -d -p 1883:1883 -p 9001:9001 --name=broker eclipse-mosquitto
docker run -d -p 80:80 --name=system-test amy-master-node
sleep 10
curl -I localhost
docker stop system-test
docker stop broker
docker logs system-test | tee system-test.log | grep -i -E --color=auto "(Error|Exception)"
ERROR=$?
echo system-test.log:
cat system-test.log
if [ $ERROR -eq "0" ]; then
	exit 1;
else
	exit 0;
fi
