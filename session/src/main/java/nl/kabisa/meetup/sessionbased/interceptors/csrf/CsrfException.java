package nl.kabisa.meetup.sessionbased.interceptors.csrf;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CsrfException extends Exception {
    public CsrfException() {
        super("Request denied due to possible cross-site request forgery");
    }
}
