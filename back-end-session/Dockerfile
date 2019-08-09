# Execute the following command to build an image:
# docker build -t java-meetup-back-end-session .

FROM frolvlad/alpine-oraclejdk8:slim
ADD target/sessionbased-0.0.1-SNAPSHOT.jar app.jar
ADD docker/application.yml application.yml
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
