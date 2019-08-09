package nl.kabisa.meetup.sessionbased.interceptors.authentication;

import static io.jsonwebtoken.Jwts.parser;
import static nl.kabisa.meetup.sessionbased.TokenNames.ACCESS_TOKEN_NAME;
import static nl.kabisa.meetup.sessionbased.TokenNames.REFRESH_TOKEN_NAME;

import java.time.Instant;
import java.util.Date;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.*;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Value("${jwt.secret_key}")
    private String encodedKey;

    @Value("${jwt.short_expiration_in_seconds}")
    private int expiration;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequireValidToken.class)) {
                checkAccessToken(request, response);
            }
        }
        return true;
    }

    private void checkAccessToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Cookie jwtAccessCookie = WebUtils.getCookie(request, ACCESS_TOKEN_NAME);
        if (jwtAccessCookie == null) {
            checkRefreshToken(request, response);
        } else {
            String token = jwtAccessCookie.getValue();
            try {
                parser().setSigningKey(encodedKey).parse(token);
            } catch (Exception e) {
                checkRefreshToken(request, response);
            }
        }
    }

    private void checkRefreshToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Cookie jwtRefreshCookie = WebUtils.getCookie(request, REFRESH_TOKEN_NAME);
        if (jwtRefreshCookie == null) {
            throw new AuthenticationException();
        }

        String token = jwtRefreshCookie.getValue();
        Jws<Claims> jws;
        try {
            jws = Jwts.parser().setSigningKey(encodedKey).parseClaimsJws(token);
        } catch (Exception e) {
            throw new AuthenticationException();
        }

        checkBlackList(jws.getBody().getSubject(), jws.getBody().getExpiration());

        generateAccessToken(response, jws.getBody().getSubject());
    }

    private void generateAccessToken(HttpServletResponse response, String subject) {
        String token = Jwts.builder()
                .setSubject(subject)
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .compact();

        Cookie jwtAccessCookie = new Cookie(ACCESS_TOKEN_NAME, token);
        jwtAccessCookie.setHttpOnly(true);
        jwtAccessCookie.setMaxAge(expiration);
        jwtAccessCookie.setPath("/");

        response.addCookie(jwtAccessCookie);
    }

    private void checkBlackList(String subject, Date expiration) {
        // Throw an AuthenticationException for tokens
        // with expiration dates and subjects that have
        // been blacklisted.
    }
}
