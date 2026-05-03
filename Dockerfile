FROM eclipse-temurin:25
COPY target/reconciliation-1.0-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
RUN sh -c 'touch reconciliation-1.0-SNAPSHOT.jar'
ENTRYPOINT ["java","-jar","reconciliation-1.0-SNAPSHOT.jar"]