#!/bin/bash
WAIT_TO_START=20
absPath=$(readlink -f "$0")
absDir=$(dirname "$absPath")
cd "${absDir}"
docker-compose up -d
sleep $WAIT_TO_START
curl -I -S --show-error localhost
curlCode=$?
docker-compose stop
docker-compose logs master-node
contCode=$(docker inspect -f '{{.State.ExitCode}}' master-node)
errorCount=$(docker-compose logs master-node | grep -i -E -c "(Error|Exception)")

if [ $curlCode -ne 0 ] ;then echo "Curl:$curlCode" ;fi
if [ $contCode -ne 143 ] ;then echo "Container:$contCode" ;fi
if [ $errorCount -ne 0 ] ;then echo "Error count:$errorCount" ;fi

#Don't include container code, because Amy get's killed by docker-compose stop, which causes a code != 0
if [ $curlCode -ne 0 ] || [ $contCode -ne 143 ] || [ $errorCount -ne 0 ] ;then exit 1 ;fi

