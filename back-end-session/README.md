# Java Meetup - Session-based Authentication

This back-end application is meant as a demo for session-based authentication.
It requires Java 8 and Maven to run locally.

It also requires PostgreSQL to be accessible via port `5432` with databases named `java_meetup` and `java_meetup_test`.
The user `postgres` should have access to these database.
You can build a Docker image for such an instance of PostgreSQL by navigating to the folder `database` and executing `docker build -t java-meetup-postgres .`.
Start a container be executing `docker run -p 5432:5432 java-meetup-postgres`.

## Running tests

Execute `mvn verify` to run the tests and create a JAR.

## Running the app

Execute `mvn package -Dmaven.test.skip` to build a JAR.
Run the app by executing `java -jar target/<NAME_OF_JAR>.jar`.
