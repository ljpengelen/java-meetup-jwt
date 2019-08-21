package nl.kabisa.meetup.sessionbased.interceptors.authentication;

import javax.servlet.http.*;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import nl.kabisa.meetup.sessionbased.interceptors.csrf.CsrfException;
import nl.kabisa.meetup.sessionbased.interceptors.csrf.CsrfInterceptor;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private boolean hasValidCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            System.out.println("No session");
            return false;
        }

        String csrfAttribute = (String) session.getAttribute(CsrfInterceptor.CSRF_TOKEN_ATTRIBUTE);
        if (csrfAttribute == null) {
            System.out.println("No CSRF session attribute");
            return false;
        }

        String csrfHeaderValue = request.getHeader(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME);

        System.out.println("Attribute: " + csrfAttribute + ", Header: " + csrfHeaderValue);

        return csrfAttribute.equals(csrfHeaderValue);
    }

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
            if (handlerMethod.hasMethodAnnotation(RequireSession.class)) {
                if (!hasValidCsrfToken(request)) {
                    throw new CsrfException();
                }

                if (!hasAccountId(request)) {
                    throw new AuthenticationException();
                }
            }
        }
        return true;
    }
}
