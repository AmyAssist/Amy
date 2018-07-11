FROM openjdk:8-jre

COPY core/target/amy-core-*-Snapshot.jar /app/amy.jar
COPY plugins/*/target/*with-dependencies.jar /app/plugins/
COPY config /app/config
COPY .docker/config /app/config
COPY resources /app/resources

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "amy.jar", "-c", "/config"]
