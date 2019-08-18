FROM maven:3-jdk-8-slim

WORKDIR /app

COPY back-end-jwt/pom.xml .

RUN mvn verify --fail-never
