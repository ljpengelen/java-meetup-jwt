package nl.kabisa.meetup.jwtbased.interceptors.csrf;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.servlet.http.*;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import nl.kabisa.meetup.jwtbased.interceptors.authentication.RequiresValidJwt;

@Component
public class CsrfInterceptor extends HandlerInterceptorAdapter {

    public static final String CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token";
    public static final String CSRF_TOKEN_COOKIE_NAME = "csrf-token";

    @Value("${jwt.secret_key}")
    private String encodedKey;

    @Value("${csrf.target}")
    private String target;

    private RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
    private HmacUtils hmacUtils;

    @PostConstruct
    public void initialize() {
        this.hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_512, encodedKey);
    }

    private boolean hasValidOriginOrReferer(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (StringUtils.isNotBlank(origin)) {
            return matchesTarget(origin);
        }

        String referer = request.getHeader("Referer");
        if (StringUtils.isNotBlank(referer)) {
            return matchesTarget(referer);
        }

        return false;
    }

    private boolean matchesTarget(String headerValue) {
        try {
            URL targetUrl = new URL(target);
            URL headerUrl = new URL(headerValue);

            return headerUrl.getPort() == targetUrl.getPort() &&
                   headerUrl.getHost().equalsIgnoreCase(targetUrl.getHost()) &&
                   headerUrl.getProtocol().equalsIgnoreCase(targetUrl.getProtocol());
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String hmac(String token) {
        return hmacUtils.hmacHex(token);
    }

    private boolean hasValidCsrfToken(HttpServletRequest request) {
        Cookie csrfCookie = WebUtils.getCookie(request, CsrfInterceptor.CSRF_TOKEN_COOKIE_NAME);
        if (csrfCookie == null) {
            return false;
        }

        String csrfCookieValue = csrfCookie.getValue();

        int separatorIndex = csrfCookieValue.indexOf('.');
        if (separatorIndex == -1) {
            return false;
        }

        String tokenInCookie = csrfCookieValue.substring(0, separatorIndex);
        String hmacInCookie = csrfCookieValue.substring(separatorIndex + 1);

        String hmac = hmac(tokenInCookie);

        String tokenInHeader = request.getHeader(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME);

        return tokenInCookie.equals(tokenInHeader) && hmacInCookie.equals(hmac);
    }

    private String generateToken() {
        return randomDataGenerator.nextSecureHexString(24);
    }

    private void setCsrfTokens(HttpServletResponse response) {
        String token = generateToken();
        response.addHeader(CSRF_TOKEN_HEADER_NAME, token);

        String hmac = hmac(token);

        Cookie cookie = new Cookie(CSRF_TOKEN_COOKIE_NAME, token + "." + hmac);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequiresValidCsrfToken.class) || handlerMethod.hasMethodAnnotation(RequiresValidJwt.class)) {
                if (!hasValidOriginOrReferer(request)) {
                    throw new CsrfException();
                }

                if (!hasValidCsrfToken(request)) {
                    throw new CsrfException();
                }
            }
        }

        setCsrfTokens(response);

        return true;
    }
}
