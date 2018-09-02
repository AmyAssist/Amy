#!/bin/bash
absPath=$(readlink -f "$0")
absDir=$(dirname "$absPath")
cd "${absDir}"
docker-compose up -d
stop=false
remainingTries=20
while [ "$stop" == "false" ] ;do
  sleep 1
  curl -I -S -f --show-error localhost
  curlCode=$?
  if [ $curlCode -eq 0 ] || [ $remainingTries -eq 0 ] ;then stop=true ;fi
  let "remainingTries=$remainingTries - 1"
done
docker-compose stop
docker-compose logs master-node
contCode=$(docker inspect -f '{{.State.ExitCode}}' master-node)
errorCount=$(docker-compose logs master-node | grep -i -E -c "(Error|Exception)")

if [ $curlCode -ne 0 ] ;then echo "Curl:$curlCode" ;fi
if [ $contCode -ne 0 ] ;then echo "Container:$contCode" ;fi
if [ $errorCount -ne 0 ] ;then echo "Error count:$errorCount" ;fi

#Don't include container code, because Amy get's killed by docker-compose stop, which causes a code != 0
if [ $curlCode -ne 0 ] || [ $errorCount -ne 0 ] ;then exit 1 ;fi

