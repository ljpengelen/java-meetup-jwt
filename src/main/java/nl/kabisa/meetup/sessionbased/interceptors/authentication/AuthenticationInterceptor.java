package nl.kabisa.meetup.sessionbased.interceptors.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Date;
import java.time.Instant;

import static io.jsonwebtoken.Jwts.parser;

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
                checkShortToken(request, response);
            }
        }
        return true;
    }

    private void checkShortToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Cookie jwtShortCookie = WebUtils.getCookie(request, "jwt-short");
        if (jwtShortCookie == null) {
            checkLongToken(request, response);
        } else {
            String token = jwtShortCookie.getValue();
            try {
                parser().setSigningKey(encodedKey).parse(token);
            } catch (Exception e) {
                checkLongToken(request, response);
            }
        }
    }

    private void checkLongToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Cookie jwtLongCookie = WebUtils.getCookie(request, "jwt-long");
        if (jwtLongCookie == null) {
            throw new AuthenticationException();
        }

        String token = jwtLongCookie.getValue();
        String subject;
        try {
            subject = Jwts.parser().setSigningKey(encodedKey).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            throw new AuthenticationException();
        }

        generateShortToken(response, subject);
    }

    private void generateShortToken(HttpServletResponse response, String subject) {
        String token = Jwts.builder()
                .setSubject(subject)
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .compact();

        Cookie jwtShortCookie = new Cookie("jwt-short", token);
        jwtShortCookie.setHttpOnly(true);
        jwtShortCookie.setMaxAge(expiration);
        jwtShortCookie.setPath("/");

        response.addCookie(jwtShortCookie);
    }
}
