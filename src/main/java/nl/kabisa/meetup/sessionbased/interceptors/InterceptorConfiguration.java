package nl.kabisa.meetup.sessionbased.interceptors;

import nl.kabisa.meetup.sessionbased.interceptors.authentication.AuthenticationInterceptor;
import nl.kabisa.meetup.sessionbased.interceptors.csrf.CsrfInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private CsrfInterceptor csrfInterceptor;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(csrfInterceptor).excludePathPatterns("/error");
        registry.addInterceptor(authenticationInterceptor).excludePathPatterns("/error");
    }
}
