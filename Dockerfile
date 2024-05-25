FROM openjdk:17-jdk-slim

COPY target/TSL-2.0.jar ./TSL-2.0.jar

EXPOSE 8080

CMD ["java", "-jar", "TSL-2.0.jar"]







