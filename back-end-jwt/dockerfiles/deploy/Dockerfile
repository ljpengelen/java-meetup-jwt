FROM maven:3-jdk-8-slim AS COMPILATION_STAGE

WORKDIR /app

COPY pom.xml .
RUN mvn package -Dmaven.test.skip --fail-never

COPY . .
RUN mvn package -Dmaven.test.skip

FROM openjdk:8-jre-slim AS EXECUTION_STAGE

RUN groupadd -g 999 appuser \
  && useradd -r -u 999 -g appuser appuser

RUN mkdir /app \
  && chown 999:999 /app

WORKDIR /app

EXPOSE 3002

COPY --from=COMPILATION_STAGE /app/target/*.jar app.jar

COPY dockerfiles/application.yml application.yml

USER appuser

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
