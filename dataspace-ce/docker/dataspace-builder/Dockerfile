FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN mkdir -p /app/files/resources/src-model \
    /app/files/resources/build-model && \
    apt-get update && apt-get install -y curl && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

COPY modules/model-release/target/model-release-DEV-SNAPSHOT-runnable.jar /app/model-builder.jar

ENTRYPOINT ["java", "-jar", "/app/model-builder.jar", \
           "model-directory=/app/files/resources/src-model", \
           "run-liquibase=true", \
           "target-directory=/app/files/resources/build-model"]
