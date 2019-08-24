package nl.kabisa.meetup.jwtbased.interceptors.csrf;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresCsrfProtection {}
