FROM maven:3.6-jdk-8-alpine AS builder
LABEL maintainer="edivaldorsj@gmail.com"
WORKDIR /app
COPY ../pom.xml .
RUN mvn -e -B dependency:resolve
COPY ../src ./src
RUN mvn -e -B package

FROM openjdk:8-jre-alpine
COPY --from=builder /app/target/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]