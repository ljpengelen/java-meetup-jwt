package nl.kabisa.meetup.jwtbased.sessions.api;

import static nl.kabisa.meetup.jwtbased.TokenNames.ACCESS_TOKEN_NAME;
import static nl.kabisa.meetup.jwtbased.TokenNames.REFRESH_TOKEN_NAME;

import java.time.Instant;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import nl.kabisa.meetup.jwtbased.accounts.repository.Account;
import nl.kabisa.meetup.jwtbased.accounts.repository.AccountRepository;

@RequestMapping("/session")
@RestController
public class SessionController {

    private StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();

    @Value("${jwt.secret_key}")
    private String encodedKey;

    @Value("${jwt.long_expiration_in_seconds}")
    private int expiration;

    @Autowired
    AccountRepository accountRepository;

    @RequestMapping(method = RequestMethod.POST)
    public LoginResponse login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        Account account = accountRepository.findByUsername(request.getUsername());
        if (account == null) {
            return new LoginResponse(LoginStatus.INVALID_CREDENTIALS);
        }

        if (!encryptor.checkPassword(request.getPassword(), account.getPassword())) {
            return new LoginResponse(LoginStatus.INVALID_CREDENTIALS);
        }

        String jwt = Jwts.builder()
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .setSubject(account.getId().toString())
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .compact();
        Cookie cookie = new Cookie(REFRESH_TOKEN_NAME, jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiration);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new LoginResponse(LoginStatus.LOGGED_IN);
    }

    @GetMapping
    public StatusResponse getStatus(@CookieValue(value = REFRESH_TOKEN_NAME, required = false) String jwt) {
        if (jwt == null) {
            return new StatusResponse(SessionStatus.LOGGED_OUT);
        }

        try {
            Jwts.parser().setSigningKey(encodedKey).parseClaimsJws(jwt);
        } catch (Exception e) {
            return new StatusResponse(SessionStatus.LOGGED_OUT);
        }

        return new StatusResponse(SessionStatus.LOGGED_IN);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseStatus(HttpStatus.NO_CONTENT) void logout(HttpServletResponse response) {
        deleteCookie(REFRESH_TOKEN_NAME, response);
        deleteCookie(ACCESS_TOKEN_NAME, response);
    }

    private void deleteCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
