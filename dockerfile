FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY ./target/*.jar ./app.jar
COPY ./JARs/* ./JARs/

ENTRYPOINT ["java", "-classpath", "app/JARs/*", "-jar", "app.jar", "rabbitmq", "guest", "guest"]
#RUN java -classpath app/JARs/* -jar app.jar 0.0.0.0