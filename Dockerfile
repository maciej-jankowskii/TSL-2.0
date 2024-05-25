# obraz na ktorym bazujemy
FROM openjdk:17-jdk-slim
# co kopiujemy i gdzie
COPY target/TSL-2.0.jar ./TSL-2.0.jar
# port w kontenerze
EXPOSE 8080

CMD ["java", "-jar", "TSL-2.0.jar"]







