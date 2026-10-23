FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/custom-reverse-proxy-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
