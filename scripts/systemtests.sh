#!/bin/bash
absPath=$(readlink -f "$0")
absDir=$(dirname "$absPath")
cd "${absDir}"

docker-compose up -d
sleep 5
curl -I localhost || exit 1
docker-compose stop
docker-compose logs master-node
ERRORS=$(docker-compose logs master-node | grep -i -E -c "(Error|Exception)")
exit $ERRORS