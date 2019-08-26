package nl.kabisa.meetup.sessionbased.interceptors.csrf;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class CsrfInterceptor extends HandlerInterceptorAdapter {

    public static final String CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token";
    public static final String CSRF_TOKEN_ATTRIBUTE = "__Host-csrfToken";

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

    private void setCsrfTokens(HttpServletRequest request, HttpServletResponse response) {
        String token = generateToken();
        response.addHeader(CSRF_TOKEN_HEADER_NAME, token);

        HttpSession session = request.getSession();
        session.setAttribute(CSRF_TOKEN_ATTRIBUTE, token);
    }

    private boolean hasValidCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return false;
        }

        String csrfAttribute = (String) session.getAttribute(CsrfInterceptor.CSRF_TOKEN_ATTRIBUTE);
        if (csrfAttribute == null) {
            return false;
        }

        String csrfHeaderValue = request.getHeader(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME);

        return csrfAttribute.equals(csrfHeaderValue);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequiresCsrfToken.class)) {
                if (!hasValidOriginOrReferer(request)) {
                    throw new CsrfException();
                }

                if (!hasValidCsrfToken(request)) {
                    throw new CsrfException();
                }
            }
        }

        setCsrfTokens(request, response);

        return true;
    }
}
