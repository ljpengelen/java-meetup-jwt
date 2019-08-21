package nl.kabisa.meetup.sessionbased.interceptors.csrf;

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
    public static final String CSRF_TOKEN_ATTRIBUTE = "csrfToken";

    @Value("${csrf.target}")
    private String target;

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

    private void setCsrfTokens(HttpServletRequest request, HttpServletResponse response) {
        String token = generateToken();
        response.addHeader(CSRF_TOKEN_HEADER_NAME, token);

        HttpSession session = request.getSession();
        session.setAttribute(CSRF_TOKEN_ATTRIBUTE, token);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkOrigin(request);
        setCsrfTokens(request, response);

        return true;
    }
}
