# Java Meetup - JWT-based Authentication
This application is meant as a demo for JWT-based authentication.
It requires Java 8, Maven, and Postgres to run locally.
To run the application in a Docker container, you need Java 8, Maven, and Docker.
For convenience, you might also want to use Docker Compose.

# Running the app locally
The be able to run the app locally, you need a running Postgres instance on port 5432 with a database named "java_meetup".
Once Postgres is running you can build the app with Maven and run the resulting jar or open it in your IDE and run it from there.

# Running the app in a Docker container
1. Build an image for the container for Postgres using the command at the top of its Docker file (https://github.com/ljpengelen/java-meetup-jwt/blob/master/docker/Dockerfile-postgres).
1. Build an image for the container for the app using the command at the top of its Docker file
(https://github.com/ljpengelen/java-meetup-jwt/blob/master/Dockerfile).
1. Follow the instructions in the repository for the front end.

# Running the app with Docker Compose
After building the required images, navigate to the `docker` folder and execute `docker-compose up -d`.
