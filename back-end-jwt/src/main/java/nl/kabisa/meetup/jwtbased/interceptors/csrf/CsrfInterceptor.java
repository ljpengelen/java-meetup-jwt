package nl.kabisa.meetup.jwtbased.interceptors.csrf;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class CsrfInterceptor extends HandlerInterceptorAdapter {

    public static final String CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token";
    public static final String CSRF_TOKEN_COOKIE_NAME = "csrf-token";

    @Value("${csrf.target}")
    private String target;

    private void checkRequestedWithHeader(HttpServletRequest request) throws CsrfException {
        String requestedWith = request.getHeader("X-Requested-With");
        if (!"XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            throw new CsrfException();
        }
    }

    private void checkOrigin(HttpServletRequest request) throws CsrfException {
        String origin = request.getHeader("Origin");
        if (StringUtils.isNotBlank(origin)) {
            compareWithTarget(origin);
            return;
        }

        String referer = request.getHeader("Referer");
        if (StringUtils.isNotBlank(referer)) {
            compareWithTarget(referer);
            return;
        }

        throw new CsrfException();
    }

    private void compareWithTarget(String origin) throws CsrfException {
        try {
            URL targetUrl = new URL(target);
            URL originUrl = new URL(origin);

            boolean matches = true;
            matches &= originUrl.getPort() == targetUrl.getPort();
            matches &= originUrl.getHost().equalsIgnoreCase(targetUrl.getHost());
            matches &= originUrl.getProtocol().equalsIgnoreCase(targetUrl.getProtocol());

            if (!matches) {
                throw new CsrfException();
            }
        } catch (MalformedURLException e) {
            throw new CsrfException();
        }
    }

    private String generateToken() {
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        return randomDataGenerator.nextSecureHexString(24);
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
        checkRequestedWithHeader(request);
        checkOrigin(request);

        setCsrfTokens(response);

        return true;
    }
}
