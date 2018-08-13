#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker images
docker tag amy-master-node amyassist/amy:dev
docker push amyassist/amy:dev
