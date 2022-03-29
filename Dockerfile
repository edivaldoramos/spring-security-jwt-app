FROM maven:3.8.4-jdk-11-slim AS mvnDps
LABEL maintainer="edivaldorsj@gmail.com"
WORKDIR /app
COPY pom.xml ./pom.xml
RUN mvn -e -B dependency:resolve

FROM mvnDps as mavenBuild
COPY src ./src
RUN mvn -e -B package

FROM openjdk:8-jre-alpine
COPY --from=mvnDps /app/target/*.jar /app/app.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]