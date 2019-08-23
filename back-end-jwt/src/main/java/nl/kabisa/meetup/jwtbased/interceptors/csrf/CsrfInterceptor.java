package nl.kabisa.meetup.jwtbased.interceptors.csrf;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.*;

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

    @Value("${csrf.target}")
    private String target;

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

    private String generateToken() {
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        return randomDataGenerator.nextSecureHexString(24);
    }

    private boolean hasValidCsrfToken(HttpServletRequest request) {
        Cookie csrfCookie = WebUtils.getCookie(request, CsrfInterceptor.CSRF_TOKEN_COOKIE_NAME);
        if (csrfCookie == null) {
            return false;
        }

        String csrfCookieValue = csrfCookie.getValue();
        String csrfHeaderValue = request.getHeader(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME);
        if (csrfCookieValue.equals(csrfHeaderValue)) {
            return true;
        }

        return false;
    }

    private void setCsrfTokens(HttpServletResponse response) {
        String token = generateToken();
        response.addHeader(CSRF_TOKEN_HEADER_NAME, token);

        Cookie cookie = new Cookie(CSRF_TOKEN_COOKIE_NAME, token);
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
