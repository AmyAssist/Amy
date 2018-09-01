#!/bin/bash
docker-compose up -d
sleep 5
curl -I localhost
docker-compose stop

echo system test log:
docker-compose logs master-node
ERRORS=$(docker-compose logs master-node | grep -i -E -c "(Error|Exception)")
exit $ERRORS