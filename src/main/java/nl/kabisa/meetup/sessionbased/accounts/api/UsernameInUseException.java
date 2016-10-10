package nl.kabisa.meetup.sessionbased.accounts.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameInUseException extends Exception {
    public UsernameInUseException(String username) {
        super("Username '" + username + "' is already used");
    }
}
