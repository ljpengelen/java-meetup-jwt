package nl.kabisa.meetup.jwtbased.interceptors.authentication;

import java.time.Instant;
import java.util.Date;

import javax.servlet.http.*;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.*;
import nl.kabisa.meetup.jwtbased.interceptors.csrf.CsrfException;
import nl.kabisa.meetup.jwtbased.interceptors.csrf.RequiresCsrfProtection;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "jwt-refresh";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "jwt-access";

    public static final String CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token";
    public static final String CSRF_TOKEN_COOKIE_NAME = "csrf-token";
    private static final String CSRF_TOKEN_CLAIM_NAME = "csrfToken";

    @Value("${jwt.secret_key}")
    private String encodedKey;

    @Value("${jwt.short_expiration_in_seconds}")
    private int expiration;

    private RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

    private Jws<Claims> getJws(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            return null;
        }

        String token = cookie.getValue();
        try {
            return Jwts.parser().setSigningKey(encodedKey).parseClaimsJws(token);
        } catch (Exception e) {
            return null;
        }
    }

    private void generateAccessToken(String subject, HttpServletResponse response) {
        String token = Jwts.builder()
                .setSubject(subject)
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .compact();

        Cookie jwtAccessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, token);
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

    private String generateToken() {
        return randomDataGenerator.nextSecureHexString(24);
    }

    private void setCsrfTokens(String subject, HttpServletResponse response) {
        String token = generateToken();
        response.addHeader(CSRF_TOKEN_HEADER_NAME, token);

        String tokenWithSubject = Jwts.builder()
                .setSubject(subject)
                .claim(CSRF_TOKEN_CLAIM_NAME, token)
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .compact();

        Cookie cookie = new Cookie(CSRF_TOKEN_COOKIE_NAME, tokenWithSubject);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private boolean hasValidCsrfToken(HttpServletRequest request) {
        Jws<Claims> csrfJws = getJws(request, CSRF_TOKEN_COOKIE_NAME);
        if (csrfJws == null) {
            return false;
        }

        String tokenInJws = csrfJws.getBody().get(CSRF_TOKEN_CLAIM_NAME, String.class);
        String tokenInHeader = request.getHeader(CSRF_TOKEN_HEADER_NAME);

        return tokenInJws.equals(tokenInHeader);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String subject = "anonymous";

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequiresValidJwt.class)) {
                Jws<Claims> refreshToken = getJws(request, REFRESH_TOKEN_COOKIE_NAME);
                if (refreshToken == null) {
                    throw new AuthenticationException();
                }

                subject = refreshToken.getBody().getSubject();

                Jws<Claims> accessToken = getJws(request, ACCESS_TOKEN_COOKIE_NAME);
                if (accessToken != null) {
                    return true;
                }

                if (isBlacklisted(subject, refreshToken.getBody().getExpiration())) {
                    throw new AuthenticationException();
                }

                generateAccessToken(subject, response);
            }

            if (handlerMethod.hasMethodAnnotation(RequiresCsrfProtection.class)) {
                if (!hasValidCsrfToken(request)) {
                    throw new CsrfException();
                }
            }
        }

        setCsrfTokens(subject, response);

        return true;
    }
}
