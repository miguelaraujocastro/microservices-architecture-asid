############################################################ 
# Dockerfile.cdc (usando JarLauncher do Spring Boot)
############################################################

FROM eventuateio/eventuate-tram-cdc-mysql-service:0.21.3.RELEASE

RUN mkdir -p /opt/drivers
COPY cdc-drivers/mysql-connector-java-8.0.21.jar /opt/drivers/mysql-connector-java.jar

ENTRYPOINT ["java","-cp","/opt/drivers/*:/eventuate-tram-cdc-mysql-service-0.21.3-SNAPSHOT.jar","org.springframework.boot.loader.JarLauncher"]