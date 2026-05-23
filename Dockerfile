FROM eclipse-temurin:25
COPY target/reconciliation-1.0-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/reconciliation-1.0-SNAPSHOT.jar"]