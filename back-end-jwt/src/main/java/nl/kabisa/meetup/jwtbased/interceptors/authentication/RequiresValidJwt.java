package nl.kabisa.meetup.jwtbased.interceptors.authentication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresValidJwt {}
