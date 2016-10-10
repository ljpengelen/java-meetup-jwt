package nl.kabisa.meetup.sessionbased.interceptors.csrf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class CsrfInterceptor extends HandlerInterceptorAdapter {

    private static final String TARGET = "http://localhost";
    private final URL TARGET_URL;

    public CsrfInterceptor() {
        try {
            TARGET_URL = new URL(TARGET);
        } catch (MalformedURLException e) {
            throw new RuntimeException("'" + TARGET + "' is a malformed URL");
        }
    }

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
            URL originUrl = new URL(origin);

            boolean matches = true;
            matches &= originUrl.getPort() == TARGET_URL.getPort();
            matches &= originUrl.getHost().equalsIgnoreCase(TARGET_URL.getHost());
            matches &= originUrl.getProtocol().equalsIgnoreCase(TARGET_URL.getProtocol());

            if (!matches) {
                throw new CsrfException();
            }
        } catch (MalformedURLException e) {
            throw new CsrfException();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkRequestedWithHeader(request);
        checkOrigin(request);

        return true;
    }
}
