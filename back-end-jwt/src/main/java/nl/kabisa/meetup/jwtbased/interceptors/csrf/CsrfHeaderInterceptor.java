package nl.kabisa.meetup.jwtbased.interceptors.csrf;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class CsrfHeaderInterceptor extends HandlerInterceptorAdapter {

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequiresCsrfProtection.class)) {
                if (!hasValidOriginOrReferer(request)) {
                    throw new CsrfException();
                }
            }
        }

        return true;
    }
}
