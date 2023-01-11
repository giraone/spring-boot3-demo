FROM openjdk:17-jdk-alpine
EXPOSE 8080
COPY target/demo-sb3.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]