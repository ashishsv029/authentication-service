# This is a Multistage docker build i.e artifacts are generated in one stage and copied in the final stage, so that other tools needed to build the artifact will not be present in final build, reducing the total size of final image
# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
VOLUME /tmp
EXPOSE 3200
# The app.jar is the name of the JAR file inside the Docker container, which is used when running the application. So can be renamed as well
COPY --from=build /app/target/authentication-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
