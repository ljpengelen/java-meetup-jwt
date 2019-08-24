package nl.kabisa.meetup.sessionbased.interceptors.csrf;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresCsrfToken {}
