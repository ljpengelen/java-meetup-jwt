package nl.kabisa.meetup.jwtbased.interceptors.authentication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import nl.kabisa.meetup.jwtbased.interceptors.csrf.RequiresCsrfProtection;

@Retention(RetentionPolicy.RUNTIME)
@RequiresCsrfProtection
public @interface RequiresValidJwt {}
