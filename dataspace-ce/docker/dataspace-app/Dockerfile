FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN mkdir -p /app/files/resources/build-model

COPY modules/dataspace-app/target/dataspace-app-DEV-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar", \
           "--dataspace.app.pathConfigDirectory=files/resources/build-model/", \
           "--dataspace.app.singleMode=true", \
           "--dataspace.app.pdmZipped=false"]
