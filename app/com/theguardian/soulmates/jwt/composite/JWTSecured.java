package com.theguardian.soulmates.jwt.composite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;

@With(JWTSecuredAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JWTSecured {
	
	public static final String VERIFIED_JWT = "VERIFIED_JWT";
	
}
