package nl.kabisa.meetup.sessionbased.interceptors.authentication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import nl.kabisa.meetup.sessionbased.interceptors.csrf.RequiresCsrfToken;

@Retention(RetentionPolicy.RUNTIME)
@RequiresCsrfToken
public @interface RequiresSession {}
