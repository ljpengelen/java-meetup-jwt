# Java Meetup - Front end

This application is a front end for the [session-based back end](../back-end-session) and the [JWT-based back end](../back-end-jwt).
To run the application locally, you need nginx.
To run the application in a Docker container, you need Docker.

# Running the front end locally

Execute the command `./serve` to start nginx, and visit http://localhost:8000 to view the app.

# Building the Docker images for the front end

1. Follow the instructions in the repository for the session-based back end (https://github.com/ljpengelen/java-meetup-session) and the JWT-based back end (https://github.com/ljpengelen/java-meetup-jwt) to build images for the containers for the back ends.
1. Build images for nginx by executing the commands at the top of https://github.com/ljpengelen/java-meetup-front-end/blob/master/Dockerfile-jwt and https://github.com/ljpengelen/java-meetup-front-end/blob/master/Dockerfile-session.

The nginx configuration files differ slightly for each type of back end.
The configuration file for the session-based app depends on IP hashing to ensure that each request from a certain IP always ends up at the same back end.
The JWT-based app doesn't have this requirement.

# Running the app using Docker Compose

Follow the instructions in the repositories for each back end to run the app.
