package nl.kabisa.meetup.jwtbased.interceptors.authentication;

import static nl.kabisa.meetup.jwtbased.TokenNames.ACCESS_TOKEN_NAME;
import static nl.kabisa.meetup.jwtbased.TokenNames.REFRESH_TOKEN_NAME;

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

    private class RefreshToken {

        private final String token;

        private Jws<Claims> jws;

        public RefreshToken(String token) {
            this.token = token;
            parseToken();
        }

        private void parseToken() {
            try {
                jws = Jwts.parser().setSigningKey(encodedKey).parseClaimsJws(token);
            } catch (Exception e) {
            }
        }

        public boolean isValid() {
            return jws != null;
        }

        public String getSubject() {
            return jws.getBody().getSubject();
        }

        public Date getExpiration() {
            return jws.getBody().getExpiration();
        }
    }

    private boolean hasValidAccessToken(HttpServletRequest request) {
        Cookie jwtAccessCookie = WebUtils.getCookie(request, ACCESS_TOKEN_NAME);

        if (jwtAccessCookie == null) {
            return false;
        }

        String token = jwtAccessCookie.getValue();
        try {
            Jwts.parser().setSigningKey(encodedKey).parse(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void generateAccessToken(String subject, HttpServletResponse response) {
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

    private boolean isBlacklisted(String subject, Date expiration) {
        // Return true for tokens
        // with expiration dates and subjects that have
        // been blacklisted.
        return false;
    }

    private void validateTokens(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (hasValidAccessToken(request)) {
            return;
        }

        Cookie jwtRefreshCookie = WebUtils.getCookie(request, REFRESH_TOKEN_NAME);
        if (jwtRefreshCookie == null) {
            throw new AuthenticationException();
        }

        RefreshToken refreshToken = new RefreshToken(jwtRefreshCookie.getValue());
        if (!refreshToken.isValid()) {
            throw new AuthenticationException();
        }

        if (isBlacklisted(refreshToken.getSubject(), refreshToken.getExpiration())) {
            throw new AuthenticationException();
        }

        generateAccessToken(refreshToken.getSubject(), response);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequiresValidJwt.class)) {
                validateTokens(request, response);
            }
        }
        return true;
    }
}
