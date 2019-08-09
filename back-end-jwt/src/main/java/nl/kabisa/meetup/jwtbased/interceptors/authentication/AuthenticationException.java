package nl.kabisa.meetup.jwtbased.interceptors.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends Exception {
    public AuthenticationException() {
        super("Request denied due to unauthorized access");
    }
}
