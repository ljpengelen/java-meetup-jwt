package nl.kabisa.meetup.sessionbased.interceptors.authentication;

import javax.servlet.http.*;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private boolean hasAccountId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return false;
        }

        return session.getAttribute("accountId") != null;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RequiresSession.class)) {
                if (!hasAccountId(request)) {
                    throw new AuthenticationException();
                }
            }
        }
        return true;
    }
}
