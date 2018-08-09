# source environment
FROM scratch AS source

COPY ./ /src/

# build environment
FROM maven:3.5.4-jdk-8 AS builder

COPY --from=source /src/ /app/

WORKDIR /app

RUN set -x; mvn install -DskipTests=true

RUN mkdir /dist && mkdir /dist/plugins
RUN mv amy-master-node/target/amy-master-node-*-Snapshot.jar /dist/amy.jar
RUN mv plugins/*/target/*with-dependencies.jar /dist/plugins/

# production
FROM openjdk:8-jre

COPY --from=builder /dist/amy.jar /app/amy.jar
COPY --from=builder /dist/plugins /app/plugins

COPY --from=source /src/.docker/config /app/config

WORKDIR /app

EXPOSE 80

CMD ["java", "-jar", "amy.jar", "-c", "config"]
