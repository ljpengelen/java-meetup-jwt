# Java meetup - JWT

Presentation and demo applications for a Java meetup about JWTs.
To run these applications, you need Docker and Docker Compose.

## Session-based authentication

Execute `docker-compose -f docker-compose-session.yaml build` to build the images for the session-based version of the application and `docker-compose -f docker-compose-session.yaml up` to run it.
Once the app is running, it is accessible via http://localhost:8000.

## JWT-based authentication

Execute `docker-compose -f docker-compose-jwt.yaml build` to build the images for the session-based version of the application and `docker-compose -f docker-compose-jwt.yaml up` to run it.
Once the app is running, it is accessible via http://localhost:8000.
