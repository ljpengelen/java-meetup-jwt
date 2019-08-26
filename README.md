# Java meetup - JWT

Presentation and demo applications for a Java meetup about JWTs.
To run these applications, you need Docker and Docker Compose.

## Session-based authentication

Execute `docker-compose -f docker-compose-session.yaml build` to build the images for the session-based version of the application and `docker-compose -f docker-compose-session.yaml up` to run it.
Once the app is running, it is accessible via http://localhost:8000.

## JWT-based authentication

Execute `docker-compose -f docker-compose-jwt.yaml build` to build the images for the session-based version of the application and `docker-compose -f docker-compose-jwt.yaml up` to run it.
Once the app is running, it is accessible via http://localhost:8000.

Due to the use of the `__Host-` prefix for a particular cookie, this app doesn't work over HTTP in Chrome and Firefox.
The details are described in this blog post: https://www.theguild.nl/where-to-put-json-web-tokens-in-2019/.
The app works fine in Safari, however, and probably works in Edge and Internet Explorer as well.
If you want to see the app in action in Chrome and Firefox too, you'll have to use something like [mkcert](https://github.com/FiloSottile/mkcert) to set up HTTPS locally.
