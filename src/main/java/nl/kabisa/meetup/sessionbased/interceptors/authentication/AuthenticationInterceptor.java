package nl.kabisa.meetup.sessionbased.interceptors.authentication;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Value("${secret_key}")
    private String encodedKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequireValidToken.class)) {
                checkToken(request);
            }
        }
        return true;
    }

    private void checkToken(HttpServletRequest request) throws AuthenticationException {
        Cookie jwtCookie = WebUtils.getCookie(request, "jwt");
        if (jwtCookie == null) {
            throw new AuthenticationException();
        }

        String token = jwtCookie.getValue();
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException();
        }
    }
}
