package nl.kabisa.meetup.sessionbased.interceptors.authentication;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequireSession.class)) {
                checkSession(request);
            }
        }
        return true;
    }

    private void checkSession(HttpServletRequest request) throws AuthenticationException {
        HttpSession session = request.getSession();
        if (session == null) {
            throw new AuthenticationException();
        }

        if (session.getAttribute("accountId") == null) {
            throw new AuthenticationException();
        }
    }
}
