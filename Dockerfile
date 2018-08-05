# source environment
FROM scratch AS source

COPY ./ /src/

# build environment
FROM maven:3.5.4-jdk-8 AS builder

COPY --from=source /src/ /app/

WORKDIR /app

RUN set -x; mvn install -DskipTests=true

RUN mkdir /dist && mkdir /dist/plugins
RUN mv core/target/amy-core-*-Snapshot.jar /dist/amy.jar
RUN mv plugins/*/target/*with-dependencies.jar /dist/plugins/

# production
FROM openjdk:8-jre

COPY --from=builder /dist/amy.jar /app/amy.jar
COPY --from=builder /dist/plugins /app/plugins

COPY --from=source /src/config /app/config
#COPY --from=source /src/.docker/config /app/config

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "amy.jar", "-c", "/config"]
